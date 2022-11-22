package com.lw.familysystem.vo;

import com.lw.familysystem.entity.VideoPlayHistory;
import lombok.Data;

@Data
public class VideoPlayHistoryVo extends VideoPlayHistory {

    private String createTimeFmt;
    private String updateTimeFmt;

    private VideoPhysicsInfoVo videoPhysicsInfoVo;
    private VideoInfoVo videoInfoVo;
}
