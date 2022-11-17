package com.lw.familysystem.video;

import com.alibaba.fastjson.JSON;
import com.lw.config.mybatis.page.Page;
import com.lw.familysystem.ReturnInfo;
import com.lw.familysystem.cache.FamilyCache;
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

    @RequestMapping("/getRootDirectory")
    @ResponseBody
    @ApiOperation("getRootDirectory")
    public ReturnInfo getRootDirectory(@RequestParam String filePath) {
        try {
            filePath = URLDecoder.decode(filePath, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }

        DirectoryInfoVo vo = null;
        if (StringUtils.isEmpty(filePath)) {
            vo = this.videoService.getRootDirectoryInfo();
        } else {
            vo = this.videoService.getDirectoryInfo(filePath, false);
        }
        return ReturnInfo.returnSuccessInfo(vo);
    }

    /**
     * 视频播放
     *
     * @param response
     */
    @RequestMapping("/playVideo/{videoId}/{accountName}")
    @ApiOperation("视频播放")
    @CrossOrigin(origins = "*", maxAge = 3600)
    public void playVideo(@PathVariable("videoId") String videoId, @PathVariable("accountName") String accountName, HttpServletResponse response) {
        Map<String, String> otherInfo = new HashMap<>();
        otherInfo.put("accountName", accountName);
        VideoInfoVo vo = FamilyCache.getVideoInfo(videoId);
        this.videoService.playVideo(otherInfo, vo, response);
    }

    @RequestMapping("/addDanmu2Video")
    public void addDanmu2Video(@RequestBody DanmuVo danmuVo) {
        VideoInfoVo videoInfoVo = FamilyCache.getVideoInfo(danmuVo.getVideoId());
        videoInfoVo.addDanmu(danmuVo);
    }

    @RequestMapping("/getAllDanmu/{videoId}")
    @ResponseBody
    @ApiOperation("获取弹幕")
    public ReturnInfo getAllDanmu(@PathVariable("videoId") String videoId) {
        VideoInfoVo videoInfoVo = FamilyCache.getVideoInfo(videoId);
        return ReturnInfo.returnSuccessInfo(videoInfoVo.getDanMus());
    }


    /**
     * 视频下载
     *
     * @param response
     */
    @RequestMapping("/downloadVideo")
    @ApiOperation("视频下载")
    public void downloadVideo(@RequestBody VideoInfoVo vo, HttpServletResponse response) {
        Map<String, String> otherInfo = new HashMap<>();
        this.videoService.downloadVideo(otherInfo, vo, response);
    }

    /**
     * 保存播放历史
     */
    @RequestMapping("/savePlayHistory")
    @ResponseBody
    @ApiOperation("保存播放历史")
    public ReturnInfo savePlayHistory(@RequestParam("loginName") String loginName, @RequestParam("videoId") String videoId, @RequestParam("range") double range) {
        HistoryVideoVo historyVideoVo = new HistoryVideoVo();
        VideoInfoVo vo = FamilyCache.getVideoInfo(videoId);
        historyVideoVo.setVideoInfoVo(vo);
        historyVideoVo.setAccountName(loginName);
        historyVideoVo.setRange((int) range);
        FamilyCache.putVideoHistoryCache(historyVideoVo);
        return ReturnInfo.returnSuccessInfo();
    }

    /**
     * 获取某个视频的播放历史
     */
    @RequestMapping("/getVideoHistory")
    @ResponseBody
    public ReturnInfo getVideoHistory(@RequestParam("loginName") String loginName, @RequestParam("videoId") int videoId) {
        VideoInfoVo vo = FamilyCache.getVideoInfo("" + videoId);
        HistoryVideoVo historyVideoVo = FamilyCache.getHistoryInfo(loginName, vo);
        return ReturnInfo.returnSuccessInfo(historyVideoVo);
    }

    /**
     * 获取某个视频的播放历史
     */
    @RequestMapping("/getAllVideoHistory")
    @ResponseBody
    public ReturnInfo getAllVideoHistory(@RequestParam("loginName") String loginName) {
        List<HistoryVideoVo> historyVideoVos = FamilyCache.getHistoryInfo(loginName);
        List<HistoryVideoVo> retList = new ArrayList<>();
        if(historyVideoVos!=null){
            //按照时间排序的List
            retList = historyVideoVos.stream()
                    .sorted(Comparator.comparing(HistoryVideoVo::getHistoryTime,Comparator.reverseOrder()))
                    .collect(Collectors.toList());
        }
        return ReturnInfo.returnSuccessInfo(retList);
    }

    @RequestMapping("/findAllCategories")
    @ResponseBody
    public ReturnInfo findAllCategories(){
        List<VideoCategoryVo>  categories = this.videoService.findAllCategories();
        return ReturnInfo.returnSuccessInfo(categories);
    }
    @RequestMapping("/findCategoriesByPage")
    @ResponseBody
    public ReturnInfo findCategoriesByPage(){
        Page<VideoCategoryVo> categories = this.videoService.findCategoriesByPage();
        return ReturnInfo.returnSuccessInfo(categories);
    }

}
