package com.lw.familysystem.games;

import com.lw.familysystem.vo.LuckImageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * 今日运气游戏
 */
@Service("luckGameService")
@Slf4j
public class LuckGameService {

    private String IMAGE_PATH = "E:\\temp\\images";

    /**
     * 返回图片信息
     */
    public List<LuckImageVo> getImages(int imageTotal){
        List<LuckImageVo> images = new ArrayList<>();

        File imgDir = new File(IMAGE_PATH);
        File[] imageFiles = imgDir.listFiles();
        BASE64Encoder base64Encoder = new BASE64Encoder();

        Random random = new Random();

        for (int i = 0; i < imageTotal; i++) {

            //随机挑选图片
            int imageIndex = random.nextInt(imageFiles.length);
            File imageFile = imageFiles[imageIndex];

            try(FileInputStream fis = new FileInputStream(imageFile);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){

                //读取图片信息
                byte[] imageByte = new byte[1024];
                int length = fis.read(imageByte);
                while(length>0){
                    byteArrayOutputStream.write(imageByte,0,length);
                    length = fis.read(imageByte);
                }

                //将图片信息返回为byte[]
                byte[] imageByteInfo = byteArrayOutputStream.toByteArray();

                //base64编码
                String imageBase64Info = base64Encoder.encode(imageByteInfo);

                LuckImageVo imageVo = new LuckImageVo();
                imageVo.imageCode = imageIndex;
                imageVo.imageBase64 = imageBase64Info;
                images.add(imageVo);

            }catch (IOException e){
                log.error("",e);
            }

        }
        return images;
    }



}
