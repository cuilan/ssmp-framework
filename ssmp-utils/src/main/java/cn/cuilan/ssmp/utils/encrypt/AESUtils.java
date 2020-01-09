package cn.cuilan.ssmp.utils.encrypt;

import cn.cuilan.ssmp.utils.CheckUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES加密工具类
 */
public class AESUtils {

    /**
     * 随机生成AES秘钥
     *
     * @return 返回AES秘钥，Base64编码字符串格式
     */
    public static String generateRandomKeyWithBase64() {
        return new String(Base64.getEncoder().encode(generateRandomKey()));
    }

    /**
     * 随机生成AES秘钥
     *
     * @return 返回AES秘钥，byte数组
     */
    public static byte[] generateRandomKey() {
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance(EncryptAlgorithm.AES_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("genarateRandomKey fail!", e);
        }
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        Key key = keygen.generateKey();
        return key.getEncoded();
    }

    /**
     * 将Base64 key转为byte数组
     *
     * @param key Base64编码的key
     * @return 返回byte数组
     */
    public static byte[] base64DecodeKey(String key) {
        return Base64.getDecoder().decode(key);
    }

    // ===================================================================================

    /**
     * 将key转为BASE64编码
     *
     * @param data 数据
     * @param key  BASE64编码的key
     * @return BASE64编码字符串密文
     */
    public static String encryptWithKeyBase64(String data, String key) {
        try {
            byte[] valueByte = encrypt(data.getBytes(EncryptAlgorithm.CHAR_ENCODING),
                    Base64.getDecoder().decode(key.getBytes()));
            return new String(Base64.getEncoder().encode(valueByte));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    /**
     * 加密
     *
     * @param data 需要加密的内容
     * @param key  加密密码
     * @return 返回AES加密后的密文
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        CheckUtils.notEmpty(data, "data");
        CheckUtils.notEmpty(key, "key");
        if (key.length != 16) {
            throw new RuntimeException("Invalid AESUtils key length (must be 16 bytes)");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, EncryptAlgorithm.AES_ALGORITHM);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, EncryptAlgorithm.AES_ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(EncryptAlgorithm.AES_ALGORITHM);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, seckey);
            // 加密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    // ===================================================================================

    /**
     * 解密
     *
     * @param data 加密密文
     * @param key  BASE64编码的秘钥
     * @return 原文
     */
    public static String decryptWithKeyBase64(String data, String key) {
        try {
            byte[] originalData = Base64.getDecoder().decode(data.getBytes());
            byte[] valueByte = decrypt(originalData, Base64.getDecoder().decode(key.getBytes()));
            return new String(valueByte, EncryptAlgorithm.CHAR_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    /**
     * 解密
     *
     * @param data 待解密内容
     * @param key  解密密钥
     * @return 返回AES解密后的明文
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        CheckUtils.notEmpty(data, "data");
        CheckUtils.notEmpty(key, "key");
        if (key.length != 16) {
            throw new RuntimeException("Invalid AESUtils key length (must be 16 bytes)");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, EncryptAlgorithm.AES_ALGORITHM);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, EncryptAlgorithm.AES_ALGORITHM);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(EncryptAlgorithm.AES_ALGORITHM);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, seckey);
            // 解密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }
}
