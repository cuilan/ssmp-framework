package cn.cuilan.ssmp.utils.encrypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DES3加密算法
 *
 * @author zhang.yan
 * @date 2020/1/16
 */
public class DESEncryptUtils {

    // TODO 三轮DES加密解密

    public static void main(String[] args) {
//        byte[] e = desEncrypt("12345678".getBytes(), "NoSuchAlgorithmException".getBytes());
        // A6B9654A886E019633B85FDCB995BA6DAAA7F8371BDA88F9
//        System.out.println(ByteUtils.bytesToHexString(e));
        String s = desDecrypt("12345678", "A6B9654A886E019633B85FDCB995BA6DAAA7F8371BDA88F9");
        System.out.println(s);
    }

    /**
     * 单DES加密算法
     *
     * @param key  十六进制
     * @param data 十六进制
     * @return 加密后的十六进制形式
     */
    public static String desEncrypt(String key, String data) {
        return ByteUtils.bytesToHexString(
                desEncrypt(ByteUtils.hexStringToByte(key),
                        ByteUtils.hexStringToByte(data)));
    }

    /**
     * 单DES解密算法
     *
     * @param key  十六进制
     * @param data 十六进制
     * @return 解密后的十六进制形式
     */
    public static String desDecrypt(String key, String data) {
        return ByteUtils.bytesToHexString(desDecrypt(ByteUtils.hexStringToByte(key), ByteUtils.hexStringToByte(data)));
    }

    /**
     * 单DES加密算法
     *
     * @param key  秘钥二进制数组
     * @param data 数据二进制数组
     * @return 加密后的二进制数组
     */
    public static byte[] desEncrypt(byte[] key, byte[] data) {
        return des(key, data, Cipher.ENCRYPT_MODE);
    }

    /**
     * 单DES解密算法
     *
     * @param key  秘钥二进制数组
     * @param data 数据二进制数组
     * @return 解密后的二进制数组
     */
    public static byte[] desDecrypt(byte[] key, byte[] data) {
        return des(key, data, Cipher.DECRYPT_MODE);
    }


    /**
     * DES基础加密/解密方法
     *
     * @param key    秘钥二进制数组
     * @param data   数据二进制数组
     * @param opmode 按照Cipher的opmode定义
     * @return 返回加密/解密后的二进制数组
     */
    private static byte[] des(byte[] key, byte[] data, int opmode) {
        byte[] result = null;
        // 密钥长度必须为8
        //if (key.length != 8) {
        //  throw new RuntimeException("expected length of des key is 8! [" + key.length + "]");
        //}
        // 数据长度必须为8的倍数
        if (data.length % 8 != 0) {
            throw new RuntimeException("expected length of des data must multiple of 8! [" + data.length + "]");
        }
        try {
            SecretKeyFactory keyFactory;
            DESKeySpec dks = new DESKeySpec(key);
            keyFactory = SecretKeyFactory.getInstance(EncryptAlgorithm.DES_ALGORITHM);
            SecretKey secretkey = keyFactory.generateSecret(dks);
            // 创建Cipher对象
            Cipher cipher = Cipher.getInstance(EncryptAlgorithm.DES_ECB_ALGORITHM);
            // 初始化Cipher对象
            cipher.init(opmode, secretkey);
            result = new byte[data.length];
            // 如果数据超过8位，循环每8位进行加解密，然后进行拼接
            int offset = 0;
            for (int i = 0; i < data.length / 8; i++) {
                // 需要处理的数据逐8位取出
                byte[] tmp = new byte[8];
                System.arraycopy(data, offset, tmp, 0, 8);
                // 进行加解密计算
                byte[] tmpResult = cipher.doFinal(tmp);
                // 放入返回结果中
                System.arraycopy(tmpResult, 0, result, offset, 8);
                offset += 8;
            }
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
        return result;
    }
}
