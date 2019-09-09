package cn.cuilan.framework.utils;

import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class ObjectUtil {

    public static boolean isDiff(Object objA, Object objB) {
        JSONObject jsonObjA = (JSONObject) JSONObject.toJSON(objA);
        JSONObject jsonObjB = (JSONObject) JSONObject.toJSON(objB);

        Iterator<Map.Entry<String, Object>> iter = jsonObjA.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            String entryKey = (String) entry.getKey();
            if (entry.getValue() != null && (!Objects.equals("id", entryKey))) {
                if (!Objects.equals(entry.getValue(), jsonObjB.get(entryKey))) {
                    return false;
                }
            }
        }
        return true;
    }

}
