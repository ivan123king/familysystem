package com.lw.familysystem.vo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 弹幕
 */
@Data
@Slf4j
public class DanmuVo implements Serializable {

    private static final long serialVersionUID = 3L;

    private String content;
    private String accountName;
    private long videoTime;//发送在视频中的时间
    private String videoId;
}
