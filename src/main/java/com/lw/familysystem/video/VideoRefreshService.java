package com.lw.familysystem.video;

import com.lw.familysystem.entity.VideoInfo;
import com.lw.familysystem.entity.VideoPhysicsInfo;
import com.lw.familysystem.video.mapper.VideoCategoryMapper;
import com.lw.familysystem.video.mapper.VideoInfoMapper;
import com.lw.familysystem.video.mapper.VideoPhysicsInfoMapper;
import com.lw.familysystem.vo.VideoCategoryVo;
import com.lw.familysystem.vo.VideoInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * 刷数据专用
 */
@Service("videoRefreshService")
@Slf4j
public class VideoRefreshService {

    @Value("${video.config.root_path:E:\\temp\\视频}")
    private static String ROOT_PATH = "E:\\temp\\视频";

    @Autowired
    private VideoCategoryMapper categoryMapper;
    @Autowired
    private VideoInfoMapper videoInfoMapper;
    @Autowired
    private VideoPhysicsInfoMapper videoPhysicsInfoMapper;

    /**
     * 第一层结构，类别
     */
    public void refreshCategoryInfo2Db() {
        List<VideoCategoryVo> categoryVos = this.categoryMapper.findAllCategories();
        File rootFile = new File(ROOT_PATH);
        if (rootFile.exists()) {
            FileFilter fileFilter = new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory();
                }
            };
            File[] categoryFiles = rootFile.listFiles(fileFilter);//只罗列出目录

            for (File categoryFile : categoryFiles) {
                String fileName = categoryFile.getName();

                boolean hasIn = false;//是否已经存在于DB中
                for (VideoCategoryVo categoryVo : categoryVos) {
                    if (fileName.equals(categoryVo.getCategoryName())) {
                        hasIn = true;
                        break;
                    }
                }

                if (!hasIn) {//数据库没有的才添加
                    VideoCategoryVo categoryVo = new VideoCategoryVo();
                    categoryVo.setCategoryName(fileName);
                    this.categoryMapper.addCategory(categoryVo);
                }
            }
        }
    }

    /**
     * 第二层结构：视频信息
     * 将视频信息保存到video_info表
     * 仅仅是视频名称等，不包含物理地址
     */
    public void refreshVideoInfo2Db() {
        List<VideoCategoryVo> categoryVos = this.categoryMapper.findAllCategories();

        List<VideoInfo> videoInfos = new ArrayList<>();

        /**
         * 1. 获取video_category中类别
         * 2. 将类别目录下的信息直接当做视频信息写入video_info表
         */
        for (VideoCategoryVo categoryVo : categoryVos) {
            String categoryName = categoryVo.getCategoryName();
            String filePath = ROOT_PATH + File.separator + categoryName;

            File videoInfoRootFile = new File(filePath);
            File[] videoInfoFiles = videoInfoRootFile.listFiles();
            for (File videoInfoFile : videoInfoFiles) {
                VideoInfo videoInfo = new VideoInfo(videoInfoFile,ROOT_PATH);
                videoInfo.setCategoryId(categoryVo.getCategoryId());
                videoInfos.add(videoInfo);

                if (videoInfos.size() > 100) {//超过指定数据就刷入数据库
                    this.videoInfoMapper.refreshVideoInfo2DB(videoInfos);
                    videoInfos = new ArrayList<>();
                }
            }
        }

        if (videoInfos.size() > 0) {//将剩余的数据刷入数据库
            this.videoInfoMapper.refreshVideoInfo2DB(videoInfos);
        }
    }

    /**
     * 第三层结构：物理视频
     * 将硬盘中的视频信息刷入数据库
     */
    public void refreshVideoPhysicsInfo2Db() {
        List<VideoInfo> videoInfos = this.videoInfoMapper.findAllVideoInfo();

        List<VideoPhysicsInfo> physicsInfos = new ArrayList<>();

        /**
         * 1.将所有video_info表的拉出来
         * 2. 分为三种类型，第一种是video_info中是单独，也就是是视频
         *          第二种，目录下面直接是视频
         *          第三种，目录下面还有第几季
         */
        for (VideoInfo videoInfo : videoInfos) {
            //如果是单独的，其实就是mp4文件，就没必要再解析一遍
            if(videoInfo.getVideoType().equals(VideoInfoVo.Constant.VIDEO_TYPE_DAN_DU)){
                VideoPhysicsInfo physicsInfo = new VideoPhysicsInfo(videoInfo);
                physicsInfos.add(physicsInfo);
            }else{//如果是合集，则需要去解析
                String filePath = ROOT_PATH+videoInfo.getRelativePath();
                File videoRootFile = new File(filePath);
                File[] videos = videoRootFile.listFiles();
                for (File video : videos) {
                    if(video.isFile()){//第一种，直接下面是mp4文件
                        VideoPhysicsInfo physicsInfo = new VideoPhysicsInfo(video,ROOT_PATH);
                        physicsInfo.setInfoId(videoInfo.getInfoId());
                        physicsInfos.add(physicsInfo);
                    }else if(video.isDirectory()){//第二种，还有一层目录
                        File[] videosInner = video.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File file) {
                                return file.isFile();
                            }
                        });

                        for (File videoInner : videosInner) {
                            VideoPhysicsInfo physicsInfo = new VideoPhysicsInfo(videoInner,ROOT_PATH);
                            physicsInfo.setInfoId(videoInfo.getInfoId());
                            physicsInfo.setQuarterInfo(video.getName());
                            physicsInfos.add(physicsInfo);
                        }
                    }
                }

            }

            if (physicsInfos.size() > 100) {//超过指定数据就刷入数据库
                this.videoPhysicsInfoMapper.refreshVideoPhysicsInfo2DB(physicsInfos);
                physicsInfos = new ArrayList<>();
            }
        }

        if (physicsInfos.size() > 0) {
            this.videoPhysicsInfoMapper.refreshVideoPhysicsInfo2DB(physicsInfos);
        }
    }

    private void getDirInfoRecursive(String dirPath, List<VideoInfoVo> videos) {
        File dirFile = new File(dirPath);
        if (dirFile.isDirectory()) {
            VideoInfoVo videoInfoVo = new VideoInfoVo();
            videoInfoVo.setFilePath(dirFile.getAbsolutePath());
            videoInfoVo.setName(dirFile.getName());
            videos.add(videoInfoVo);

            File[] files = dirFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    getDirInfoRecursive(file.getAbsolutePath(), videos);
                }
            }
        }
    }

    private void getFileInfoRecursive(String dirPath, List<VideoInfoVo> videos) {
        File dirFile = new File(dirPath);
        if (dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    getFileInfoRecursive(file.getAbsolutePath(), videos);
                } else {
                    VideoInfoVo videoInfoVo = new VideoInfoVo();
                    videoInfoVo.setFilePath(file.getAbsolutePath());
                    videoInfoVo.setName(file.getName());
                    videos.add(videoInfoVo);
                }
            }
        }
    }
}
