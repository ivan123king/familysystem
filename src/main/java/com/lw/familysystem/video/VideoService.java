package com.lw.familysystem.video;

import com.coremedia.iso.IsoFile;
import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.cache.FamilyCache;
import com.lw.familysystem.entity.VideoInfo;
import com.lw.familysystem.entity.VideoPhysicsInfo;
import com.lw.familysystem.video.mapper.VideoCategoryMapper;
import com.lw.familysystem.video.mapper.VideoInfoMapper;
import com.lw.familysystem.video.mapper.VideoPhysicsInfoMapper;
import com.lw.familysystem.vo.*;
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
    @Autowired
    private VideoInfoMapper videoInfoMapper;
    @Autowired
    private VideoPhysicsInfoMapper videoPhysicsInfoMapper;

    /**
     * 获取类别
     * @return
     */
    public List<VideoCategoryVo> findAllCategories(){
        return this.categoryMapper.findAllCategories();
    }

    public Page<VideoInfoVo> findVideoInfoByPage(VideoInfo param,PageRequest pageRequest){
        return this.videoInfoMapper.findVideoInfoByPage(param,pageRequest);
    }

    public Page<VideoPhysicsInfoVo> findVideoPhysicsInfoByPage(VideoPhysicsInfo param,PageRequest pageRequest){
        return this.videoPhysicsInfoMapper.findVideoPhysicsInfoByPage(param,pageRequest);
    }

    /**
     * 获取季度信息
     * @param infoId
     * @return
     */
    public List<String> findQuarterInfo(int infoId){
        return this.videoPhysicsInfoMapper.findQuarterInfo(infoId);
    }

    /**
     * 播放视频
     *
     * @param vo
     * @param response
     */
    public void playVideo(int videoId, HttpServletResponse response) {
        VideoPhysicsInfoVo physicsInfoVo = this.videoPhysicsInfoMapper.findPhysicsInfoById(videoId);
        try  {
            OutputStream outputStream = response.getOutputStream();
            String filePath = ROOT_PATH+physicsInfoVo.getRelativePath();
            File file = new File(filePath);

            if (file.exists()) {
                RandomAccessFile targetFile = new RandomAccessFile(file, "r");
                long fileLength = targetFile.length();

                long range = 0;
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
    public void downloadVideo(int videoId,HttpServletResponse response) {
        try (OutputStream outputStream = response.getOutputStream()) {
            VideoPhysicsInfoVo vo = this.videoPhysicsInfoMapper.findPhysicsInfoById(videoId);
            if(vo==null){
                return;
            }
            String filePath = ROOT_PATH+vo.getRelativePath();
            File file = new File(filePath);

            if (file.exists()) {
                RandomAccessFile targetFile = new RandomAccessFile(file, "r");
                long fileLength = targetFile.length();
                //设置响应头，把文件名字设置好
                response.setHeader("Content-Disposition", "attachment; filename=" + vo.getVideoName()+".mp4");
                //设置文件长度
                response.setHeader("Content-Length", String.valueOf(fileLength));
                //解决编码问题
                response.setHeader("Content-Type", "application/octet-stream");
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                byte[] cache = new byte[1024 * 300];
                int flag;
                while ((flag = targetFile.read(cache)) != -1) {
                    outputStream.write(cache, 0, flag);
                }
            } else {
                String message = "文件不存在";
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
