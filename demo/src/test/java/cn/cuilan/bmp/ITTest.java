package cn.cuilan.bmp;

import org.junit.Test;

import java.util.HashMap;
import java.util.Random;

/**
 * @author zhang.yan
 * @date 2019/9/10
 */
public class ITTest {

    @Test
    public void test() {
        HashMap<Person, String> map = new HashMap<>();

        for (int i = 0; i < 1000; i++) {
            String value = String.valueOf(new Random(100).nextInt());
            map.put(new Person(), value);
        }

        System.out.println(map.size());
    }

    static class Person {
        @Override
        public int hashCode() {
            return new Random(100).nextInt();
        }
    }

}
