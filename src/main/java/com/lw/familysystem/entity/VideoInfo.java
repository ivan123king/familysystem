package com.lw.familysystem.entity;

import com.lw.familysystem.vo.VideoInfoVo;
import lombok.Data;

import java.io.File;
import java.util.Date;

/**
 * video_info
 */
@Data
public class VideoInfo {
    private Integer infoId;

    private String videoName;
    /**
     * 合集、单独
     */
    private String videoType;

    private Integer orderNo;

    private Integer categoryId;

    /**
     * 相对根路径
     */
    private String relativePath;

    private Date createTime;
    private Date updateTime;
    
    public VideoInfo(){}
    
    public VideoInfo(File videoInfoFile,String rootPath){
        this.setVideoType(videoInfoFile.isDirectory() ? VideoInfoVo.Constant.VIDEO_TYPE_HE_JI: VideoInfoVo.Constant.VIDEO_TYPE_DAN_DU);

        String fileName = videoInfoFile.getName();
        if(!videoInfoFile.isDirectory()){
            fileName = fileName.substring(0,fileName.indexOf(".mp4"));
        }
        this.setVideoName(fileName);

        this.setOrderNo(1);//默认值

        String relativePath = videoInfoFile.getAbsolutePath().substring(rootPath.length());
        this.setRelativePath(relativePath);
    }


}
