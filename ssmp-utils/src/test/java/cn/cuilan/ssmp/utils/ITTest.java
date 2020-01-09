package cn.cuilan.ssmp.utils;

import cn.cuilan.ssmp.utils.encrypt.AESUtils;
import cn.cuilan.ssmp.utils.encrypt.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ITTest {

    private static final String srcName = "C:\\Users\\Administrator\\Desktop\\123\\data.zip";

    private static final String encryptName = "C:\\Users\\Administrator\\Desktop\\123\\data.encrypt";

    @Test
    public void rsaEncryptFile() {
        byte[] bytes = FileReadWriteUtils.readFileBytes(srcName);
        byte[] encryptData = RSAUtils.rsaEncrypt(bytes);
        FileReadWriteUtils.writeFile(encryptData, encryptName);
    }

    @Test
    public void rsaDecryptFile() {
        byte[] bytes = FileReadWriteUtils.readFileBytes(encryptName);
        byte[] decryptData = RSAUtils.rsaDecrypt(bytes);
        FileReadWriteUtils.writeFile(decryptData, "C:\\Users\\Administrator\\Desktop\\123\\test_1.gif");
    }

    // 秘钥
    private static final String AES_KEY = "/eS9zFpsHyz4PSKb44aaJw==";

    @Test
    public void aesEncryptFile() {
        long start = System.currentTimeMillis();
        byte[] bytes = FileReadWriteUtils.readFileBytes(srcName);
        byte[] encryptData = AESUtils.encrypt(bytes, AESUtils.base64DecodeKey(AES_KEY));
        FileReadWriteUtils.writeFile(encryptData, encryptName);
        System.out.println("加密耗时: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void aesDecryptFile() {
        long start = System.currentTimeMillis();
        byte[] bytes = FileReadWriteUtils.readFileBytes(encryptName);
        byte[] decryptData = AESUtils.decrypt(bytes, AESUtils.base64DecodeKey(AES_KEY));
        FileReadWriteUtils.writeFile(decryptData, "C:\\Users\\Administrator\\Desktop\\123\\data_1.zip");
        System.out.println("解密耗时: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void test() {
        byte[] key = AES_KEY.getBytes();//AESUtils.base64DecodeKey(AES_KEY);
        FileReadWriteUtils.writeFile(key, "C:\\Users\\Administrator\\Desktop\\123\\secret.key");

    }
}
