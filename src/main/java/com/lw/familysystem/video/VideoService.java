package com.lw.familysystem.video;

import com.coremedia.iso.IsoFile;
import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.cache.FamilyCache;
import com.lw.familysystem.video.mapper.VideoCategoryMapper;
import com.lw.familysystem.vo.DirectoryInfoVo;
import com.lw.familysystem.vo.HistoryVideoVo;
import com.lw.familysystem.vo.VideoCategoryVo;
import com.lw.familysystem.vo.VideoInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lw
 * @date 2022/10/26 0026
 * @description
 */
@Service("videoService")
@Slf4j
public class VideoService {

    @Value("${video.config.root_path:E:\\temp\\视频}")
    private String ROOT_PATH = "G:\\视频\\其他";

    @Autowired
    private VideoCategoryMapper categoryMapper;

    public List<VideoCategoryVo> findAllCategories(){
        return this.categoryMapper.findAllCategories();
    }

    public Page<VideoCategoryVo> findCategoriesByPage(){
        VideoCategoryVo vo = new VideoCategoryVo();
//        vo.setCategoryName("电影");
        PageRequest pageRequest = new PageRequest(1,3);
        return this.categoryMapper.findCategoriesByPage(vo,pageRequest);
    }

    /**
     * 生成目录信息
     *
     * @return
     */
//    @PostConstruct
    public void createDirectoryTree() {
        DirectoryInfoVo vo = new DirectoryInfoVo();
        this.getAllInfoRecursive(ROOT_PATH, vo);
        vo.putAllInfo2Cache();
    }

    public DirectoryInfoVo getRootDirectoryInfo(){
        return this.getDirectoryInfo(ROOT_PATH,false);
    }

    /**
     * 获取目录信息
     *
     * @param filePath
     * @param refresh
     * @return
     */
    public DirectoryInfoVo getDirectoryInfo(String filePath, boolean refresh) {
        File file = new File(filePath);
        if (!file.isDirectory()) {
            return null;
        }

        DirectoryInfoVo vo = new DirectoryInfoVo();
        if (!refresh && FamilyCache.hasDirInfo(filePath)) {
            vo = FamilyCache.getDirectoryInfo(filePath);
        }
        if (refresh || vo == null) {
            this.getAllInfoRecursive(filePath, vo);
            FamilyCache.refreshDirInfo(filePath, vo);
        }
        return vo;
    }

    /**
     * 递归处理获取目录信息
     *
     * @param dirPath
     * @param vo
     */
    private void getAllInfoRecursive(String dirPath, DirectoryInfoVo vo) {
        File dirFile = new File(dirPath);
        if (dirFile.isDirectory()) {
            vo.setName(dirFile.getName());
            vo.setPath(dirFile.getAbsolutePath());

            File[] files = dirFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    DirectoryInfoVo childDir = new DirectoryInfoVo();
                    getAllInfoRecursive(file.getAbsolutePath(), childDir);
                    vo.addChildDir(childDir);
                } else {
                    VideoInfoVo videoInfoVo = new VideoInfoVo();
                    videoInfoVo.setFilePath(file.getAbsolutePath());
                    videoInfoVo.setName(file.getName());
                    vo.addVideo(videoInfoVo);
                }
            }
        }
    }

    public VideoInfoVo getVideoInfo(String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return null;
        }
        VideoInfoVo vo = new VideoInfoVo();
        try {
            IsoFile isoFile = new IsoFile(filePath);
            long lengthInSeconds =
                    isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                            isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
            vo.setLongTime(lengthInSeconds);
            vo.setSizeB(isoFile.getSize());
            vo.setSizeM(vo.getSizeB() / 1024 / 1024);
            vo.setVideoAllInfo(file);
            if (!FamilyCache.hasVideoInfo(filePath)) {
                FamilyCache.putVideoCache(vo);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return vo;
    }





    /**
     * 播放视频
     *
     * @param vo
     * @param response
     */
    public void playVideo(Map<String, String> otherInfo, VideoInfoVo vo, HttpServletResponse response) {
        String accountName = otherInfo.get("accountName");
        try  {
            OutputStream outputStream = response.getOutputStream();
            String filePath = vo.getFilePath();
            File file = new File(filePath);

            if (file.exists()) {
                RandomAccessFile targetFile = new RandomAccessFile(file, "r");
                long fileLength = targetFile.length();

                long range = 0;
                if(!StringUtils.isEmpty(accountName)){
                    //发现如果跳过某些数据的话，前端video组件无法加载。
//                    HistoryVideoVo history = FamilyCache.getHistoryInfo(accountName, vo);
//                    if(history!=null){
//                       range = history.getRange();
//                    }
                }
                /*
                    解决： java.io.IOException: 你的主机中的软件中止了一个已建立的连接
                    获取服务器视频流时可能存在跨域问题
                 */
                response.addHeader("Access-Control-Allow-Origin","*");
                //设置内容类型
                response.setHeader("Content-Type", "video/mp4");
                //设置此次相应返回的数据长度
                response.setHeader("Content-Length", String.valueOf(fileLength - range));
                //设置此次相应返回的数据范围
                response.setHeader("Content-Range", "bytes " + range + "-" + (fileLength - 1) + "/" + fileLength);
                //返回码需要为206，而不是200
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                //设定文件读取开始位置（以字节为单位）
                targetFile.seek(range);
                byte[] cache = new byte[1024 * 300];
                int flag;
                while ((flag = targetFile.read(cache)) != -1) {
                    try {
                        outputStream.write(cache, 0, flag);
                    } catch (IOException ioe) {
                /*
                经过测试发现，前端使用video会调用三次playVideo方法，
                前两次第一次应该是探测是否能连接
                第二次是获取视频长度
                第三次是获取视频流，在第三次时会报 远程主机强制关闭了一个连接，应该是给客户端传递数据后，客户端主动关闭了连接
                 */
                    }
                }
            } else {
                String message = "file: not exists";
                //解决编码问题
                response.setHeader("Content-Type", "application/json");
                outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            }
            try{
                outputStream.flush();
            }catch (IOException ioe){
                /*
                经过测试发现，前端使用video会调用三次playVideo方法，
                前两次第一次应该是探测是否能连接
                第二次是获取视频长度
                第三次是获取视频流，在第三次时会报 远程主机强制关闭了一个连接，应该是给客户端传递数据后，客户端主动关闭了连接
                 */
            }
        } catch (IOException e) {
            log.error("", e);
        }
    }

    /**
     * 下载视频
     *
     * @param otherInfo
     * @param vo
     * @param response
     */
    public void downloadVideo(Map<String, String> otherInfo, VideoInfoVo vo, HttpServletResponse response) {
        try (OutputStream outputStream = response.getOutputStream()) {
            String fileName = vo.getName();
            String filePath = vo.getFilePath();
            File file = new File(filePath);

            if (file.exists()) {
                RandomAccessFile targetFile = new RandomAccessFile(file, "r");
                long fileLength = targetFile.length();
                //设置响应头，把文件名字设置好
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                //设置文件长度
                response.setHeader("Content-Length", String.valueOf(fileLength));
                //解决编码问题
                response.setHeader("Content-Type", "application/octet-stream");

                byte[] cache = new byte[1024 * 300];
                int flag;
                while ((flag = targetFile.read(cache)) != -1) {
                    outputStream.write(cache, 0, flag);
                }
            } else {
                String message = "file: not exists";
                //解决编码问题
                response.setHeader("Content-Type", "application/json");
                outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();
        } catch (IOException e) {
            log.error("", e);
        }
    }

    public static void main(String[] args) {
        VideoService vs = new VideoService();
//        vs.getVideoInfo();
        String rootPath = "G:\\视频\\其他";
//        vs.refreshVideoIntoDb(rootPath);
//        vs.refreshStructIntoDb(rootPath);

//        vs.createDirectoryTree();

//        String videoPath = "G:\\视频\\其他\\B站UP主\\紫颜-小仙紫\\- 三生烟火 一世迷离 -_高清 1080P.mp4";
//        VideoInfoVo vo = vs.getVideoInfo(videoPath);
//        log.info("" + vo.getSizeM());

//        log.info(""+"dadfsd".hashCode());
//        log.info(""+"dadfsd".hashCode());
//        log.info(""+"dadfsc".hashCode());

        String inputFilePath = "E:\\temp\\视频\\23.mp4";
        String outFilePath = "E:\\temp\\转码视频\\zhuanma2.mp4";

        VideoUtils.encode(inputFilePath,outFilePath);

    }
}
