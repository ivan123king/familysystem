package com.lw.config.mybatis.page;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页封装返回
 * @param <T>
 */
@Data
public class Page<T> implements Serializable {
    private static final long serialVersionUID = 1625981207349025919L;
    //查询结果集
    private final List<T> content;
    //分页参数
    private  PageRequest pageRequest;
    //总记录数
    private  int total;
    public Page(List<T> content, PageRequest pageRequest, int total) {
        this.content = new ArrayList();
        if (null == content) {
            throw new IllegalArgumentException("Content must not be null!");
        } else {
            this.content.addAll(content);
            this.total = total;
            this.pageRequest = pageRequest;
        }
    }
}