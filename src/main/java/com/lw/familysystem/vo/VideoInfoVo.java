package com.lw.familysystem.vo;

import com.lw.familysystem.entity.VideoInfo;
import lombok.Data;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * @author lw
 * @date 2022/10/26 0026
 * @description
 */
@Data
public class VideoInfoVo extends VideoInfo implements Serializable {

    private String createTimeFmt;
    private String updateTimeFmt;

    private static final long serialVersionUID = 2L;

    private String name;//名字
    private long longTime;//时长 单位 秒
    private long sizeB;//大小，单位 B
    private long sizeM;//大小 单位M
    private int vId;
    private String filePath;//文件路径

    private List<DanmuVo> danMus = new ArrayList<>();//弹幕信息

    public void setFilePath(String filePath){
        this.filePath = filePath;
        this.vId = this.filePath.hashCode();
    }

    public void setVideoAllInfo(File file){
        if(file!=null){
            this.setName(file.getName());
            this.setFilePath(file.getAbsolutePath());
            this.vId = this.filePath.hashCode();
        }
    }

    public void addDanmu(DanmuVo danmu){
        if(danMus.size()>20){
            danMus.clear();
        }
        danMus.add(danmu);
    }

    public static class Constant{
        public static final String VIDEO_TYPE_DAN_DU = "单独";
        public static final String VIDEO_TYPE_HE_JI = "合集";
    }
}
