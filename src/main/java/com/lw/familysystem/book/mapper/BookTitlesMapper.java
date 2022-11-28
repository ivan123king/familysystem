package com.lw.familysystem.book.mapper;

import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.entity.BookTitles;
import com.lw.familysystem.vo.BookTitlesVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 *
 */
@Mapper
public interface BookTitlesMapper {

    /**
     * 批量刷数据用的
     * @param vo
     */
    void refreshBookTitles2DB(@Param("vos") List<BookTitles> vo);

    void addBookTitles(@Param("vo") BookTitles vo);

    /**
     * 分页查询
     * @param vo
     * @return
     */
    Page<BookTitlesVo> findBookTitlesByPage(@Param("vo") BookTitles vo, PageRequest pageRequest);

    @Select("select t.* from book_titles t where t.title_id = #{id}")
    BookTitlesVo findBookTitlesById(@Param("id")int id);

    @Select("select t.* from book_titles t where t.book_id = #{bookId} order by t.create_time")
    List<BookTitlesVo> findBookTitlesByBookId(@Param("bookId")int bookId);

    @Delete("delete from book_titles where book_id = #{bookId}")
    void deleteBookTitlesByBookId(@Param("bookId")int bookId);
}
