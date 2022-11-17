package com.lw.familysystem.vo;

import com.lw.familysystem.cache.FamilyCache;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lw
 * @date 2022/10/28 0028
 * @description 目录信息
 */
@Data
@Slf4j
public class DirectoryInfoVo implements Serializable {

    private static final long serialVersionUID = 4L;

    private String name;
    private String path;

    /**
     * 目录信息
     */
    @Setter(AccessLevel.NONE)
    private List<DirectoryInfoVo> childDirList = new ArrayList<>();

    /**
     * 本目录下视频信息
     */
    @Setter(AccessLevel.NONE)
    private List<VideoInfoVo> videoInfoList = new ArrayList<>();

    public void addChildDir(DirectoryInfoVo vo){
        this.childDirList.add(vo);
    }

    public void addVideo(VideoInfoVo vo){
        this.videoInfoList.add(vo);
    }

    /**
     * 将所有数据放入缓存
     */
    public void putAllInfo2Cache(){
        putAllInfo2CacheRecursive(this);
    }

    /**
     * 递归处理
     * @param vo
     */
    private void putAllInfo2CacheRecursive(DirectoryInfoVo vo){
        FamilyCache.putDirCache(vo);
        for (VideoInfoVo videoInfoVo : vo.videoInfoList) {
            FamilyCache.putVideoCache(videoInfoVo);
        }
        for (DirectoryInfoVo directoryInfoVo : vo.childDirList) {
            putAllInfo2CacheRecursive(directoryInfoVo);
        }
    }

    /**
     * 打印所有信息
     */
    public void printAllInfo(){
        this.printRecursive(this);
    }

    /**
     * 递归处理
     * @param vo
     */
    private void printRecursive(DirectoryInfoVo vo){
        log.info(vo.getName()+"-->"+vo.getPath());
        for (VideoInfoVo videoInfoVo : vo.videoInfoList) {
            log.info(videoInfoVo.getName()+"-->"+videoInfoVo.getFilePath());
        }
        log.info("视频信息-----------------end");
        for (DirectoryInfoVo directoryInfoVo : vo.childDirList) {
            printRecursive(directoryInfoVo);
        }
    }

}
