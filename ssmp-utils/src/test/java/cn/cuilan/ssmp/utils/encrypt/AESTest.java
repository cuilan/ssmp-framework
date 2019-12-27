package cn.cuilan.ssmp.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class AESTest {

    // 明文
    private static final String data = "2017年10月18日，中国共产党第十九次全国代表大会开幕。" +
            "习近平代表第十八届中央委员会向大会作了题为《决胜全面建成小康社会 夺取新时代中国特色社会主义伟大胜利》的报告。" +
            "习近平指出，这次大会的主题是：不忘初心，牢记使命，高举中国特色社会主义伟大旗帜，决胜全面建成小康社会，夺取新" +
            "时代中国特色社会主义伟大胜利，为实现中华民族伟大复兴的中国梦不懈奋斗。";

    // 密文
    private static String encryptData = "QNvYflWzYmi8diJtq0MCr1bUebICGzwAPyi1af8ipUa/R6OPcSSp92hNWEVwJhnE7rpH" +
            "1c6ytbxrfEeeLSfZ/iYyklrC0LomKdiUiKyNqaBO8Fk741v8s7eAHkeRi7/cS4WuW4wE2bKn8ElbDInkLki3prl/sesI5e8T" +
            "dYfO2Ld/B9UYu/u003ir+2n+j1sh+08hDiYhi2p6Z3fHKIIGjqtrrbakcUsgpaGbvMNsoMOjgyOZWwNfofQF7NN307jFETdU" +
            "QAOWf543cI2FqSLsSx13PXeRYptlkulXoaWEddqoWjhoFf4hOPRCbcVOQ7/XC1lqRRCP3Ve5jS373icxgViiUQCptelF03Zh" +
            "IZSbrtRU2HX2CHzmvska0OSooP9C6b0qDmmPmz4FaA4qnxw8vlQpOh9Rufrff+i3y1LX0Tysx1kveNKE7Ovho3PbHCPX1fM6" +
            "x8lK3mGLD1BulqXOPCbn1D5C6IxRz0fEBkpWJci4deXCh6SbHh44YI7xE3D8IEOz4vIw8U1PwIF0ID6OEI1QPDiOOsQ6XCc6" +
            "26SO1tlA0HK1z6TGi7ptQSpABTIgb4+Rn5RVdjZ9+FrfN2wV4TaDUInecvNKn4VhnpeplQosWOd+ZZmT6lu9Uj7Dg8wPi0mJ" +
            "qnHLCe4OfXUGidFoGLhxq3s6Qhb6rsc/+mQRRiU=";

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
        log.info("data: {}", data);
        log.info("Encrypt to Base64: {}", encryptData);
    }

    @Test
    public void testDecryptWithKeyBase64() {
        // 解密
        String sourceData = AES.decryptWithKeyBase64(encryptData, key);
        log.info("Decrypt data: {}", sourceData);
    }

}
