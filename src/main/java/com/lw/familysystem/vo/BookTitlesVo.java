package com.lw.familysystem.vo;

import com.lw.familysystem.entity.BookTitles;
import lombok.Data;

@Data
public class BookTitlesVo extends BookTitles {
    private String createTimeFmt;
    private String updateTimeFmt;
}
