package com.lw.familysystem.vo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 书籍信息
 */
@Data
@Slf4j
public class BookInfoVo {

    /**
     * key: 章节标题  value: file的range
     */
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Map<String,Long> titles = new HashMap<>();


    /**
     * 文件长度，对应 range
     */
    private long bookLength;

    /**
     * 文件大小，对应 M
     */
    private double bookSize;

    private String relativePath;

    public void setBookLength(long bookLength){
        this.bookLength = bookLength;
        this.bookSize = this.bookLength*1.0/1000/1000;
    }

    public void addTitle(String title,long range){
        this.titles.put(title,range);
    }

    public long getTitleRange(String title){
        Long rangeT = this.titles.get(title);
        if(rangeT==null){
            rangeT = 0L;
        }
        return rangeT;
    }

    @Override
    public String toString() {
        return "BookInfoVo{" +
                "titles=" + titles.size() +
                ", bookLength=" + bookLength +
                ", bookSize=" + bookSize +
                ", relativePath='" + relativePath + '\'' +
                '}';
    }
}
