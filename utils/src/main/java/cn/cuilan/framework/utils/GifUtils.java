package cn.cuilan.framework.utils;

import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import java.io.*;
import java.net.URL;

@Slf4j
public class GifUtils {

    public static boolean isGif(String imgUrl) {

        try {
            return isGif(new BufferedInputStream(new URL(imgUrl).openConnection().getInputStream()));
        } catch (IOException e) {
        }
        return false;

    }

    public static boolean isGif(File file) {

        try {
            return isGif(new BufferedInputStream(new FileInputStream(file)));
        } catch (IOException e) {
        }
        return false;

    }

    /**
     * 判断 是否是GIF 图片
     */
    public static boolean isGif(BufferedInputStream input) {

        boolean isGif = false;

        if (input != null) {
            input.mark(6);
            byte[] header = new byte[6];

            int head = 0;

            try {
                head = input.read(header);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (head != -1) {
                String id = "";
                for (int i = 0; i < 6; i++) {
                    id += (char) header[i];
                }

                if (id.toUpperCase().startsWith("GIF")) {
                    isGif = true;
                }
            }

            try {
                input.close();
                input.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return isGif;

    }

    /**
     * 获取gif图片第一帧
     * @param imgUrl
     * @param frame
     * @return
     */
    public static byte[] getGifOneFrame(byte[] imgBytes,int frameIndex){
        ByteArrayInputStream inputStream = null;
        try{
            if(frameIndex < 0){
                frameIndex = 0;
            }
            ImageReaderSpi readerSpi = new GIFImageReaderSpi();
            GIFImageReader gifReader = (GIFImageReader) readerSpi.createReaderInstance();
            inputStream=new ByteArrayInputStream(imgBytes);
            gifReader.setInput(ImageIO.createImageInputStream(inputStream));
            int num = gifReader.getNumImages(true);
            if (num > frameIndex) {
                for (int i = 0; i < num; i++) {
                    if (i == frameIndex) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        ImageIO.write(gifReader.read(i), "png", out);
                        byte[] b = out.toByteArray();
                        return b;
                    }
                }
            }

        }catch(Exception err){
            log.error("获取gif图片第一帧失败",err);
        }finally {
            if(null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
