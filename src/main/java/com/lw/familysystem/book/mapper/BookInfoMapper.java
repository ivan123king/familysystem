package com.lw.familysystem.book.mapper;

import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.entity.BookInfo;
import com.lw.familysystem.vo.BookInfoVo;
import com.lw.familysystem.vo.BookInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BookInfoMapper {

    /**
     * 批量刷数据用的
     * @param vo
     */
    void refreshBookInfo2DB(@Param("vos") List<BookInfo> vo);

    void addBookInfo(@Param("vo") BookInfo vo);

    /**
     * 分页查询
     * @param vo
     * @return
     */
    Page<BookInfoVo> findBookInfoByPage(@Param("vo") BookInfo vo, PageRequest pageRequest);

    @Select("select t.* from book_info t where t.book_id = #{id}")
    BookInfoVo findBookById(@Param("id")int id);

    @Select("select t.* from book_info t where t.relative_path = #{relative_path}")
    BookInfoVo findBookByRelativePath(@Param("relative_path")String relativePath);

    @Select("select t.* from book_info t ")
    List<BookInfoVo> findAllBookInfo();
}
