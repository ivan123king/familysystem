package com.lw.familysystem.vo;

import com.lw.config.mybatis.page.PageRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class BookDetailVo {

    private int bookId;

    private String content;

    /**
     * 偏移量
     */
    private Long range;

    /**
     * 内容大小
     */
    private Long bookLength;

}
