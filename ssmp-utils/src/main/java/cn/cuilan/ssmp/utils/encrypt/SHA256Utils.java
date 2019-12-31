package cn.cuilan.ssmp.utils.encrypt;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Formatter;

/**
 * SHA-2，安全散列算法2
 * <p>
 * Secure Hash Algorithm 2的缩写，一种密码散列函数算法标准，由美国国家安全局研发，属于SHA算法之一，是SHA-1的后继者。
 * SHA-2下又可再分为六个不同的算法标准：
 * <pre>
 *     SHA-224
 *     SHA-256
 *     SHA-384
 *     SHA-512
 *     SHA-512/224
 *     SHA-512/256
 * </pre>
 *
 * @author zhang.yan
 * @date 2019-12-31
 */
@Slf4j
public class SHA256Utils {

    /**
     * 生成随机盐
     *
     * @return 随机盐
     */
    public static String genSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return byte2Hex(salt);
    }

    /**
     * byte数组转为16进制字符串
     *
     * @param bytes byte数组
     * @return 16进制字符串
     */
    private static String byte2Hex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hash = formatter.toString();
        formatter.close();
        return hash;
    }

    /**
     * 16进制字符串转为byte数组
     *
     * @param hex 16进制字符串
     * @return byte数组
     */
    private static byte[] hex2Bytes(String hex) {
        return new BigInteger(hex, 16).toByteArray();
    }

    /**
     * SHA-256安全散列
     *
     * @param src  原文
     * @param salt 盐
     * @return SHA-256散列值
     */
    public static String encrypt(String src, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(EncryptAlgorithm.SHA_256_ALGORITHM);
            md.update(hex2Bytes(salt));
            md.update(src.getBytes(StandardCharsets.UTF_8));
            return byte2Hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            log.error("sha256 error: ", e);
            throw new IllegalArgumentException("hash error: " + e.getMessage());
        }
    }
}
