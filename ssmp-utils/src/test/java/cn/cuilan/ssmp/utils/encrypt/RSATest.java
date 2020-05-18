package cn.cuilan.ssmp.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
@Slf4j
public class RSATest {

    @Test
    public void testRsaEncrypt() {
        String src = "2017年10月18日，中国共产党第十九次全国代表大会开幕。" +
                "习近平代表第十八届中央委员会向大会作了题为《决胜全面建成小康社会 夺取新时代中国特色社会主义伟大胜利》的报告。" +
                "习近平指出，这次大会的主题是：不忘初心，牢记使命，高举中国特色社会主义伟大旗帜，决胜全面建成小康社会，夺取新" +
                "时代中国特色社会主义伟大胜利，为实现中华民族伟大复兴的中国梦不懈奋斗。";
        String encrypt = RSAUtils.rsaEncryptToString(src.getBytes());
        log.info("RSA encrypt: {}", encrypt);
    }

    @Test
    public void testRsaDecrypt() {
        String encrypt = "49cb3786933c0d1d11fd72050422b07e305dbed4119ce5037afeaec3e2c368f5a233d83c22d3e69bf55950e" +
                "b49c13bc80423c5eb4a0d578ef271b94449fb061f81e6627d4cd82912379ac2cf51b42a7ede4055c71c585abcf01572c" +
                "713708239cd6199ebf8e789a59ebf91e1b691d38b4ecf3adce3705bbde7b7c2a1f6883e745158890148067fe2da04018" +
                "0d8e64818962a8e6e5122bd62463b871d11f2152951ab5985493ac882ceaf95c09939111e469728347f958c9eac4c827" +
                "c790c56671e8e45bc6ca1ce4e8d1e0975b700bee035e311653e6a9f6b0ca1fd282c2918607628edb5aabee17299d5426" +
                "5dbbd35f5a33a5069c862e809ef2cf6b7133f21d623d6d9d0f1837f2e769b84387a76cfc4b15e8dd14b712edb01fd174" +
                "e85a341d7d28d7fd74ae5a184db8792eac18666bac7be7fef248900f1f5110c95a5d5a2ffdff157f63fb3a664b20f2c1" +
                "8d54018b49a0c944a20ee61bedba804c176ca8eeb7bb5289b0019a660566c9d5762e2eb8390c755155625a7da2e3e182" +
                "60de2c32b2ef89bc12c2b6d6f2978df79a443085bf95dcbf95840d10fd0b6752bc4b608bcf8baeefc2ad74fbdcef3054" +
                "889fa3591b6d46fc86b810d1a6c25b9249e35033936edd7e3ab47b2714525ef9621ea0a2e6bf9c544191ef5b01f35c1c" +
                "41ad6de1b2a0c8530ca44265a3fb16b50e7391f32bfb4423d363fcc2cdee8c316e75e2fc4a1dc04aa0e519eab43ad212" +
                "ddb621157a4e117dabdb21ebca33f2b16483ea1ff6cd5ecf134a1518b673fd318f095157dcb441cb40ebf3bf20717765" +
                "3b7e9d9569a4c8f6598abb9024a5e938a092c072f64a774a3be9f87ee272ee0b09a31d6928a08f29445a06b104c6bd2d" +
                "d9126e3242496dd84ab83f1b8312da922bb41dd1e";
        long start = System.currentTimeMillis();
        String src = RSAUtils.rasDecryptFromString(encrypt);
        log.info("耗时: {}ms", System.currentTimeMillis() - start);
        log.info("RSA decrypt: {}", src);
    }

}
