package cn.cuilan.framework.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class GsonUtil {

    private static Gson gson = new Gson();
    public static String toJsonString(Object obj){
        return gson.toJson(obj);
    }

    public static Map<String,String> toStrMap(String jsonStr){
        return gson.fromJson(jsonStr,new TypeToken<Map<String,String>>(){}.getType());
    }
    public static Map<String,Object> toMap(String jsonStr){
        return gson.fromJson(jsonStr,new TypeToken<Map<String,Object>>(){}.getType());
    }
    public static <T> T toObject(String jsonStr,Class<T> cls){
        return gson.fromJson(jsonStr,cls);
    }

    public static JsonObject toJsonObj(String jsonStr){

        return gson.fromJson(jsonStr,JsonObject.class);


    }
}
