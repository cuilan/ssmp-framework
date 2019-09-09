package cn.cuilan.framework.utils;

import org.apache.commons.lang3.StringUtils;

public class VoGetUtils {

    public static Integer resultToInt(Object object) {
        if (object == null) {
            return 0;
        }
        String countStr = object.toString();
        Integer count = 0;
        if (StringUtils.isNotBlank(countStr)) {
            count = Integer.parseInt(countStr);
        }
        return count;
    }

    public static Long resultToLong(Object object) {
        if (object == null) {
            return 0L;
        }
        String countStr = object.toString();
        Long count = 0L;
        if (StringUtils.isNotBlank(countStr)) {
            count = Long.parseLong(countStr);
        }
        return count;
    }

    public static Boolean resultToBoolean(Object object) {
        Boolean value =  Boolean.FALSE;
        if (object == null) {
           return value;
        }
        String countStr = object.toString();
        if (StringUtils.isNotBlank(countStr)) {
            value = Boolean.valueOf(countStr);
        }
        return value;
    }

}
