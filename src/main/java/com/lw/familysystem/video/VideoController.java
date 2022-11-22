package com.lw.familysystem.video;

import com.alibaba.fastjson.JSON;
import com.lw.config.mybatis.page.Page;
import com.lw.config.mybatis.page.PageRequest;
import com.lw.familysystem.ReturnInfo;
import com.lw.familysystem.cache.FamilyCache;
import com.lw.familysystem.entity.VideoInfo;
import com.lw.familysystem.entity.VideoPhysicsInfo;
import com.lw.familysystem.entity.VideoPlayHistory;
import com.lw.familysystem.vo.*;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lw
 * @date 2022/10/26 0026
 * @description
 */
@Controller
@RequestMapping("/video")
@Slf4j
public class VideoController {

    @Resource(name = "videoService")
    private VideoService videoService;
    @Resource(name = "videoHistoryAndDanmuService")
    private VideoHistoryAndDanmuService videoHistoryAndDanmuService;

    /**
     * 视频播放
     *
     * @param response
     */
    @RequestMapping("/playVideo/{videoId}")
    @ApiOperation("视频播放")
    @CrossOrigin(origins = "*", maxAge = 3600)
    public void playVideo(@PathVariable("videoId") String videoId, HttpServletResponse response) {
        this.videoService.playVideo(Integer.parseInt(videoId), response);
    }

    @RequestMapping("/addDanmu2Video")
    public void addDanmu2Video(@RequestBody DanmuVo danmuVo) {
        //todo
    }

    @RequestMapping("/getAllDanmu/{videoId}")
    @ResponseBody
    @ApiOperation("获取弹幕")
    public ReturnInfo getAllDanmu(@PathVariable("videoId") String videoId) {
        //todo
        return null;
    }


    /**
     * 视频下载
     *
     * @param response
     */
    @RequestMapping("/downloadVideo/{videoId}")
    @ApiOperation("视频下载")
    public void downloadVideo(@PathVariable("videoId")int videoId, HttpServletResponse response) {
        this.videoService.downloadVideo(videoId, response);
    }

    /**
     * 保存播放历史
     */
    @PostMapping("/savePlayHistory")
    @ResponseBody
    @ApiOperation("保存播放历史")
    public ReturnInfo savePlayHistory(@RequestParam("accountName") String accountName, @RequestParam("videoId") int videoId, @RequestParam("playTimeDouble") double playTimeDouble) {
        long playTime = (long)playTimeDouble;
        this.videoHistoryAndDanmuService.saveVideoHistory(accountName,playTime,videoId);
        return ReturnInfo.returnSuccessInfo();
    }

    /**
     * 获取某个视频的播放历史
     */
    @RequestMapping("/getVideoHistory")
    @ResponseBody
    public ReturnInfo getVideoHistory(@RequestParam("accountName") String accountName, @RequestParam("videoId") int videoId) {
        VideoPlayHistoryVo historyVo = this.videoHistoryAndDanmuService.findVideoHistoryByVideoId(accountName,videoId);
        return ReturnInfo.returnSuccessInfo(historyVo);
    }

    /**
     * 获取播放历史
     */
    @RequestMapping("/getAllVideoHistory")
    @ResponseBody
    public ReturnInfo getAllVideoHistory(@RequestParam("accountName") String accountName,HttpServletRequest request) {
        VideoPlayHistory playHistory = new VideoPlayHistory();
        playHistory.setAccountName(accountName);

        String page = request.getParameter("page");
        String size = request.getParameter("size");
        PageRequest pageRequest = new PageRequest(Integer.parseInt(page),Integer.parseInt(size));

        Page<VideoPlayHistoryVo> historyVoPage = this.videoHistoryAndDanmuService.findVideoHistoryByPage(playHistory,pageRequest);
        return ReturnInfo.returnSuccessInfo(historyVoPage);
    }

    /**
     * 查询类别
     * @return
     */
    @RequestMapping("/findAllCategories")
    @ResponseBody
    public ReturnInfo findAllCategories(){
        List<VideoCategoryVo>  categories = this.videoService.findAllCategories();
        return ReturnInfo.returnSuccessInfo(categories);
    }

    /**
     * 视频信息
     * @param videoInfo
     * @param request
     * @return
     */
    @PostMapping("/findVideoInfoByPage")
    @ResponseBody
    public ReturnInfo findVideoInfoByPage(@RequestBody VideoInfo videoInfo,HttpServletRequest request){
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        PageRequest pageRequest = new PageRequest(Integer.parseInt(page),Integer.parseInt(size));
        Page<VideoInfoVo> videoInfos = this.videoService.findVideoInfoByPage(videoInfo,pageRequest);
        return ReturnInfo.returnSuccessInfo(videoInfos);
    }

    /**
     * 查询季度信息
     * @param infoId
     * @return
     */
    @GetMapping("findQuarterInfo")
    @ResponseBody
    public ReturnInfo findQuarterInfo(@RequestParam("infoId")int infoId){
        return ReturnInfo.returnSuccessInfo(this.videoService.findQuarterInfo(infoId));
    }

    /**
     * 视频物理信息，可播放
     * @param physicsInfo
     * @param request
     * @return
     */
    @PostMapping("/findVideoPhysicsInfoByPage")
    @ResponseBody
    public ReturnInfo findVideoPhysicsInfoByPage(@RequestBody VideoPhysicsInfo physicsInfo, HttpServletRequest request){
        String page = request.getParameter("page");
        String size = request.getParameter("size");
        PageRequest pageRequest = new PageRequest(Integer.parseInt(page),Integer.parseInt(size));
        Page<VideoPhysicsInfoVo> videoInfos = this.videoService.findVideoPhysicsInfoByPage(physicsInfo,pageRequest);
        return ReturnInfo.returnSuccessInfo(videoInfos);
    }

}
