package com.lw.familysystem.vo;

import com.lw.familysystem.entity.VideoPhysicsInfo;
import lombok.Data;

@Data
public class VideoPhysicsInfoVo extends VideoPhysicsInfo {
    private String createTimeFmt;
    private String updateTimeFmt;
}
