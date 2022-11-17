package com.lw.familysystem.video.mapper;

import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.entity.VideoCategory;
import com.lw.familysystem.vo.VideoCategoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类别
 */
@Mapper
public interface VideoCategoryMapper {

    void addCategory(@Param("vo") VideoCategory vo);
    List<VideoCategoryVo> findAllCategories();
    Page<VideoCategoryVo> findCategoriesByPage(@Param("vo") VideoCategoryVo vo2,PageRequest pageRequest);
}
