package com.lw.familysystem.vo;

import com.lw.familysystem.entity.VideoCategory;
import lombok.Data;

import java.util.Date;

@Data
public class VideoCategoryVo extends VideoCategory {
    private String createTimeFmt;
    private String updateTimeFmt;
}
