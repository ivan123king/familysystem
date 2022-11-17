package com.lw.config.mybatis.page;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页参数
 */
@Data
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -2464407342708149892L;
    /**
     *页码（从0开始）
     */
    private  int page;
    /**
     *每页显示数量
     */
    private  int size;

    public PageRequest() {
        this(0, 10);
    }
    public PageRequest(int page, int size) {
        if (page < 0) {
            page = 0;
        }
        if (size < 0) {
            size = 0;
        }
        this.page = page;
        this.size = size;
    }
    public int getOffset() {
        return this.page * this.size;
    }
}
