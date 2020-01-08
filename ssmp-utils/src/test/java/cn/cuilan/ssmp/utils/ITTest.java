package cn.cuilan.ssmp.utils;

import cn.cuilan.ssmp.utils.encrypt.RSAUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class ITTest {

    private static final String srcName = "C:\\Users\\Administrator\\Desktop\\image\\test.gif";

    private static final String encryptName = "C:\\Users\\Administrator\\Desktop\\image\\test.encrypt";

    @Test
    public void encryptFile() {
        byte[] bytes = FileReadWriteUtils.readFileBytes(srcName);
        byte[] encryptData = RSAUtils.rsaEncrypt(bytes);
        FileReadWriteUtils.writeFile(encryptData, encryptName);
    }

    @Test
    public void decryptFile() {
        byte[] bytes = FileReadWriteUtils.readFileBytes(encryptName);
        byte[] decryptData = RSAUtils.rsaDecrypt(bytes);
        FileReadWriteUtils.writeFile(decryptData, "C:\\Users\\Administrator\\Desktop\\image\\test_1.gif");
    }

}
