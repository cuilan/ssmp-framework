package cn.cuilan.framework.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 校验工具类
 * 验证对象是否为 null，空字符串(只有空格的字符串也认为是空串)，空数组，空的 Collection，或空Map
 *
 * @author zhang.yan
 */
public class CheckUtils {

    /**
     * 验证对象是否为 null，空字符串(只有空格的字符串也认为是空串)，空数组，空的 Collection，或空Map
     *
     * @param obj     被验证的对象
     * @param message 异常信息
     */
    @SuppressWarnings("rawtypes")
    public static void notEmpty(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof String && obj.toString().trim().length() == 0) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            throw new IllegalArgumentException(message + " must be specified");
        }
        if (obj instanceof Map && ((Map) obj).isEmpty()) {
            throw new IllegalArgumentException(message + " must be specified");
        }
    }


}
