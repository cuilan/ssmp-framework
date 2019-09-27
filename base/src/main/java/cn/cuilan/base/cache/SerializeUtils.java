package cn.cuilan.base.cache;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.nio.charset.Charset;

public class SerializeUtils {

    private static Charset utf8 = Charset.forName("utf-8");


    public static byte[] serialize(Object v) {
        return JSONObject.toJSONString(v, SerializerFeature.WriteClassName).getBytes(utf8);
    }

    @SuppressWarnings("unchecked")
    public static <V> V deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return (V) JSONObject.parse(new String(bytes, utf8), Feature.SupportAutoType);
    }

}
