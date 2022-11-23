package com.lw.familysystem.entity;

import com.lw.familysystem.video.VideoUtils;
import lombok.Data;

import java.io.File;
import java.util.Date;

@Data
public class VideoPhysicsInfo {
    private Integer videoId;
    private String videoName;
    private Integer infoId;
    /**
     * 第几季度
     */
    private String quarterInfo;

    private Integer orderNo;

    private String relativePath;

    /**
     * 视频时长
     */
    private Long videoLongTime;

    /**
     * 视频时长可视化 00:00:00
     */
    private String videoLongTimeVis;

    private Date createTime;
    private Date updateTime;

    public VideoPhysicsInfo(){

    }

    public VideoPhysicsInfo(File file,String rootPath){
        if(file!=null&&file.isFile()){
            String fileName = file.getName();
            fileName = fileName.substring(0,fileName.indexOf(".mp4"));
            this.videoName = fileName;
            this.orderNo = 1;
            this.relativePath = file.getAbsolutePath().substring(rootPath.length());

            long videoLongTime = VideoUtils.getMp4Duration(rootPath + relativePath);
            this.setVideoLongTime(videoLongTime);
            this.setVideoLongTimeVis(VideoUtils.getVideoTimeFormat(videoLongTime));
        }
    }

    public VideoPhysicsInfo(VideoInfo videoInfo,String rootPath){
        this.setVideoName(videoInfo.getVideoName());
        this.setInfoId(videoInfo.getInfoId());
        this.setOrderNo(1);
        this.setRelativePath(videoInfo.getRelativePath());

        long videoLongTime = VideoUtils.getMp4Duration(rootPath + relativePath);
        this.setVideoLongTime(videoLongTime);
        this.setVideoLongTimeVis(VideoUtils.getVideoTimeFormat(videoLongTime));
    }
}
