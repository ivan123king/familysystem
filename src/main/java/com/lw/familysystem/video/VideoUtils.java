package com.lw.familysystem.video;

import com.coremedia.iso.IsoFile;
import com.lw.familysystem.music.AudioUtils;
import lombok.extern.slf4j.Slf4j;
import com.lw.familysystem.StyleConstants;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacv.*;

import java.io.IOException;

/**
 * @author lw
 * @date 2022/10/26 0026
 * @description 视频工具
 */
@Slf4j
public class VideoUtils {
    /**
     * 获取视频文件的播放长度(mp4、mov格式)
     * @param videoPath
     * @return 单位为毫秒
     */
    public static long getMp4Duration(String videoPath) {
        long lengthInSeconds = 0;
        try{
            IsoFile isoFile = new IsoFile(videoPath);
            lengthInSeconds =
                    isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                            isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        }catch (IOException e){
            log.error("",e);
        }
        return lengthInSeconds;
    }


    /**
     * 得到语音或视频文件时长,单位秒
     * @param filePath
     * @return
     */
    public static long getDuration(String filePath){
        String format = getVideoFormat(filePath);
        long result = 0;
        result = getDuration(filePath,format);
        return result;
    }

    /**
     * 得到语音或视频文件时长,单位秒
     * @param filePath
     * @return
     */
    public static long getDuration(String filePath,String format){
        long result = 0;
        switch (format){
            case StyleConstants.WAV:
                result = AudioUtils.getDuration(filePath).intValue();
                break;
            case StyleConstants.MP3:
                result = AudioUtils.getMp3Duration(filePath).intValue();
                break;
            case StyleConstants.M4A:
            case StyleConstants.MOV:
            case StyleConstants.MP4:
                result = VideoUtils.getMp4Duration(filePath);
                break;

        }
        return result;
    }


    /**
     * 得到文件格式
     * @param path
     * @return
     */
    public static String getVideoFormat(String path){
        return  path.toLowerCase().substring(path.toLowerCase().lastIndexOf(".") + 1);
    }

    /**
     * 转变视频编码
     * @param inputFile  文件原始路径
     * @param outputFile 文件输出路径
     * @throws Exception
     */

    public static void encode(String inputFile, String outputFile){
        FFmpegFrameGrabber grabber = null;
        Frame captured_frame;
        FFmpegFrameRecorder recorder = null;

        try {
            grabber = FFmpegFrameGrabber.createDefault(inputFile);

            grabber.start();

            recorder = new FFmpegFrameRecorder(outputFile, grabber.getImageWidth(), grabber.getImageHeight(),
                    grabber.getAudioChannels());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setSampleRate(grabber.getSampleRate());
            recorder.setVideoBitrate(grabber.getVideoBitrate());
            recorder.setAspectRatio(grabber.getAspectRatio());
            recorder.setAudioBitrate(grabber.getAudioBitrate());
            recorder.setAudioOptions(grabber.getAudioOptions());
            recorder.setAudioCodec(avcodec.AV_CODEC_ID_AAC);
            recorder.start();

            while (true) {
                captured_frame = grabber.grabFrame();
                if (captured_frame == null) {
                    log.info("转码成功");
                    break;
                }
                recorder.record(captured_frame);
            }

        } catch (FrameGrabber.Exception|FrameRecorder.Exception e) {
            log.error("",e);
        } finally {
            if (recorder != null) {
                try {
                    recorder.close();
                } catch (Exception e) {
                    log.error("recorder.close异常");
                }
            }

            if(grabber!=null){
                try {
                    grabber.close();
                } catch (FrameGrabber.Exception e) {
                    log.error("frameGrabber.close异常");
                }
            }
        }
    }
}
