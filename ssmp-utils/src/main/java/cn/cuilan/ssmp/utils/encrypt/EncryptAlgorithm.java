package cn.cuilan.ssmp.utils.encrypt;

/**
 * 加密算法相关配置
 *
 * @author zhang.yan
 */
public class EncryptAlgorithm {

    /**
     * 字符编码
     */
    public static final String CHAR_ENCODING = "UTF-8";

    /**
     * MD5信息摘要算法
     */
    public static final String MD5_ALGORITHM = "MD5";

    /**
     * SHA-256
     */
    public static final String SHA_256_ALGORITHM = "SHA-256";

    /**
     * AES对称加密算法
     * 格式："算法" 或 "算法/模式/填充"
     */
    public static final String AES_ALGORITHM = "AES";
//    public static final String AES_ALGORITHM = "/CBC/PKCS5Padding";

    public static final String CBC_ALGORITHM = "CBC";

    public static final String ECB_ALGORITHM = "ECB";

    /**
     * DES对称加密
     */
    public static final String DES_ALGORITHM = "DES";

    /**
     * DES对称加密，ECB模式，不填充
     */
    public static final String DES_ECB_ALGORITHM = "DES/ECB/NoPadding";

    // -------------------------------------------------------

    /**
     * RSA非对称加密算法
     */
    public static final String RSA_ALGORITHM = "RSA";

}
