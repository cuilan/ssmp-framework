package cn.cuilan.framework.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.GsonBuilder;
import com.qiniu.http.Client;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import com.qiniu.util.UrlSafeBase64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;

public class QiniuUtils {

    /**
     * 拼接审核body
     *
     * @param imgUrl 图片url
     */
    public static JSONObject getPostBodyOfImgCheck(String imgUrl) {
        JSONObject jsonObject = new JSONObject();
        JSONObject uriObj = new JSONObject();
        uriObj.put("uri", imgUrl);
        JSONObject typeObj = new JSONObject();
        String[] type = new String[]{"pulp", "terror", "politician"};
        typeObj.put("type", type);
        jsonObject.put("data", uriObj);
        jsonObject.put("params", typeObj);
        return jsonObject;
    }

    public static byte[] objToBytes(Object obj, String charset) {
        return new GsonBuilder().serializeNulls().create().toJson(obj).getBytes(Charset.forName(charset));
    }

    public static StringMap authorizationV2(String url, String method, byte[] body, String contentType, String aKey, Mac mac) {
        String authorization = "Qiniu " + signRequestV2(url, method, body, contentType, aKey, mac);
        return new StringMap().put("Authorization", authorization);
    }

    /**
     * 生成HTTP请求签名字符串
     */
    private static String signRequestV2(String urlString, String method, byte[] body, String contentType, String aKey, Mac mac) {
        URI uri = URI.create(urlString);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s %s", method, uri.getPath()));
        if (uri.getQuery() != null) {
            sb.append(String.format("?%s", uri.getQuery()));
        }

        sb.append(String.format("\nHost: %s", uri.getHost()));
        if (uri.getPort() > 0) {
            sb.append(String.format(":%d", uri.getPort()));
        }

        if (contentType != null) {
            sb.append(String.format("\nContent-Type: %s", contentType));
        }

        sb.append("\n\n");
        if (body != null && body.length > 0 && !StringUtils.isNullOrEmpty(contentType)) {
            if (contentType.equals(Client.FormMime)
                    || contentType.equals(Client.JsonMime)) {
                sb.append(new String(body));
            }
        }

        mac.update(StringUtils.utf8Bytes(sb.toString()));

        String digest = UrlSafeBase64.encodeToString(mac.doFinal());
        return aKey + ":" + digest;
    }


    public static Mac createMac(String aKey, String sKey) {
        if (StringUtils.isNullOrEmpty(aKey) || StringUtils.isNullOrEmpty(sKey)) {
            throw new IllegalArgumentException("empty key");
        }
        byte[] sk = StringUtils.utf8Bytes(sKey);
        SecretKeySpec secretKey = new SecretKeySpec(sk, "HmacSHA1");

        Mac mac;
        try {
            mac = javax.crypto.Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        return mac;
    }
}
