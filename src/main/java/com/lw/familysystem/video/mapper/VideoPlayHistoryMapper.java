package com.lw.familysystem.video.mapper;

import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.entity.VideoPlayHistory;
import com.lw.familysystem.vo.VideoPlayHistoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VideoPlayHistoryMapper {

    void addVideoHistory(@Param("vo") VideoPlayHistory history);

    void updateVideoHistory(@Param("vo") VideoPlayHistory history);

    Page<VideoPlayHistoryVo> findVideoHistoryByPage(@Param("vo")VideoPlayHistory history, PageRequest pageRequest);

    VideoPlayHistoryVo findVideoHistoryByVideoId(@Param("videoId")int videoId,@Param("accountName")String accountName);
}
