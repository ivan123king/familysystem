package com.lw.familysystem.video.mapper;


import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.entity.VideoInfo;
import com.lw.familysystem.vo.VideoInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoInfoMapper {

    /**
     * 批量刷数据用的
     * @param vo
     */
    void refreshVideoInfo2DB(@Param("vos") List<VideoInfo> vo);

    void addVideoInfo(@Param("vo") VideoInfo vo);

    /**
     * 分页查询
     * @param vo
     * @return
     */
    Page<VideoInfoVo> findVideoInfoByPage(@Param("vo") VideoInfo vo, PageRequest pageRequest);

    VideoInfoVo findVideoInfoById(@Param("id")int id);

    List<VideoInfo> findAllVideoInfo();

}
