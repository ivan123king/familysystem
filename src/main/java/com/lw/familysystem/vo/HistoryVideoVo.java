package com.lw.familysystem.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lw
 * @date 2022/10/28 0028
 * @description  视频历史
 */
@Slf4j
@Data
public class HistoryVideoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录人
     */
    private String accountName;

    /**
     * 视频信息
     */
    private VideoInfoVo videoInfoVo;

    /**
     * 播放进度
     */
    private long range = 0;

    /**
     * 时间
     */
    @Setter(AccessLevel.NONE)
    private Date historyTime = new Date();
    private String historyTimeFmt = "";

    public void setHistoryTime(Date historyTime){
        this.historyTime = historyTime;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.historyTimeFmt = df.format(this.historyTime);
    }
}
