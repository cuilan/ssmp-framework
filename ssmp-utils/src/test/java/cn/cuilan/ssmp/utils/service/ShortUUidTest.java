package cn.cuilan.ssmp.utils.service;

import org.junit.Test;

public class ShortUUidTest {

    @Test
    public void generateShortUuid() {
        for (int i = 0; i < 10; i++) {
            System.out.println(ShortUUid.generateShortUuid());
        }
    }
}
