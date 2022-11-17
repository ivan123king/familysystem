package com.lw.familysystem.entity;

import lombok.Data;

import java.util.Date;

@Data
public class VideoCategory {

    private Integer categoryId;

    private String categoryName;

    private Date createTime;
    private Date updateTime;
}
