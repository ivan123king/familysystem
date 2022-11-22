package com.lw.familysystem.video;

import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.entity.VideoPlayHistory;
import com.lw.familysystem.video.mapper.VideoPhysicsInfoMapper;
import com.lw.familysystem.video.mapper.VideoPlayHistoryMapper;
import com.lw.familysystem.vo.VideoPhysicsInfoVo;
import com.lw.familysystem.vo.VideoPlayHistoryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 历史记录和弹幕信息
 */
@Service("videoHistoryAndDanmuService")
@Slf4j
public class VideoHistoryAndDanmuService {

    @Autowired
    private VideoPlayHistoryMapper videoPlayHistoryMapper;
    @Autowired
    private VideoPhysicsInfoMapper videoPhysicsInfoMapper;

    /**
     * 保存播放历史
     * @param accountName
     * @param playTime  播放时长
     * @param videoId
     */
    public void saveVideoHistory(String accountName,long playTime,int videoId){
        VideoPlayHistoryVo playHistoryVo = this.videoPlayHistoryMapper.findVideoHistoryByVideoId(videoId,accountName);
        VideoPlayHistory history = new VideoPlayHistory();
        history.setVideoId(videoId);
        history.setAccountName(accountName);
        history.setPlayTime(playTime);

        //格式化播放时间为 00:00:00 格式
        history.setPlayTimeVis(VideoUtils.getVideoTimeFormat(playTime));

        if(playHistoryVo!=null){
            this.updateVideoHistory(history);
        }else{
            this.addVideoHistory(history);
        }
    }

    private void addVideoHistory(VideoPlayHistory videoPlayHistory){
//        int videoId = videoPlayHistory.getVideoId();
//        VideoPhysicsInfoVo physicsInfoVo = this.videoPhysicsInfoMapper.findPhysicsInfoById(videoId);
        this.videoPlayHistoryMapper.addVideoHistory(videoPlayHistory);
    }

   private void updateVideoHistory(VideoPlayHistory videoPlayHistory){
        videoPlayHistory.setUpdateTime(new Date());
        this.videoPlayHistoryMapper.updateVideoHistory(videoPlayHistory);
   }

   public Page<VideoPlayHistoryVo> findVideoHistoryByPage(VideoPlayHistory history, PageRequest pageRequest){
        return this.videoPlayHistoryMapper.findVideoHistoryByPage(history,pageRequest);
    }

   public VideoPlayHistoryVo findVideoHistoryByVideoId(String accountName,int videoId){
        return this.videoPlayHistoryMapper.findVideoHistoryByVideoId(videoId,accountName);
   }

}
