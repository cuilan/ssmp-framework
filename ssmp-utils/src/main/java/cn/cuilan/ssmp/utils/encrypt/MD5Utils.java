package cn.cuilan.ssmp.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;

/**
 * MD5消息摘要算法
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
@Slf4j
public class MD5Utils {

    /**
     * MD5消息摘要算法
     *
     * @param src 原文
     * @return byte数组
     */
    private static byte[] md5(String src) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance(EncryptAlgorithm.MD5_ALGORITHM);
            algorithm.reset();
            algorithm.update(src.getBytes(StandardCharsets.UTF_8));
            return algorithm.digest();
        } catch (Exception e) {
            log.error("MD5 Error...", e);
        }
        return null;
    }

    /**
     * 将MD5消息摘要计算后的byte数组转为16进制
     *
     * @param hash MD5消息摘要计算后的byte数组
     * @return 返回16进制格式字符串
     */
    private static String toHex(byte[] hash) {
        if (hash == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder(hash.length * 2);
        int i;
        for (i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    /**
     * 将消息进行MD5消息摘要计算后转为16进制字符串
     *
     * @param src 原文
     * @return 返回16进制字符串
     */
    public static String md5ToHex(String src) {
        try {
            return new String(Objects.requireNonNull(toHex(md5(src))).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("not supported charset...", e);
            return src;
        }
    }

    /**
     * 将消息加盐处理，并进行MD5消息摘要计算后转为16进制字符串
     *
     * @param src 原文
     * @return 包含随机盐的密文
     */
    public static String md5ToHexWithRandomSalt(String src) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(16);
        // 两次添加8位数字
        sb.append(random.nextInt(99999999))
                .append(random.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16; i++) {
                sb.append("0");
            }
        }
        String salt = sb.toString();
        src = md5ToHex(src + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = src.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = src.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * 校验密码是否正确
     *
     * @param src            原文
     * @param md5HexWithSalt 加盐处理后的MD5密文（16进制）
     * @return 是否正确
     */
    public static boolean verify(String src, String md5HexWithSalt) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5HexWithSalt.charAt(i);
            cs1[i / 3 * 2 + 1] = md5HexWithSalt.charAt(i + 2);
            cs2[i / 3] = md5HexWithSalt.charAt(i + 1);
        }
        String salt = new String(cs2);
        return md5ToHex(src + salt).equals(new String(cs1));
    }
}
