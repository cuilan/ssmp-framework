package cn.cuilan.framework.utils.service;

//import com.luciad.imageio.webp.WebPReadParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Jag 2018/3/29 15:21
 */
@Slf4j
public class UploadUtil {


    private static final String CHOUTI = "CHOUTI_";

    private static Map<String, String> map = null;

    static {
        map = new HashMap<>();
        map.put("FFD8FF", "jpg");
        map.put("89504E", "png");
        map.put("474946", "gif");
        map.put("524946", "webp");
        map.put("000001", "ico");
        map.put("424D36", "bmp");
        map.put("00000A", "tga");
        map.put("49492A", "tif");
    }


    public static String genFileName(File file) {
        if (file == null) {
            return "";
        }
        String fileName = file.getName();
        String fileSuffix;
        if (fileName.contains(".")) {
            fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        } else {//如果没后缀就从图片中读取
            fileSuffix = "." + getImageType(file);
        }

        String uid = UUID.randomUUID().toString().replace("-", "");
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            String filename = getPrefix() + uid + "_w" + width + "h" + height;
            filename = filename.toUpperCase();
            return filename.concat(fileSuffix);
        } catch (Exception e) {
            log.info("chouti get image WH fail...");
            return uid + fileSuffix;
        }
    }

    public static String getImageType(File file) {
        String type = "";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] b = new byte[3];
            fileInputStream.read(b, 0, b.length);
            type = getImageType(b);
        } catch (Exception e) {
            log.error("获取文件格式异常");
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return type;
    }


    private static String checkType(String code) {
        return map.get(code);
    }

    private static String getImageType(byte header[]) {
        byte[] dest = new byte[3];
        System.arraycopy(header, 0, dest, 0, 3);
        String code = bytesToHexString(dest);
        code = code.toUpperCase();
        return checkType(code);
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    private static String getPrefix() {
        return CHOUTI + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/";
    }

    /**
     * 上传用户本地图片到该服务器端
     */
    public static String saveFile(MultipartFile fileItem, String saveFilePath) throws IOException {
        String shortUuid = ShortUUid.generateShortUuid();
        String fileName = shortUuid.substring(0, 2) + System.currentTimeMillis() + shortUuid.substring(2, 7);
        // 新的图片文件名 = 获取时间戳+"."图片扩展名
        String newFileName = fileName + "." + getImageType(fileItem.getBytes());

        /* 构建文件目录 */
        File fileDir = new File(saveFilePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        try {
            FileOutputStream out = new FileOutputStream(saveFilePath + "/" + newFileName);
            // 写入文件
            out.write(fileItem.getBytes());
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if ("webp".equals(newFileName.substring(newFileName.lastIndexOf(".") + 1))) {
                log.info("文件是webp格式，需要在一步处理");
                // Obtain a WebP ImageReader instance
                ImageReader reader = ImageIO.getImageReadersByMIMEType("image/webp").next();
                // Configure decoding parameters
//                WebPReadParam readParam = new WebPReadParam();
//                readParam.setBypassFiltering(true);
                // Configure the input on the ImageReader
//                reader.setInput(new FileImageInputStream(new File(saveFilePath + "/" + newFileName)));
                // Decode the image
//                BufferedImage image = reader.read(0, readParam);
//                newFileName = newFileName.replaceAll(".webp", ".jpg");
//                ImageIO.write(image, "jpg", new File(saveFilePath + "/" + newFileName));
//                BufferedImage image = reader.read(0, readParam);
                newFileName = newFileName.replaceAll(".webp", ".jpg");
//                ImageIO.write(image, "jpg", new File(saveFilePath + "/" + newFileName));
                log.info("文件从webp格式转成jpg");
            }
        } catch (Exception e) {
            log.error("webp转jpg异常:{}", e.getMessage());
            throw new IOException("不支持webp动态图上传");
        }
        return saveFilePath + "/" + newFileName;
    }

    public static void main(String[] a) {
        String path1 = "/Users/Jag/choutiWorkspace/chouti-dig/modules/chouti-upload/0.jpg";
        String path2 = "/Users/Jag/choutiWorkspace/chouti-dig/modules/chouti-upload/jpg-0.webp";
        System.out.println(getImageType(new File(path1)));
        System.out.println(getImageType(new File(path2)));
    }


    public static boolean isGif(File srcfile) {
        boolean isGif = false;
        try {
            if (!srcfile.exists()) {
                return false;
            }
            FileInputStream in = new FileInputStream(srcfile);
            in.mark(6);
            byte[] header = new byte[6];
            int head = 0;
            try {
                head = in.read(header);
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

        } catch (Exception err) {

        }
        return isGif;
    }
}
