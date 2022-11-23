package com.lw.familysystem.video.mapper;

import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.entity.VideoPhysicsInfo;
import com.lw.familysystem.vo.VideoPhysicsInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoPhysicsInfoMapper {

    void refreshVideoPhysicsInfo2DB(@Param("vos") List<VideoPhysicsInfo> videoPhysicsInfos);
    void addVideoPhysicsInfo(@Param("vo")VideoPhysicsInfo videoPhysicsInfo);
    void updateVideoPhysicsInfo(@Param("vo")VideoPhysicsInfo videoPhysicsInfo);

    Page<VideoPhysicsInfoVo> findVideoPhysicsInfoByPage(@Param("vo")VideoPhysicsInfo videoPhysicsInfo, PageRequest pageRequest);

    VideoPhysicsInfoVo findPhysicsInfoById(@Param("id")int id);

    @Select("select distinct t.quarter_info from video_physics_info t where t.info_id = #{infoId} and t.quarter_info is not null")
    List<String> findQuarterInfo(@Param("infoId")int infoId);

    /**
     * 相对路径肯定是唯一的，因为只要加上根路径就能找到这个视频硬盘地址
     * @param relativePath
     * @return
     */
    VideoPhysicsInfoVo findPhysicsInfoByRelativePath(@Param("relativePath")String relativePath);
}
