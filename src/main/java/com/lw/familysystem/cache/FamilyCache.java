package com.lw.familysystem.cache;

import com.lw.familysystem.vo.HistoryVideoVo;
import com.lw.familysystem.vo.VideoInfoVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.*;

/**
 * @author lw
 * @date 2022/10/28 0028
 * @description 缓存系统
 */
@Slf4j
@Component
public class FamilyCache {
    /**
     * 播放历史
     * key: accountName
     */
    private static Map<String, List<HistoryVideoVo>> historyCache = new HashMap<>();

    public static String putVideoHistoryCache(HistoryVideoVo vo) {
        String key = vo.getAccountName();
        List<HistoryVideoVo> histories = historyCache.get(key);
        if (histories == null) {
            histories = new ArrayList<>();
            historyCache.put(key, histories);
        }
        boolean hasIn = false;//判断是否已经将视频信息放入缓存
        for (HistoryVideoVo history : histories) {
            VideoInfoVo video = history.getVideoInfoVo();
            if (video.getVId() == vo.getVideoInfoVo().getVId()) {
                history.setRange(vo.getRange());
                history.setHistoryTime(new Date());
                hasIn = true;
                break;
            }
        }
        if (!hasIn) {
            vo.setHistoryTime(new Date());
            histories.add(vo);
        }
        return key;
    }

    public static List<HistoryVideoVo> getHistoryInfo(String accountName) {
        return historyCache.get(accountName);
    }

    public static HistoryVideoVo getHistoryInfo(String accountName, VideoInfoVo video) {
        HistoryVideoVo retHistory = null;

        List<HistoryVideoVo> histories = getHistoryInfo(accountName);
        if (histories != null) {
            for (HistoryVideoVo history : histories) {
                VideoInfoVo videoTemp = history.getVideoInfoVo();
                if (videoTemp.getVId() == video.getVId()) {
                    retHistory = history;
                    break;
                }
            }
        }
        return retHistory;
    }

    /**
     * 从硬盘中读取对象
     */
    public static void loadVideoHistoryFromHard() {
        File historyFile = new File("./histories.txt");
        if (historyFile.exists()) {
            try (FileInputStream freader = new FileInputStream(historyFile)) {
                ObjectInputStream objectInputStream = new ObjectInputStream(freader);
                historyCache = (HashMap<String, List<HistoryVideoVo>>) objectInputStream.readObject();
            } catch (ClassNotFoundException | IOException e) {
                log.error("加载历史数据失败：", e);
            }
        }
    }

    /**
     * 将对象写入硬盘
     */
    public static void writeVideoHistoryToHard() {
        try (FileOutputStream outStream = new FileOutputStream("./histories.txt")) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
            objectOutputStream.writeObject(historyCache);
        } catch (IOException e) {
            log.error("写入历史数据失败：", e);
        }
    }

}
