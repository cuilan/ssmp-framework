package cn.cuilan.framework.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static final Pattern URL_PATTERN = Pattern.compile(
            "((?:https|http)://)" + "(?:[0-9a-z_!~*'()-]+\\.)*" // 域名-
                    // www.
                    + "(?:[0-9a-z][0-9a-z!~*#&'.^:@+$%-]{0,61})?[0-9a-z]\\." // 二级域名
                    // ,可以含有符号
                    + "[a-z]{0,6}" // .com
                    + "(?::[0-9]{1,4})?" // 端口
                    + "(?:/[0-9A-Za-z_!~*'().?:@&=+,$%#-]*)*" // 除了域名外中间参数允许的字符
                    + "[0-9A-Za-z-/?~#%*&()$+=^]", Pattern.CASE_INSENSITIVE); // 指定可以作为结尾的字符

    /**
     * 计算字符长度，汉字长度是2，字母，数字或者符号为1
     */
    public static Integer getLength(String source) {
        if (StringUtils.isEmpty(source)) {
            return 0;
        }

        String temp = source;
        temp = temp.trim();
        int length = 0;
        for (int i = 0; i < temp.length(); i++) {
            char c = temp.charAt(i);
            // 单字节加1
            if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
                length++;
            } else {
                length += 2;
            }
        }
        return length;
    }

    /**
     * 验证url是否合法
     *
     * @param url
     * @return
     */
    public static boolean isURL(String url) {

        if (StringUtils.isEmpty(url)) {
            return false;
        }


        try {
            url = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        url = addURLProtocol(url);

        String matches = "^http(s)?://[\\S]{1,}(\\.[\\S]{1,}){1,}[\\S]{1,}";

        if (!url.matches(matches)) {
            return false;
        }

        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String addURLProtocol(String url) {
        String urlLower = url.toLowerCase().trim();

        if (urlLower.startsWith("http://")) {
            return url;
        }
        if (urlLower.startsWith("https://")) {
            return url;
        }
        return "http://" + url;
    }

    public static String getURLHost(String urlStr) {
        if (StringUtils.isBlank(urlStr)) {
            return "";
        }
        urlStr = addURLProtocol(urlStr);
        try {
            URL url = new URL(urlStr);
            return url.getHost().toLowerCase();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String removeURLHost(String imgUrl) {
        if (StringUtils.isEmpty(imgUrl) || ChoutiWeb.getUrlIndex(imgUrl) == -1) {
            return imgUrl;
        }
        String domain = getURLHost(imgUrl);
        imgUrl = imgUrl.substring(imgUrl.indexOf(domain) + domain.length());
        return imgUrl;
    }
    public static String URLDecode(String url){
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
    public static String URLEncode(String url){
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String removeUrl(String text) {
        Matcher matcher = URL_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.replaceAll("");
        }
        return text;
    }

    /**
     * 过滤非UTF-8 字符
     */
    public static String removeOutOfUTF8(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        try {
            StringBuilder retval = new StringBuilder();
            char ch;

            for (int i = 0; i < text.length(); i++) {
                ch = text.charAt(i);

                // Strip all non-characters http://unicode.org/cldr/utility/list-unicodeset.jsp?a=[:Noncharacter_Code_Point=True:]
                // and non-printable control characters except tabulator, new line and carriage return
                if (ch % 0x10000 != 0xffff && // 0xffff - 0x10ffff range step 0x10000
                        ch % 0x10000 != 0xfffe && // 0xfffe - 0x10fffe range
                        (ch <= 0xfdd0 || ch >= 0xfdef) && // 0xfdd0 - 0xfdef
                        (ch > 0x1F || ch == 0x9 || ch == 0xa || ch == 0xd)) {

                    retval.append(ch);
                }
            }

            return retval.toString();
        } catch (Exception ignored) {

        }
        return text;
    }
    private static final ArrayList<String> imageType = new ArrayList<String>() {{
        add("png");
        add("jpg");
        add("jpeg");
        add("gif");
    }};
    public static boolean isImgUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        if (url.contains("http://img1.chouti.com")
                || url.contains("http://dohko.img.gozap.com")) {
            return true;
        }
        String prefix = url.substring(url.lastIndexOf(".") + 1);
        if (StringUtils.isNotEmpty(prefix)){
            for (String type : imageType) {
                if (prefix.equalsIgnoreCase(type)) {
                    return true;
                }
            }
        }
        return false;
    }
    public static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }
}
