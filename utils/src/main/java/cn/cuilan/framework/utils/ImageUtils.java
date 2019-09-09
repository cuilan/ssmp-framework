package cn.cuilan.framework.utils;


import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@SuppressWarnings("restriction")
public class ImageUtils {
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";

    private static final int DEFAULT_WIDTH = 0;
    private static final int DEFAULT_HEIGHT = 1600;

    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);
    static Map<String, String> imageTypeMap = new HashMap() {
        {
            put("jpg", "FFD8FF"); // JPEG
            put("png", "89504E47");// PNG
            put("gif", "47494638");// GIF
            put("webp", "52494646");//WEBP
        }
    };

    public static void clipImg(InputStream stream, int x, int y, int width, int height, Float scale, String picType, String destPath) {
        try {
            Image image = ImageIO.read(stream);

            int h = image.getHeight(null);
            int w = image.getWidth(null);
//            logger.info("{}=={}", w, h);
            h = (int) (h * scale);
            w = (int) (w * scale);
            image = image.getScaledInstance(w, h, 1);

            BufferedImage tag = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//			tag.getGraphics().drawImage(image.getScaledInstance(width, height,Image.SCALE_SMOOTH), x, y, null);
            Graphics g = tag.createGraphics();
            g.drawImage(image, 0, 0, w, h, null);
//			g.drawImage(image, x, y, width, height, null);
//			g.hitClip(x, y, width, height);
//			g.clipRect(x, y, width, height);
//			g.drawImage(image, x, y, width, height, null);
//			g.fillRect(x, y, width, height);
            g.dispose();

            ImageIO.write(tag, "jpeg", new File(destPath));
            image.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        clipImg(destPath, x, y, width, height, scale, picType, destPath);
    }

    /**
     * 截取图片对象
     *
     * @param sourcePath
     * @param x
     * @param y
     * @param width
     * @param height
     * @param scale
     * @param picType    图片后缀名
     * @param destFile
     */
    public static void clipImg(String sourcePath, int x, int y, int width, int height, Float scale, String picType, String destFile) {

        try {
            // 取得图片读入器
            @SuppressWarnings("rawtypes")
            Iterator readers = ImageIO.getImageReadersByFormatName(picType);

//            logger.info("{}", readers);
            ImageReader reader = (ImageReader) readers.next();
//            logger.info("{}", reader);
            // 取得图片读入流
            ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(sourcePath));
            reader.setInput(iis, true);
            // 图片参数
            ImageReadParam param = reader.getDefaultReadParam();
            int imageIndex = 0;
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(imageIndex, param);
            ImageIO.write(bi, picType, new FileOutputStream(destFile));

        } catch (Exception e) {
            logger.info("-----ImgReaderException:{}", e.toString());

        }
    }

    /**
     * @param sourcePath
     * @param x
     * @param y
     * @param width
     * @param height
     * @param picType
     */
    public static void clipImg1(InputStream stream, String sourcePath, int x, int y, int width, int height, String picType) {
        try {
            Image image = ImageIO.read(stream);

            int h = image.getHeight(null);
            int w = image.getWidth(null);
            logger.info(w + "==" + h);
            image = image.getScaledInstance(w, h, 1);

            BufferedImage tag = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            Graphics g = tag.createGraphics();
            g.drawImage(image, 0, 0, w, h, null);

            g.dispose();

            ImageIO.write(tag, "jpeg", new File(sourcePath));
            image.flush();

            // 取得图片读入器
            @SuppressWarnings("rawtypes")
            Iterator readers = ImageIO.getImageReadersByFormatName(picType);

//            logger.info("{}", readers);
            ImageReader reader = (ImageReader) readers.next();
//            logger.info("{}", reader);
            // 取得图片读入流
            ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(sourcePath));
            reader.setInput(iis, true);
            // 图片参数
            ImageReadParam param = reader.getDefaultReadParam();
            int imageIndex = 0;
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(imageIndex, param);
            ImageIO.write(bi, picType, new FileOutputStream(sourcePath));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param sourcePath
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static void clipImg1(InputStream stream, String sourcePath, int x, int y, int width, int height) {
        try {
            Image image = ImageIO.read(stream);

            int h = image.getHeight(null);
            int w = image.getWidth(null);
//            logger.info(w + "==" + h);
            image = image.getScaledInstance(w, h, 1);

            BufferedImage tag = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            Graphics g = tag.createGraphics();
            g.drawImage(image, 0, 0, w, h, null);

            g.dispose();

            ImageIO.write(tag, "jpeg", new File(sourcePath));
            image.flush();


            // 取得图片读入器
            Iterator readers = ImageIO.getImageReadersByFormatName("jpg");
//            logger.info("{}", readers);
            ImageReader reader = (ImageReader) readers.next();
//            logger.info("{}", reader);
            // 取得图片读入流
            ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(sourcePath));
            reader.setInput(iis, true);
            // 图片参数
            ImageReadParam param = reader.getDefaultReadParam();
            int imageIndex = 0;
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(imageIndex, param);
            ImageIO.write(bi, "jpg", new FileOutputStream(sourcePath));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getImgSize(InputStream stream) {
        try {
            Image image = ImageIO.read(stream);
            int h = image.getHeight(null);
            int w = image.getWidth(null);
            return "_W" + w + "H" + h;
        } catch (Exception e) {
            return "";
        }
    }

    public static HashMap<String, Integer> getImgWidthAndHeight(String imgUrl) {
        Integer h = 0; //默认
        Integer w = 0;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        try {
            URL url = new URL(imgUrl);
            InputStream is = url.openStream();
            Image image = ImageIO.read(is);
            h = image.getHeight(null);
            w = image.getWidth(null);
        } catch (Exception e) {
            logger.info("getImgWidthAndHeight Exception: {}", e.toString());
        }
        map.put("width", w);
        map.put("height", h);
        return map;
    }

    public static HashMap<String, Integer> getImgWH(String imgUrl) {
        if (StringUtils.isEmpty(imgUrl)) {
            return null;
        }
        try {
            return processStream(new URL(imgUrl).openConnection().getInputStream());
        } catch (Exception err) {
            return null;
        }

    }

    private static HashMap<String, Integer> processStream(InputStream is) throws IOException {
        int c1 = is.read();
        int c2 = is.read();
        int c3 = is.read();
        String mimeType = null;
        int width;
        int height;
        width = height = -1;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        if (c1 == 'G' && c2 == 'I' && c3 == 'F') { // GIF
            is.skip(3);
            width = readInt(is, 2, false);
            height = readInt(is, 2, false);
            mimeType = "image/gif";
            map.put(WIDTH, width);
            map.put(HEIGHT, height);
        } else if (c1 == 0xFF && c2 == 0xD8) { // JPG
            while (c3 == 255) {
                int marker = is.read();
                int len = readInt(is, 2, true);
                if (marker == 192 || marker == 193 || marker == 194) {
                    is.skip(1);
                    height = readInt(is, 2, true);
                    width = readInt(is, 2, true);
                    mimeType = "image/jpeg";
                    map.put(WIDTH, width);
                    map.put(HEIGHT, height);
                    break;
                }
                is.skip(len - 2);
                c3 = is.read();
            }
        } else if (c1 == 137 && c2 == 80 && c3 == 78) { // PNG
            is.skip(15);
            width = readInt(is, 2, true);
            is.skip(2);
            height = readInt(is, 2, true);
            mimeType = "image/png";
            map.put(WIDTH, width);
            map.put(HEIGHT, height);
        } else if (c1 == 66 && c2 == 77) { // BMP
            is.skip(15);
            width = readInt(is, 2, false);
            is.skip(2);
            height = readInt(is, 2, false);
            mimeType = "image/bmp";
            map.put(WIDTH, width);
            map.put(HEIGHT, height);
        } else {
            int c4 = is.read();
            if ((c1 == 'M' && c2 == 'M' && c3 == 0 && c4 == 42)
                    || (c1 == 'I' && c2 == 'I' && c3 == 42 && c4 == 0)) { //TIFF
                boolean bigEndian = c1 == 'M';
                int ifd = 0;
                int entries;
                ifd = readInt(is, 4, bigEndian);
                is.skip(ifd - 8);
                entries = readInt(is, 2, bigEndian);
                for (int i = 1; i <= entries; i++) {
                    int tag = readInt(is, 2, bigEndian);
                    int fieldType = readInt(is, 2, bigEndian);
                    int valOffset;
                    if ((fieldType == 3 || fieldType == 8)) {
                        valOffset = readInt(is, 2, bigEndian);
                        is.skip(2);
                    } else {
                        valOffset = readInt(is, 4, bigEndian);
                    }
                    if (tag == 256) {
                        width = valOffset;
                        map.put(WIDTH, width);
                    } else if (tag == 257) {
                        height = valOffset;
                        map.put(HEIGHT, height);
                    }
                    if (width != -1 && height != -1) {
                        mimeType = "image/tiff";
                        break;
                    }
                }
            }
        }
        if (null != is) {
            is.close();
        }
        return map;
    }

    private static int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
        int ret = 0;
        int sv = bigEndian ? ((noOfBytes - 1) * 8) : 0;
        int cnt = bigEndian ? -8 : 8;
        for (int i = 0; i < noOfBytes; i++) {
            ret |= is.read() << sv;
            sv += cnt;
        }
        return ret;
    }

    public static boolean isLongGraph(String uri) {
        return isLongGraph(uri, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static boolean isLongGraph(String uri, Integer width, Integer height) {
        if (StringUtils.isBlank(uri)) {
            return false;
        }
        try {
            Map<String, Integer> map = getImgWH(uri);
            if (map.containsKey(WIDTH)
                    && map.containsKey(HEIGHT)) {
                Integer w = map.get(WIDTH);
                Integer h = map.get(HEIGHT);
                if (w == null || h == null || w <= 0 || h <= 0) {
                    return false;
                }
                if (w > width && h >= height) {
                    int scale = h / w;
                    if (scale > 2) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean isImgUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        byte[] result = FileUpload.translateRemoteImageToByte(url);
        if (ArrayUtils.isEmpty(result)) {
            logger.error("下载配图失败! 取消配图");
            return false;
        }

        String type = ImageUtils.getImageType(result);
        if (StringUtils.isEmpty(type)) {
//            logger.info("----验证图片格式失败");
//            logger.info("----变换图片格式为jpg");
            return false;
        }
        return true;
    }

    public static String getImageType(byte header[]) {
        String filetypeHex = String.valueOf(TextUtils.bytesToHexString(header));
        Iterator<Map.Entry<String, String>> entryiterator = imageTypeMap.entrySet().iterator();
        while (entryiterator.hasNext()) {
            Map.Entry<String, String> entry = entryiterator.next();
            String fileTypeHexValue = entry.getValue();
            if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getImageType(File file) {
        String type = null;
        try {
            BufferedInputStream buffer = new BufferedInputStream(
                    new FileInputStream(file));
            if (buffer != null) {
                buffer.mark(6);
                byte[] header = new byte[8];
                int head = buffer.read(header);
                if (head != -1) {
                    if ((header[0] & 0xff) == 0x47 && (header[1] & 0xff) == 0x49
                            && (header[2] & 0xff) == 0x46) {
                        type = "gif";
                    } else if ((header[0] & 0xff) == 0xff
                            && (header[1] & 0xff) == 0xd8) {
                        type = "jpg";
                    } else if ((header[0] & 0xff) == 0x89
                            && (header[1] & 0xff) == 0x50
                            && (header[2] & 0xff) == 0x4e
                            && (header[3] & 0xff) == 0x47
                            && (header[4] & 0xff) == 0x0d
                            && (header[5] & 0xff) == 0x0a
                            && (header[6] & 0xff) == 0x1a
                            && (header[7] & 0xff) == 0x0a) {
                        type = "png";
                    }
                }
                buffer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }
}
