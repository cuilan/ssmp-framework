package cn.cuilan.framework.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtils {

    public static final Pattern URL_PATTERN = Pattern.compile("((?:https|http)://)(?:[0-9a-z_!~*'()-]+\\.)*(?:[0-9a-z][0-9a-z!~*#&'.^:@+$%-]{0,61})?[0-9a-z]\\.[a-z]{0,6}(?::[0-9]{1,4})?(?:/[0-9A-Za-z_!~*'().?:@&=+,$%#-]*)*[0-9A-Za-z-/?~#%*&()$+=^]", 2);
    private static final Pattern[] DANGEROUS_TOKENS = new Pattern[]{Pattern.compile("^j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:", 2)};

    public static String removeHtmlTag(String source) {
        if (StringUtils.isEmpty(source)) {
            return "";
        }
        source = source.replaceAll(" \u200b", "").trim();
        if (StringUtils.isEmpty(source)) {
            return "";
        }
        Pattern pattern = Pattern.compile("<([^>]*)>");
        Matcher matcher = pattern.matcher(source);
        String string = matcher.replaceAll("");

        for (int i = 0; i < DANGEROUS_TOKENS.length; ++i) {
            string = DANGEROUS_TOKENS[i].matcher(source).replaceAll("");
        }

        string = string.replaceAll("(\\w)https?:", "$1");
        string = string.replaceAll("\r", "").trim();
        string = string.replaceAll("\n", "").trim();
        string = string.replaceAll("\t", "").trim();
        string = string.replaceAll("<", "&lt").trim();
        string = string.replaceAll(">", "&gt").trim();
        string = string.replaceAll("<%", "&lt%").trim();
        string = string.replaceAll("%>", "%&gt").trim();
        string = string.replaceAll("జ్ఞ\u200cా", "").trim();
        String patternStr = "(?i)onerror";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)onAbort";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)onload";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)mouseover";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)onclick";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)mouseout";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)style";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)onmouseenter";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)onmousedown";
        string = string.replaceAll(patternStr, "[replace]").trim();
        patternStr = "(?i)onmouseup";
        string = string.replaceAll(patternStr, "[replace]").trim();
        return string;
    }

    public static String formatUrl(String url){
        url = url.trim();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }
}
