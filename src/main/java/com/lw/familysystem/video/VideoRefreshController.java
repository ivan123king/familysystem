package com.lw.familysystem.video;


import com.lw.familysystem.ReturnInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 刷数据专用
 */
@Controller
@RequestMapping("/videoRefresh")
@Slf4j
public class VideoRefreshController {

    @Resource(name="videoRefreshService")
    private VideoRefreshService refreshService;

    /**
     * 刷类别
     */
    @GetMapping("/refreshCategoryInfo2Db")
    @ResponseBody
    public ReturnInfo refreshCategoryInfo2Db() {
        this.refreshService.refreshCategoryInfo2Db();
        return ReturnInfo.returnSuccessInfo();
    }

    /**
     * 刷视频信息
     */
    @GetMapping("/refreshVideoInfo2Db")
    @ResponseBody
    public ReturnInfo refreshVideoInfo2Db() {
        this.refreshService.refreshVideoInfo2Db();
        return ReturnInfo.returnSuccessInfo();
    }

    /**
     * 刷视频物理信息
     */
    @GetMapping("/refreshVideoPhysicsInfo2Db")
    @ResponseBody
    public ReturnInfo refreshVideoPhysicsInfo2Db() {
        this.refreshService.refreshVideoPhysicsInfo2Db();
        return ReturnInfo.returnSuccessInfo();
    }
}
