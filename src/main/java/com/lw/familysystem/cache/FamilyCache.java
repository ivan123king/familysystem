package com.lw.familysystem.cache;

import com.lw.familysystem.vo.DirectoryInfoVo;
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
     * 目录缓存
     * key：filePath
     */
    private static Map<String, DirectoryInfoVo> dirsCache = new HashMap<>();

    /**
     * 视频信息缓存
     * Key: filePath 此处是绝对路径，带了文件名和路径
     * Key: vId
     */
    private static Map<String, VideoInfoVo> videoInfoCache = new HashMap<>();

    /**
     * 播放历史
     * key: accountName
     */
    private static Map<String, List<HistoryVideoVo>> historyCache = new HashMap<>();

    /**
     * 放入缓存
     *
     * @param vo
     * @return
     */
    public static String putDirCache(DirectoryInfoVo vo) {
        String key = vo.getPath();
        dirsCache.put(key, vo);
        return key;
    }

    public static String putVideoCache(VideoInfoVo vo) {
        String key = "" + vo.getFilePath().hashCode();
        videoInfoCache.put(key, vo);
        return key;
    }

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

    /**
     * 刷新缓存
     *
     * @param filePath
     * @param vo
     */
    public static void refreshDirInfo(String filePath, DirectoryInfoVo vo) {
        dirsCache.put(filePath, vo);
    }

    public static void refreshVideoInfo(String filePath, VideoInfoVo vo) {
        videoInfoCache.put("" + filePath.hashCode(), vo);
    }

    /**
     * 获取缓存信息
     *
     * @param filePath
     * @return
     */
    public static DirectoryInfoVo getDirectoryInfo(String filePath) {
        if (hasDirInfo(filePath)) {
            return dirsCache.get(filePath);
        }
        return null;
    }

    public static VideoInfoVo getVideoInfo(String vId) {
        return videoInfoCache.get(vId);
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
     * 判断是否在缓存中
     *
     * @param filePath
     * @return
     */
    public static boolean hasDirInfo(String filePath) {
        return dirsCache.containsKey(filePath);
    }

    public static boolean hasVideoInfo(String filePath) {
        return videoInfoCache.containsKey(filePath);
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
