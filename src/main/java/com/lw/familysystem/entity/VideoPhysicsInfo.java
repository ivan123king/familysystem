package com.lw.familysystem.entity;

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
        }
    }

    public VideoPhysicsInfo(VideoInfo videoInfo){
        this.setVideoName(videoInfo.getVideoName());
        this.setInfoId(videoInfo.getInfoId());
        this.setOrderNo(1);
        this.setRelativePath(videoInfo.getRelativePath());
    }
}
