package cn.cuilan.ssmp.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class AESTest {

    // 明文
    private static final String data = "测试一下AES加密";

    // 密文
    private static String encryptData = "5CijQJ1uEeggCBANS1iCE5EKmyTDFF4S9R8hXBMYIWw=";

    // 秘钥
    private static String key = "/eS9zFpsHyz4PSKb44aaJw==";

    @Test
    public void testGenerateRandomKeyWithBase64() {
        String keyWithBase64 = AES.generateRandomKeyWithBase64();
        log.info("Generate AES key with Base64: {}", keyWithBase64);
    }

    @Test
    public void testEncryptWithKeyBase64() {
        log.info("Generate AES key with Base64: {}", key);
        // 加密
        encryptData = AES.encryptWithKeyBase64(data, key);
        log.info("data: {}, Encrypt to Base64: {}", data, encryptData);
    }

    @Test
    public void testDecryptWithKeyBase64() {
        // 解密
        String sourceData = AES.decryptWithKeyBase64(encryptData, key);
        log.info("Decrypt data: {}", sourceData);
    }

}
