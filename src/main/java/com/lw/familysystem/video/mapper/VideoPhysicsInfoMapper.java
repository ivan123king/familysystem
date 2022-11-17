package com.lw.familysystem.video.mapper;

import com.lw.familysystem.entity.VideoPhysicsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VideoPhysicsInfoMapper {

    void refreshVideoPhysicsInfo2DB(@Param("vos") List<VideoPhysicsInfo> videoPhysicsInfos);
    void addVideoPhysicsInfo(@Param("vo")VideoPhysicsInfo videoPhysicsInfo);
}
