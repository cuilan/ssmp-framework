package cn.cuilan.ssmp.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 文件读取、写入工具
 *
 * @author zhang.yan
 * @date 2020/1/8
 */
@Slf4j
public class FileReadWriteUtils {

    /**
     * 读取文件
     *
     * @param fileName 文件绝对路径
     * @return 返回文件byte数组
     */
    public static byte[] readFileBytes(String fileName) {
        File file = new File(fileName);
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] bytes = new byte[(int) file.length()];
            // 读取文件内容到bytes数组
            int read = is.read(bytes);
            return bytes;
        } catch (IOException e) {
            log.error("read file error, file path: {}", fileName);
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 写入文件
     *
     * @param bytes    文件byte数组
     * @param fileName 文件名称，绝对路径
     */
    public static void writeFile(byte[] bytes, String fileName) {
        File file = new File(fileName);
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            log.error("write file error, file path: {}", fileName);
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
