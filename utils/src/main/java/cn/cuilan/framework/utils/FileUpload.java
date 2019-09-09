package cn.cuilan.framework.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;


public class FileUpload {

    public static final String imgType = "type";
    public static final String imgData = "data";
    private static final Logger logger = LoggerFactory.getLogger(FileUpload.class);

    /**
     * 把远程图片转换成byte[]
     *
     * @param remoteUrl 远程其他人的地址http://开头的，或者其他
     * @return upload file absolute path
     */
    public static byte[] translateRemoteImageToByte(String remoteUrl) {
        return translateRemoteImageToByte(remoteUrl, null);
    }

    /**
     * 把远程图片转换成byte[]
     *
     * @param remoteUrl 远程其他人的地址http://开头的，或者其他
     * @param referer   引用页
     * @return upload file absolute path
     */
    public static byte[] translateRemoteImageToByte(String remoteUrl, String referer) {

        if (StringUtils.isEmpty(remoteUrl)) {
            return null;
        }

        try {
            URL url = new URL(remoteUrl);
            URLConnection connection = url.openConnection();
            if (StringUtils.isNotBlank(referer)) {
                connection.setRequestProperty("Referer", referer);
            }
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            return inputStreamToByte(connection.getInputStream());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static Map<String, byte[]> translateRemoteImageToByteAndType(String remoteUrl) {
        Map<String, byte[]> map = new HashMap<String, byte[]>();
        if (StringUtils.isEmpty(remoteUrl)) {
            return map;
        }

        try {
            URL url = new URL(remoteUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

            String imgType = connection.getContentType();
            if (!StringUtils.equalsIgnoreCase(imgType.substring(0, imgType.indexOf("/")), "image")) {
                return map;
            }
            imgType = imgType.substring(imgType.indexOf("/") + 1);
            if (StringUtils.equalsIgnoreCase(imgType, "jpeg")) {
                imgType = "jpg";
            }

            byte[] result = inputStreamToByte(connection.getInputStream());
            byte[] imgTypeByte = null;
            if (!StringUtils.isEmpty(imgType)) {
                imgTypeByte = imgType.getBytes();
            }
            map.put(FileUpload.imgType, imgTypeByte);
            map.put(FileUpload.imgData, result);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return map;
        }
    }

    /**
     * 把InputStream转换成byte[]
     *
     * @param is 字节流
     * @return
     * @throws IOException
     */
    public static byte[] inputStreamToByte(InputStream is) throws IOException {
        try {
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            logger.error("inputStreamToByte exception: {}", e.getMessage());
        } finally {
            is.close();
        }
        return null;
    }


    /**
     * 获取指定文件的扩展名
     *
     * @param remoteImage
     * @return
     */
    public static String getImageExt(String remoteImage) {
        File file = new File(remoteImage);
        String imgName = file.getName();

        if (StringUtils.isEmpty(imgName)) {
            return null;
        }
        return imgName.substring(imgName.lastIndexOf(".") + 1);
    }

}
