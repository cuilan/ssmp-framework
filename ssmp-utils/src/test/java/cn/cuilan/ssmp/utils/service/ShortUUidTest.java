package cn.cuilan.ssmp.utils.service;

import cn.cuilan.ssmp.utils.ShortUUid;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class ShortUUidTest {

    @Test
    public void generateShortUuid() {
        for (int i = 0; i < 10; i++) {
            System.out.println(ShortUUid.generateShortUuid());
        }
    }
}
