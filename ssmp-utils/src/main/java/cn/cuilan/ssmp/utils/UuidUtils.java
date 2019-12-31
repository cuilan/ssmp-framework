package cn.cuilan.ssmp.utils;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author ahang.yan
 * @date 2019-12-31
 */
public class UuidUtils {

    /**
     * 生成UUID
     *
     * @return 返回UUID
     */
    public static String createUuid() {
        return UUID.randomUUID().toString();
    }

}
