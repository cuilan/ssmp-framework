package cn.cuilan.ssmp.utils.encrypt;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
@Slf4j
public class MD5Test {

    private final static String src = "2017年10月18日，中国共产党第十九次全国代表大会开幕。" +
            "习近平代表第十八届中央委员会向大会作了题为《决胜全面建成小康社会 夺取新时代中国特色社会主义伟大胜利》的报告。" +
            "习近平指出，这次大会的主题是：不忘初心，牢记使命，高举中国特色社会主义伟大旗帜，决胜全面建成小康社会，夺取新" +
            "时代中国特色社会主义伟大胜利，为实现中华民族伟大复兴的中国梦不懈奋斗。";

    @Test
    public void testMd5ToHex() {
        String md5 = MD5Utils.md5ToHex(src);
        log.info(md5);
    }

    @Test
    public void testMd5ToHexWithRandomSalt() {
        String md51 = MD5Utils.md5ToHexWithRandomSalt(src);
        log.info(md51);
        log.info(String.valueOf(MD5Utils.verify(src, md51)));

        String md52 = MD5Utils.md5ToHexWithRandomSalt(src);
        log.info(md52);
        log.info(String.valueOf(MD5Utils.verify(src, md51)));

        String md53 = MD5Utils.md5ToHexWithRandomSalt(src);
        log.info(md53);
        log.info(String.valueOf(MD5Utils.verify(src, md51)));

        String md54 = MD5Utils.md5ToHexWithRandomSalt(src);
        log.info(md54);
        log.info(String.valueOf(MD5Utils.verify(src, md51)));

        String md55 = MD5Utils.md5ToHexWithRandomSalt(src);
        log.info(md55);
        log.info(String.valueOf(MD5Utils.verify(src, md51)));
    }
}
