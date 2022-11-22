package com.lw.familysystem.entity;

import lombok.Data;

import java.util.Date;

/**
 * 播放历史
 */
@Data
public class VideoPlayHistory {
    private Integer historyId;
    private Integer videoId;
    private String accountName;
    private Long playTime;
    private String playTimeVis;

    private Date createTime;
    private Date updateTime;
}
