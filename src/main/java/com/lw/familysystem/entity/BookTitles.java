package com.lw.familysystem.entity;

import lombok.Data;

/**
 * 图书章节
 */
@Data
public class BookTitles {

    private int title_id ;
    private String title_name ;

    private int order_no;
    /**
     * 章节对应文件位置
     */
    private long title_range ;
    private int book_id ;
    private String create_time ;
    private String update_time ;

}
