package cn.cuilan.framework.utils.service;

import cn.cuilan.framework.utils.GsonUtil;
import cn.cuilan.framework.utils.HttpUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ShortUrlUtil {

    private static String shortDomainBase = "http://chouti.cc/";
    private static String baseUrl = "http://chouti.cc/shortUrl/create?secret_key=f711d1bc01c5795d6dc&urls=";
    private static String weiboApiUrl = "https://api.weibo.com/2/short_url/shorten.json?source=3454702602&url_long=";

    /**
     * @param oriUrl 单个原始url
     * @return Map key为shotUrl 时, value为对应的短连接地址
     */
    public static String genShotUrl(String oriUrl) {

        return getShortUrlByWeibo(oriUrl);
    }

    public static String genShotUrlByCT(String oriUrl) {
        Map<String, String> shortUrlMap = getShortUrl(new String[]{oriUrl});

        if (shortUrlMap == null) {
            return null;
        }

        return shortUrlMap.get(oriUrl);
    }

    /**
     * @param oriUrlArr 多个 原始url
     * @return Map key:对应一个原始url, value:对应原始url的短连接
     */
    public static Map<String, String> getShortUrl(String[] oriUrlArr) {


        try {
            String oriUrls = "";
            for (String oriUrl : oriUrlArr) {
                oriUrls += oriUrl + ",";
            }
            oriUrls = oriUrls.substring(0, oriUrls.lastIndexOf(","));
            String encodeOriUrl = URLEncoder.encode(oriUrls, "UTF-8");


            String url = baseUrl + encodeOriUrl;

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder().get().url(url).build();

            String respStr = client.newCall(request).execute().body().string();

            Map<String, Object> respMap = GsonUtil.toMap(respStr);

            int code = respMap.get("code") == null ? -1 : Double.valueOf(respMap.get("code").toString()).intValue();
            if (99999 == code) {
                Map<String, String> urlsMap = GsonUtil.toStrMap(GsonUtil.toJsonString(respMap.get("urls")));
                Set<String> keySet = urlsMap.keySet();
                for (String key : keySet) {
                    urlsMap.put(key, shortDomainBase + urlsMap.get(key));
                }
                return urlsMap;
            } else {
                log.error("get short url fail resp = {}", respStr);
            }

        } catch (Exception e) {
            log.error("get short url fail Exception = {}", e);
        }
        return null;
    }


    public static String getShortUrlByWeibo(String oriUr) {
        String resultJson = "";
        try {
            String encodeOriUrl = URLEncoder.encode(oriUr, "UTF-8");
            String url = weiboApiUrl + encodeOriUrl;
            for (int i = 0; i < 1; i++) {
                String respStr = HttpUtils.doGet(url);
                if (i == 2) {
                    log.info("重试三次获取短链后, weibo api 返回结果: {}", respStr);
                }
                resultJson = respStr;
                if (StringUtils.isBlank(respStr)) {
                    continue;
                }
                log.info("短链调试: {}", respStr);
                JsonObject respJson = GsonUtil.toJsonObj(respStr);
                JsonArray urls = respJson.getAsJsonArray("urls");
                for (JsonElement urlResult : urls) {
                    JsonObject urlJson = urlResult.getAsJsonObject();
                    boolean tmpResult = urlJson.get("result").getAsBoolean();
                    if (tmpResult) {
                        String url_short = urlJson.get("url_short").getAsString();
                        return url_short;
                    }
                }
            }
        } catch (Exception e) {
            log.error("get short url fail Exception", e);
        }
        log.warn("获取短链异常，直接返回原始地址");
        return oriUr;
    }

    public static void main(String[] args) {

        String res = getShortUrlByWeibo("https://dig.chouti.com/share/link?link_id=26696356");
        System.out.println(res);
    }
}
