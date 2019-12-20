package cn.cuilan.ssmp.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPublicKey;
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
     * 公钥名称
     */
    private static final String PUBLIC_KEY = "PUBLIC_KEY";

    /**
     * 公钥信息
     */
    private static final String PUBLIC_KEY_INFO = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMNCDh1lGQmjhy" +
            "GPCds2oxDvgr/JkqnAg6lHvCGCLteGhrwRZaTVPcfqprik3wFcfPEP82xGuRqk6/zNHpuBckPXV+2tQgfwNl/w5n4E" +
            "focuY4lA68VpoHSt0Yk/n5fFM2SjUVay/WC6O7q8Z7APkaidz1E0qH4wzXQPg0rLRekQIDAQAB";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    private static Map<String, RSAKey> keyCache = new HashMap<>();


    static {
        keyCache.put(PUBLIC_KEY, getPublicKey());
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
            kf = KeyFactory.getInstance("RSA");
            publicKey = (RSAPublicKey) kf.generatePublic(spec);
        } catch (Exception e) {
            log.error(String.format("RSAEncryptUtils error:%s", e.getMessage()), e);
        }
        return publicKey;
    }

    /**
     * RSA加密
     */
    public static byte[] rsaEncrypt(byte[] data) {
        Assert.isTrue(keyCache.get(PUBLIC_KEY) != null, "public key is null.");

        RSAPublicKey pubKey = (RSAPublicKey) keyCache.get(PUBLIC_KEY);
        byte[] encryptedData = null;
        ByteArrayOutputStream out = null;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
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
            log.error(String.format("RSAEncryptService error:%s", e.getMessage()), e);
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

    public static String rsaEncryptToString(byte[] data) {
        return Hex.encodeHexString(rsaEncrypt(data));
    }

}
