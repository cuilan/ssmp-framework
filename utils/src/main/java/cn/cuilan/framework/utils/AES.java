package cn.cuilan.framework.utils;

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
public class AES {

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
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, ConfigureEncryptAndDecrypt.AES);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, ConfigureEncryptAndDecrypt.AES);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ConfigureEncryptAndDecrypt.AES_ALGORITHM);
            // 初始化
            cipher.init(Cipher.ENCRYPT_MODE, seckey);
            // 加密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
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
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        }
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, ConfigureEncryptAndDecrypt.AES);
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, ConfigureEncryptAndDecrypt.AES);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(ConfigureEncryptAndDecrypt.AES_ALGORITHM);
            // 初始化
            cipher.init(Cipher.DECRYPT_MODE, seckey);
            // 解密
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    /**
     * 转为BASE64编码
     *
     * @param data 数据
     * @param key  key
     * @return base64编码字符串
     */
    public static String encryptToBase64(String data, String key) {
        try {
            byte[] valueByte = encrypt(data.getBytes(ConfigureEncryptAndDecrypt.CHAR_ENCODING),
                    key.getBytes(ConfigureEncryptAndDecrypt.CHAR_ENCODING));
            return new String(Base64.getEncoder().encode(valueByte));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("encrypt fail!", e);
        }

    }

    /**
     * BASE64解码
     *
     * @param data base64数据字符串
     * @param key  key
     * @return 原数据
     */
    public static String decryptFromBase64(String data, String key) {
        try {
            byte[] originalData = Base64.getDecoder().decode(data.getBytes());
            byte[] valueByte = decrypt(originalData, key.getBytes(ConfigureEncryptAndDecrypt.CHAR_ENCODING));
            return new String(valueByte, ConfigureEncryptAndDecrypt.CHAR_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    public static String encryptWithKeyBase64(String data, String key) {
        try {
            byte[] valueByte = encrypt(data.getBytes(ConfigureEncryptAndDecrypt.CHAR_ENCODING),
                    Base64.getDecoder().decode(key.getBytes()));
            return new String(Base64.getEncoder().encode(valueByte));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    public static String decryptWithKeyBase64(String data, String key) {
        try {
            byte[] originalData = Base64.getDecoder().decode(data.getBytes());
            byte[] valueByte = decrypt(originalData, Base64.getDecoder().decode(key.getBytes()));
            return new String(valueByte, ConfigureEncryptAndDecrypt.CHAR_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }

    public static byte[] generateRandomKey() {
        KeyGenerator keygen;
        try {
            keygen = KeyGenerator.getInstance(ConfigureEncryptAndDecrypt.AES_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(" genarateRandomKey fail!", e);
        }
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        Key key = keygen.generateKey();
        return key.getEncoded();
    }

    public static String generateRandomKeyWithBase64() {
        return new String(Base64.getEncoder().encode(generateRandomKey()));
    }

    public static String cryptPwd(String src, String key) {
        char item;
        char keyItem;
        int i = 0;
        int j = 0;
        int len;
        char[] destChars = new char[src.length()];
        char[] srcChars = src.toCharArray();
        char[] keyChars = key.toCharArray();
        for (len = src.length(); i < len; i++) {
            item = srcChars[i];
            keyItem = keyChars[j];
            destChars[i] = (char) ((item & 0xF0) + ((item & 0x0F) ^ (keyItem & 0x0F)));
            j++;
            if (j >= key.length()) {
                j = 0;
            }
        }
        return String.valueOf(destChars);
    }

    public static void main(String[] s) {
        String key = "abcdef123456";
        String s2 = cryptPwd("{jn4367237571:", key);
        System.out.println(s2);
    }
}
