package com.lw.familysystem.games;

import com.lw.familysystem.ReturnInfo;
import com.lw.familysystem.vo.LuckImageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * 小游戏
 */
@Controller
@RequestMapping("/games")
@Slf4j
public class GameController {

    @Resource(name="luckGameService")
    private LuckGameService luckGameService;

    @RequestMapping("/getImages/{imageTotal}")
    @ResponseBody
    public ReturnInfo getImages(@PathVariable("imageTotal")int imageTotal){
        List<LuckImageVo> images = this.luckGameService.getImages(imageTotal);
        return ReturnInfo.returnSuccessInfo(images);
    }


}
