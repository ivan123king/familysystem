package com.lw.familysystem.entity;

import com.lw.familysystem.vo.VideoInfoVo;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.File;
import java.util.Date;

@Data
public class BookInfo {
    private int book_id ;
    private String book_name ;
    /**
     * 类型，多个类型用逗号分割
     */
    private String book_type ;

    /**
     * 文件大小 单位M
     */
    @Setter(AccessLevel.NONE)
    private double book_size ;

    /**
     * 文件长度
     */
    private long book_length ;
    private String relative_path ;
    private Date create_time ;
    private Date update_time ;

    public void setBook_length(long book_length) {
        this.book_length = book_length;
        this.book_size = this.book_length*1.0/1000/1000;
    }


    public BookInfo(){}

    public BookInfo(File bookFile, String rootPath){

        String fileName = bookFile.getName();
        fileName = fileName.substring(0,fileName.indexOf(".txt"));
        this.setBook_name(fileName);

        this.setBook_length(bookFile.length());

        String relativePath = bookFile.getAbsolutePath().substring(rootPath.length());
        this.setRelative_path(relativePath);
    }
}
