package cn.cuilan.ssmp.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * RSA非对称加密
 */
@Slf4j
public class RSAEncryptUtils {

    /**
     * 加密算法RSA
     */
    private static final String RSA_ALGORITHM = "RSA";

    /**
     * 公钥名称
     */
    private static final String PUBLIC_KEY = "PUBLIC_KEY";

    /**
     * 私钥名称
     */
    private static final String PRIVATE_KEY = "PRIVATE_KEY";

    /**
     * 公钥信息
     */
    private static final String PUBLIC_KEY_INFO = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMNCDh1lGQmjhy" +
            "GPCds2oxDvgr/JkqnAg6lHvCGCLteGhrwRZaTVPcfqprik3wFcfPEP82xGuRqk6/zNHpuBckPXV+2tQgfwNl/w5n4E" +
            "focuY4lA68VpoHSt0Yk/n5fFM2SjUVay/WC6O7q8Z7APkaidz1E0qH4wzXQPg0rLRekQIDAQAB";

    /**
     * 私钥信息
     */
    private static final String PRIVATE_KEY_INFO =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMw0IOHWUZCaOHIY" +
                    "8J2zajEO+Cv8mSqcCDqUe8IYIu14aGvBFlpNU9x+qmuKTfAVx88Q/zbEa5GqTr/M" +
                    "0em4FyQ9dX7a1CB/A2X/DmfgR+hy5jiUDrxWmgdK3RiT+fl8UzZKNRVrL9YLo7ur" +
                    "xnsA+RqJ3PUTSofjDNdA+DSstF6RAgMBAAECgYEAs2+LBWfCPIKH3xqzNKAXTAyP" +
                    "XneUT7DUOkWHikKTToBjoWwGLEuOyU40ilL5sWIyNW4GFSX8L/+rOzbLrcgp7AiP" +
                    "CF61GEL28goqDgE+A8zKuxnF5rQNk04h0ccLthPQld0pNxXYaoFC4m7oO0zpQUD2" +
                    "Y9dOxxoc+KwHNj4SYsECQQDmogN0dMvQk8rgbdNt2Ypu9et5keJhb6HSCk2vxuVS" +
                    "iI2TQyl9buSMK9TvBxZKpAtDaoWZ3OQnaOvO87mJooBFAkEA4qnwu263Ytm0HY9Z" +
                    "TxTZwxQPatHqd84PW4hFUCTv/3Es10+3wxSRb1WaKYppnWZwucLySP9mxiqnyEs0" +
                    "pT7H3QJAbqQqpCOj41wDZ8dINtq13qV7Ycbqo3O6Xdkzi7APM5ju0kbWEKayXcxp" +
                    "SoJL32LzRFRF45pkmcNr3MxvpeefQQJAQllcjoEqTVVM1BpSGkQQOnp0yOAJsmgv" +
                    "Nqv9Hiix2CRY6+357LDooZ59MgvCPsDt3nKiWZvpibiSxJ6/Gl/2QQJADlZ88Obx" +
                    "tnqcoUwiM4S/1clHyLJdz2YwMpyZvTcLpX5sAM4gfhTQbiP9swGq9DuuFCRHDunT" +
                    "EzjvPn5p3E/Kbw==";

    /**
     * RSA最大加密明文分段大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文分段大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    private static Map<String, RSAKey> keyCache = new HashMap<>();

    static {
        keyCache.put(PUBLIC_KEY, getPublicKey());
        keyCache.put(PRIVATE_KEY, getPrivateKey());
    }

    /**
     * 加载公钥
     */
    private static RSAPublicKey getPublicKey() {
        byte[] keyBytes = Base64.getDecoder().decode(PUBLIC_KEY_INFO.getBytes());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf;
        RSAPublicKey publicKey = null;
        try {
            kf = KeyFactory.getInstance(RSA_ALGORITHM);
            publicKey = (RSAPublicKey) kf.generatePublic(spec);
        } catch (Exception e) {
            log.error(String.format("RSAEncryptUtils error: %s", e.getMessage()), e);
        }
        return publicKey;
    }

    /**
     * 加载私钥
     */
    private static RSAPrivateKey getPrivateKey() {
        byte[] keyBytes = Base64.getDecoder().decode(PRIVATE_KEY_INFO.getBytes());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf;
        RSAPrivateKey privateKey = null;
        try {
            kf = KeyFactory.getInstance(RSA_ALGORITHM);
            privateKey = (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (Exception e) {
            log.error(String.format("RSAEncryptUtils error: %s", e.getMessage()), e);
            e.printStackTrace();
        }
        return privateKey;
    }

    /**
     * RSA加密
     *
     * @param data byte数组
     * @return 返回加密后的byte数组
     */
    public static byte[] rsaEncrypt(byte[] data) {
        Assert.isTrue(keyCache.get(PUBLIC_KEY) != null, "public key is null.");

        RSAPublicKey pubKey = (RSAPublicKey) keyCache.get(PUBLIC_KEY);
        byte[] encryptedData = null;
        ByteArrayOutputStream out = null;
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            int inputLen = data.length;
            out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段加密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_ENCRYPT_BLOCK;
            }
            encryptedData = out.toByteArray();
        } catch (Exception e) {
            log.error(String.format("RSAEncryptService error: %s", e.getMessage()), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return encryptedData;
    }

    /**
     * RSA解密
     *
     * @param data 加密数据byte数组
     * @return 解密数据byte数组
     */
    public static byte[] rsaDecrypt(byte[] data) {
        Assert.isTrue(keyCache.get(PRIVATE_KEY) != null, "private key is null.");

        RSAPrivateKey privateKey = (RSAPrivateKey) keyCache.get(PRIVATE_KEY);
        byte[] decryptData = null;
        ByteArrayOutputStream out = null;
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            int inputLen = data.length;
            out = new ByteArrayOutputStream();
            int offSet = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offSet > 0) {
                if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offSet, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offSet, inputLen - offSet);
                }
                out.write(cache, 0, cache.length);
                i++;
                offSet = i * MAX_DECRYPT_BLOCK;
            }
            decryptData = out.toByteArray();
        } catch (Exception e) {
            log.error(String.format("RSAEncryptService error: %s", e.getMessage()), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return decryptData;
    }

    /**
     * RSA加密并转为16进制字符串
     *
     * @param data byte数组
     * @return 返回16进制字符串
     */
    public static String rsaEncryptToString(byte[] data) {
        return Hex.encodeHexString(rsaEncrypt(data));
    }

    /**
     * RSA解密，通过16进制字符串形式的密文
     *
     * @param data 16进制形式的密文
     * @return 返回明文
     */
    public static String rasDecryptFromString(String data) {
        byte[] bytes = null;
        try {
            bytes = rsaDecrypt(Hex.decodeHex(data));
        } catch (DecoderException e) {
            log.error(String.format("Hex to decode error: %s", e.getMessage()), e);
        }
        return bytes == null ? null : new String(bytes);
    }
}
