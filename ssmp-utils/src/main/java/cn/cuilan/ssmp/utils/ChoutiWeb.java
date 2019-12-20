package cn.cuilan.ssmp.utils;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.util.CollectionUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ChoutiWeb {
    private static final String CHOUTI_PHONE_REGISTER_PRE = "ctu_";

    //信息体的前缀
    private final static String URL_MSG = "chouti://msg?";
    //data信息类型
    private final static String TYPE_MSG = "type=3&";
    //date解析方式
    private final static String PARSE_JSON = "parse=json&";
    private final static String DATA = "data=";
    //入热榜用户积分阀值
    public static Integer INTEGRATION = 100;

    public final static String DATE_FORMAT = "yyyy-MM-dd";
    public final static String DATE_MIN_FORMAT = "";
    public final static String DATE_SEC_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 定义抽屉新热榜的手机验证码的长度为四位
     */
    private static final Integer CHOUTI_PHONE_AUTH_CODE_LENGTH = 4;

    private static final Pattern URL_PATTERN = Pattern.compile(
            "((?:https|http|ftp)://)" + "(?:[0-9a-z_!~*'()-]+\\.)*" // 域名-
                    // www.
                    + "(?:[0-9a-z][0-9a-z!~*#&'.^:@+$%-]{0,61})?[0-9a-z]\\." // 二级域名
                    // ,可以含有符号
                    + "[a-z]{0,6}" // .com
                    + "(?::[0-9]{1,4})?" // 端口
                    + "(?:/[0-9A-Za-z_!~*'().?:@&=+,$%#-]*)*" // 除了域名外中间参数允许的字符
                    + "[0-9A-Za-z]{1,4}"
                    + "(?::[0-9]{1,4})?" // 端口
                    + "(?:/[0-9A-Za-z_!~*'().?:@&=+,$%#-]*)*" // 除了域名外中间参数允许的字符
                    + "[0-9A-Za-z-/?~#%*&()$+=^]", Pattern.CASE_INSENSITIVE); // 指定可以作为结尾的字符

    public static String addShortUrlForText(String text) {

        Set<String> shortUrls = getAllShortUrl(text);
        if (CollectionUtils.isEmpty(shortUrls)) {
            return text;
        }

        for (String shortUrl : shortUrls) {
            String hrefStr = "<a target='_blank' href='" + shortUrl + "'>" + shortUrl + "</a>";
            text = StringUtils.replace(text, shortUrl, hrefStr);
        }

        return text;
    }

    /**
     * 获取内容中的所有短链
     */
    private static Set<String> getAllShortUrl(String text) {
        Set<String> shortUrls = new HashSet<>();
        if (StringUtils.isEmpty(text)) {
            return shortUrls;
        }
        Set<String> tempList = ChoutiWeb.parseUrl(text);
        if (CollectionUtils.isEmpty(tempList)) {
            return shortUrls;
        }
        shortUrls.addAll(tempList);
        return shortUrls;
    }

    public static Set<String> parseUrl(String s) {
        Set<String> result = new HashSet<>();
        if (StringUtils.isEmpty(s)) {
            return result;
        }

        Matcher matcher = URL_PATTERN.matcher(s);
        boolean find = matcher.find();
        StringBuffer sb = new StringBuffer();
        while (find) {
            result.add(matcher.group());
            find = matcher.find();
        }
        matcher.appendTail(sb);

        return result;
    }

    /**
     * 比较2个版本号的大小{v1-v2}
     */
    public static int compareVersion(String v1, String v2) {
        if (StringUtils.isEmpty(v1) || StringUtils.isEmpty(v2)) {
            return -1;
        }

        if (v1.startsWith("v")) {
            v1 = v1.substring(1, v1.length());
        }

        if (v2.startsWith("v")) {
            v2 = v2.substring(1, v2.length());
        }
        String[] version1 = v1.split("\\.");
        String[] version2 = v2.split("\\.");
        int len1 = version1.length;
        int len2 = version2.length;
        int len = Math.min(len1, len2);
        int cv1, cv2;

        for (int i = 0; i < len; i++) {
            cv1 = Integer.parseInt(version1[i]);
            cv2 = Integer.parseInt(version2[i]);
            if (cv1 != cv2) {
                return cv1 - cv2;
            } else {
                continue;
            }
        }
        return len1 - len2;
    }

    /**
     * 获取当前时间的16位时间戳
     */
    public static Long getCurrentTimestamp() {
        return System.currentTimeMillis() * 1000;
    }


    /**
     * 根据手机随机生成4位验证码的方法
     */
    public static String createAuthCodeForMobile(String phone) {
        //参数非空判断
        if (StringUtils.isEmpty(phone)) {
            return null;
        }
        //随机获取一个六位的验证码
        StringBuilder authCode = new StringBuilder();
        for (int i = 0; i < CHOUTI_PHONE_AUTH_CODE_LENGTH; i++) {
            authCode.append((int) (Math.random() * 10));
        }
        return authCode.toString();
    }

    /**
     * 根据传入的时间戳获取当前的日期字符串(如：2013-11－21)，如果传入时间为空，默认返回日期为当天
     */
    public static String getDateByTime(String time) {
        Date dt = new Date();
        if (!StringUtils.isEmpty(time) && time.length() == 13) {
            dt = new Date(Long.parseLong(time));
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(dt);
    }

    /**
     * 获取当前日期的十位时间戳 (解析时间戳得到时间只精确到日期)
     */
    public static Long getCurrentDate(Date dt) {
        if (null == dt) {
            dt = new Date();
        }
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return getTimestamp(df.format(dt)) / 1000;
    }

    /**
     * 获取传递时间对应的时间戳,时间连接符必须是-(例如2011-12-27 ，返回时间戳)
     */
    public static Long getTimestamp(String time) {

        // 构建calendar对象
        Calendar cal = Calendar.getInstance();

        // 参数非空判断
        if (!StringUtils.isEmpty(time) && time.length() >= 10) {
            // 去除前后空格并获取年月日的值
            time = time.trim();
            int year = Integer.parseInt(time.substring(0, 4));
            int month = Integer.parseInt(time.substring(5, 7));
            int date = Integer.parseInt(time.substring(8, 10));
            cal.set(year, month - 1, date);
            // 判断是否有时分秒的值
            if (time.length() >= 13) {
                int hour = Integer.parseInt(time.substring(11, 13));
                cal.set(Calendar.HOUR_OF_DAY, hour);
            } else {
                cal.set(Calendar.HOUR_OF_DAY, 0);
            }
            if (time.length() >= 16) {
                int minite = Integer.parseInt(time.substring(14, 16));
                cal.set(Calendar.MINUTE, minite);
            } else {
                cal.set(Calendar.MINUTE, 0);
            }
            if (time.length() >= 19) {
                int seconds = Integer.parseInt(time.substring(17, 19));
                cal.set(Calendar.SECOND, seconds);
            } else {
                cal.set(Calendar.SECOND, 0);
            }
            if (time.length() >= 23) {
                int millisecond = Integer.parseInt(time.substring(20));
                cal.set(Calendar.MILLISECOND, millisecond);
            } else {
                cal.set(Calendar.MILLISECOND, 0);
            }

            return cal.getTimeInMillis();
        }
        return 0L;
    }

    /**
     * 获取传递时间对应的时间戳,时间连接符必须是-(例如2011-12-27 ，返回时间戳),数据 必须规范，否则抛异常
     */
    public static Long getTimestampToBind(String time) {

        try {
            // 构建calendar对象
            Calendar cal = Calendar.getInstance();
            // 参数非空判断
            if (!StringUtils.isEmpty(time) && time.contains("-")) {
                // 去除前后空格并获取年月日的值
                String timeStr = time.trim();
                int year = Integer.parseInt(timeStr.substring(0, timeStr.indexOf("-")));
                timeStr = timeStr.substring(timeStr.indexOf("-") + 1);
                int month = Integer.parseInt(timeStr.substring(0, timeStr.indexOf("-")));
                timeStr = timeStr.substring(timeStr.indexOf("-") + 1);
                int date;
                if (timeStr.length() > 2) {
                    date = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(" ")));
                    timeStr = timeStr.substring(timeStr.indexOf(" ") + 1);
                } else {
                    date = Integer.parseInt(timeStr);
                    timeStr = "";
                }
                cal.set(year, month - 1, date);
                // 判断是否有时分秒的值
                if (timeStr.length() >= 2) {
                    int hour = Integer.parseInt(timeStr.substring(0, 2));
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                } else {
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                }
                if (timeStr.length() >= 5) {

                    int minite = Integer.parseInt(timeStr.substring(3, 5));
                    cal.set(Calendar.MINUTE, minite);
                } else {
                    cal.set(Calendar.MINUTE, 0);
                }
                if (timeStr.length() >= 7) {
                    int seconds = Integer.parseInt(timeStr.substring(6));
                    cal.set(Calendar.SECOND, seconds);
                } else {
                    cal.set(Calendar.SECOND, 0);
                }
                return cal.getTimeInMillis();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }


    /**
     * 处理链接里面的图片 imgUrl
     */
    public static String dealWithLinksPicture(String imgUrl) {
        if (StringUtils.isEmpty(imgUrl) || ChoutiWeb.getUrlIndex(imgUrl) == -1) {
            return imgUrl;
        }
        String domain = ChoutiWeb.getDomain(imgUrl);
        imgUrl = imgUrl.substring(imgUrl.indexOf(domain) + domain.length());
        return imgUrl;
    }

    public static String getNextDay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        return sdf.format(date);
    }

    /**
     * 获取
     */
    public static Integer getUrlIndex(String text) {

        if (StringUtils.isEmpty(text)) {
            return -1;
        }
        text = text.toLowerCase();
        if (StringUtils.isEmpty(text) || !isExistUrl(text)) {
            return -1;
        }

        int index = -1;
        if (text.indexOf("https://") != -1) {
            index = text.indexOf("https://");
        } else if (text.indexOf("http://") != -1) {
            index = text.indexOf("http://");
        } else if (text.indexOf("ftp://") != -1) {
            index = text.indexOf("ftp://");
        } else if (text.indexOf("file://") != -1) {
            index = text.indexOf("file://");
        }
        return index;
    }

    /**
     * 获取域名
     */
    public static String getDomain(String url) {

        if (StringUtils.isEmpty(url)) {
            return "";
        }

        String domain = "";
        try {
            domain = new URL(url).getHost();
        } catch (MalformedURLException e) {
            try {
                domain = new URL("http://" + url).getHost();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        }
        return domain.toLowerCase();
    }

    /**
     * 判断内容中是否存在url
     */
    public static Boolean isExistUrl(String text) {

        if (StringUtils.isEmpty(text)) {
            return false;
        }
        String match = "^.*(https|http|ftp|file|HTTPS|HTTP|FTP|FILE)://.*";
        boolean result = Pattern.matches(match, text);
        return result;
    }

    public static String genFileName(byte[] bs, String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return "";
        }
        String uid = UUID.randomUUID().toString().replace("-", "");
        String fileType = "";
        if (fileName.contains(".")) {
            fileType = fileName.substring(fileName.indexOf("."));
        } else {//如果没后缀就从图片中读取
            fileType = "." + ImageUtils.getImageType(bs);
        }

        try {
            InputStream sbs = new ByteArrayInputStream(bs);
            String imgSize = ImageUtils.getImgSize(sbs);
            if (!StringUtils.isEmpty(imgSize)) {
                return getPrefix() + (uid + "" + imgSize).toUpperCase() + "" + fileType;
            }
        } catch (Exception e) {
        }
        String retFileName = getPrefix() + uid.toUpperCase() + fileType;
        return retFileName;
    }

    public static String getPrefix() {
        return "CHOUTI_" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/";
    }

    public static String genFileName(File file) {
        if (file == null) {
            return "";
        }
        String fileName = file.getName();
        String fileSuffix = "";
        if (fileName.contains(".")) {
            fileSuffix = fileName.substring(fileName.indexOf("."));
        } else {//如果没后缀就从图片中读取
            fileSuffix = "." + ImageUtils.getImageType(file);
        }

        String uid = UUID.randomUUID().toString().replace("-", "");
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();
            String filename = getPrefix() + uid + "_w" + width + "h" + height;
            filename = filename.toUpperCase();
            filename = filename.concat(fileSuffix);
            return filename;
        } catch (Exception e) {
            log.info("chouti get image WH fail...");
            return uid + fileSuffix;
        }
    }

    /**
     * 把字符串转换成int
     */
    public static Integer toInteger(String value) {
        int result = -1;
        try {
            result = Integer.parseInt(value);
        } catch (Exception e) {
            result = -1;
        }
        return result;
    }

    /**
     * 计算科技分类新闻热度分值
     */
    public static Double calculateScoreForTechLinks(Long createTime, Long votes) {
        /** 获取basetime基准时间 */
        long basetime = 1133999203000000l;// 2005-12-08 07:46:43
        Double v = null;
        if (null == votes || votes < 0) {
            v = 0d;
        } else {
            v = votes / 1d;
        }
        if (v.equals(0d)) {
            // 清零时以当前时间为基准时间
            basetime = getCurrentTimestamp();
            double t = (createTime - basetime) / (45000d * 1000000);
            return t;
        }

        double t = (createTime - basetime) / (45000d * 1000000);
        // t = t + Math.log10(upsWithWeight);
        t = t + calculateMathLog(v, 4);
        log.debug("该新闻推荐数:" + votes + "---该新闻科技分类分值:" + t);
        return t;
    }

    /**
     * 计算log的算法
     *
     * @param value
     * @param base
     * @return
     */
    private static Double calculateMathLog(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    /**
     * 计算链接的热度分值
     */
    public static Double calculateScoreForLinks(Double upsWithWeight,
                                                Long createTime) {
        /** 获取basetime基准时间 */
        long basetime = 1133999203000000L;// 2005-12-08 07:46:43
        if (upsWithWeight.equals(0d)) {
            // 清零时以当前时间为基准时间
            basetime = getCurrentTimestamp();
            double t = (createTime - basetime) / (45000d * 1000000);
            return t;
        }

        double t = (createTime - basetime) / (45000d * 1000000);
        // t = t + Math.log10(upsWithWeight);
        t = t + calculateMathLog(upsWithWeight, 4);
        return t;
    }

    public static String createMsgRule(String data) {
        Joiner joiner = Joiner.on("").skipNulls();
        return joiner.join(URL_MSG, TYPE_MSG, PARSE_JSON, DATA, data);
    }

    /**
     * 过滤掉所有的html标签
     */
    private final static Pattern[] DANGEROUS_TOKENS = new Pattern[]{Pattern
            .compile("^j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:",
            Pattern.CASE_INSENSITIVE)};

    public static String removeHtmlTag(String source) {

        if (StringUtils.isEmpty(source)) {
            return "";
        }
        source = source.replaceAll(" \u200B", "").trim();
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
        string = string.replaceAll("జ్ఞ‌ా", "").trim();


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

        patternStr = "(?i)onmouseenter";
        string = string.replaceAll(patternStr, "[replace]").trim();

        patternStr = "(?i)onmousedown";
        string = string.replaceAll(patternStr, "[replace]").trim();

        patternStr = "(?i)onmouseup";
        string = string.replaceAll(patternStr, "[replace]").trim();
        //string = string.replaceAll("onerror", "").trim();
        return string;
    }

    /**
     * 计算字符长度，汉字长度是2，字母，数字或者符号为1 isTextUrl 表示是否计算文本中存在url的长度
     */
    public static Integer getLength(String source, Boolean isTextUrlLength) {
        if (StringUtils.isEmpty(source)) {
            return 0;
        }

        String temp = source;
        if (!isTextUrlLength) {
            // 为false时不计算长度，文本中url长度都限制为1
            Set<String> urls = ChoutiWeb.parseUrl(temp);
            if (!CollectionUtils.isEmpty(urls)) {
                for (String url : urls) {
                    temp = StringUtils.replace(temp, url, "a");
                }
            }
        }
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
     * 计算字符长度，汉字长度是2，字母，数字或者符号为1
     */
    public static String getSplitLength(String source, Integer maxLength,
                                        Boolean isTextUrlLength) {
        if (StringUtils.isEmpty(source)) {
            return "";
        }
        if (maxLength == null || maxLength <= 0) {
            return source;
        }

        String prefix = "#*";
        Map<String, String> urlMap = new HashMap<String, String>();
        String temp = source;
        if (!isTextUrlLength) {
            // 为false时不计算长度，文本中url长度都限制为1
            Set<String> urls = ChoutiWeb.parseUrl(temp);
            if (!CollectionUtils.isEmpty(urls)) {
                int i = 0;
                for (String url : urls) {
                    urlMap.put(prefix + i, url);
                    temp = StringUtils.replace(temp, url, prefix + i);
                    i++;
                }
            }
        }

        int splitLength = maxLength;
        int length = 0;
        for (int i = 0; i < temp.length(); i++) {
            char c = temp.charAt(i);
            // 单字节加1
            if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
                length++;
            } else {
                length += 2;
            }
            // logger.info(c + "--" + length + "--" + i);
            if (length >= maxLength) {
                splitLength = i;
                break;
            }
        }
        String result = StringUtils.substring(temp, 0,
                splitLength + urlMap.size());
        String remainStr = StringUtils.substring(temp,
                splitLength + urlMap.size());
        if (StringUtils.startsWith(remainStr, "*")
                && StringUtils.endsWith(result, "#")) {
            // 以#结尾情况
            String num = remainStr.substring(1, 2);
            if (toInteger(num) >= 0) {
                result += remainStr.substring(0, 2);
            }
        }
        if (StringUtils.endsWith(result, prefix)) {
            // 以#*为结尾情况
            String num = remainStr.substring(0, 1);
            if (toInteger(num) >= 0) {
                result += remainStr.substring(0, 1);
            }
        }
        if (result.contains(prefix)) {
            Set<String> keySet = urlMap.keySet();
            for (String key : keySet) {
                result = StringUtils.replace(result, key, urlMap.get(key));
            }
        }
        return result;
    }

    /**
     * 把字符串转成long
     */
    public static Long toLong(String value) {
        Long result = -1l;
        try {
            result = Long.parseLong(value);
        } catch (Exception e) {
            result = -1l;
        }
        return result;
    }

    /**
     * 计算评论的热度分值
     */
    public static Float calculateScore(Long ups, Long downs) {
        if (ups == null || ups <= 0) {
            ups = 1l;
        }
        if (downs == null) {
            downs = 0l;
        }
        float count = ups + downs;
        float p = ups / count;
        float normal = 1.28f;
        float twoN = 2 * count;
        float normaPow = (float) Math.pow(normal, 2);

        float data = (float) (p * (1 - p) / count + normaPow
                / (4 * Math.pow(count, 2)));
        float denominator = 1 + (1 / count) * normaPow;
        float result = (float) ((p + (1 / twoN) * normaPow - normal
                * Math.sqrt(data)) / denominator);

        return result;
    }


    public static boolean inNightMode() {
        boolean result = false;
        long currentTime = ChoutiWeb.getCurrentTimestamp() / 1000;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(currentTime));
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        //0:00-6:00为夜间模式
        if (currentHour < 6) {
            result = true;
        }
        return result;
    }

    /**
     * 判断时间是否在24小时之内
     *
     * @param createTime
     * @return
     */
    public static Boolean isTwentyFour(Long createTime) {
        if (createTime == null || createTime < 0) {
            return false;
        }
        long result = getCurrentTimestamp() - createTime;
        if (result <= (86400 * 1000000l)) {
            return true;
        }
        return false;
    }

    /**
     * 判断时间是否在3天之内
     *
     * @param createTime
     * @return
     */
    public static Boolean isThreeDay(Long createTime) {
        if (createTime == null || createTime < 0) {
            return false;
        }
        long result = getCurrentTimestamp() - createTime;
        if (result <= (259200 * 1000000L)) {
            return true;
        }
        return false;
    }

    /**
     * 判断时间是否在2天之内
     *
     * @param createTime
     * @return
     */
    public static Boolean isTwoDay(Long createTime) {
        if (createTime == null || createTime < 0) {
            return false;
        }
        long result = getCurrentTimestamp() - createTime;
        if (result <= (172800 * 1000000L)) {
            return true;
        }
        return false;
    }

    /**
     * 判断时间是否在7天之内
     *
     * @param createTime
     * @return
     */
    public static Boolean isSevenDay(Long createTime) {
        if (createTime == null || createTime < 0) {
            return false;
        }
        long result = getCurrentTimestamp() - createTime;
        if (result <= (604800 * 1000000L)) {
            return true;
        }
        return false;
    }

    /**
     * 判断时间是否在3个月之内
     *
     * @param createTime
     * @return
     */
    public static Boolean isThreeMonth(Long createTime) {
        if (createTime == null || createTime < 0) {
            return false;
        }
        long result = getCurrentTimestamp() - createTime;
        if (result <= (7776000 * 1000000L)) {
            return true;
        }
        return false;
    }

    public static String arrayToStr(String[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(array).forEach(s -> {
                    stringBuilder.append(",");
                    stringBuilder.append(s);
                }
        );
        return stringBuilder.deleteCharAt(0).toString();
    }

    /**
     * 获取随机数
     */
    public static Integer getRandom(int total) {
        Random random = new Random();
        return Math.abs(random.nextInt()) % total;
    }

    /**
     * 生成用户jid
     */
    public static String createUserJid() {
        String millis = System.currentTimeMillis() + "";
        millis = millis.substring(1, millis.length() - 1);
        return CHOUTI_PHONE_REGISTER_PRE.concat(millis);
    }

    /**
     * 获取传入时间与当前时间的时间差
     */
    public static String differTime(String actionTime) {

        if (StringUtils.isEmpty(actionTime) || actionTime.length() < 13) {
            return "";
        }
        /*
         * int YEAR = Calendar.getInstance().get(Calendar.YEAR); int MONTH =
         * Calendar.getInstance().get(Calendar.MONTH) + 1; int DAY =
         * Calendar.getInstance().get(Calendar.DAY_OF_MONTH); int HOUR =
         * Calendar.getInstance().get(Calendar.HOUR_OF_DAY); int MIN =
         * Calendar.getInstance().get(Calendar.MINUTE);
         *
         * int oldTotalTime = getTotalTime(actionTime); int nowTotalTime = MIN +
         * HOUR * 60 + DAY * 60 * 24 + MONTH * 60 * 24 31 + YEAR * 60 * 24 * 31
         * * 12;
         */

        return getDifferTime(getCurrentTimestamp() / 1000, actionTime);
    }

    /**
     * 时间比对的方法
     */
    public static String getDifferTime(long nowTotalTime, String actionTime) {
        return parseDifferTime(nowTotalTime, actionTime, "前");
    }

    /**
     * 计算时间差
     */
    private static String parseDifferTime(Long nowTotalTime, String actionTime, String suffix) {
        if (StringUtils.isEmpty(actionTime)) {
            return "";
        }
        long oldTotalTime = Long.parseLong(actionTime);
        oldTotalTime = oldTotalTime / 1000;
        int rate = 1000;

        if (nowTotalTime - oldTotalTime <= 60 * rate) {
            return "小于1分钟" + suffix;

        } else if (nowTotalTime > oldTotalTime
                && nowTotalTime - oldTotalTime < 60L * 60 * rate) {
            return ((nowTotalTime - oldTotalTime) / (60L * rate)) + "分钟"
                    + suffix;

        } else if (nowTotalTime > oldTotalTime
                && (nowTotalTime - oldTotalTime) < 60L * 60 * 24 * rate) {
            long h = (nowTotalTime - oldTotalTime) / (60L * 60 * rate);
            long min = (((nowTotalTime - oldTotalTime - h * 60 * 60 * rate) / (60 * rate)));
            if (min < 59) {
                min = min + 1;
            }
            return h + "小时" + min + "分钟" + suffix;

        } else if (nowTotalTime > oldTotalTime
                && (nowTotalTime - oldTotalTime) < 60L * 60 * 24 * 30 * rate) {
            long d = (nowTotalTime - oldTotalTime) / (60L * 60 * 24 * rate);
            long h = (nowTotalTime - oldTotalTime - d * 60L * 60 * 24 * rate)
                    / (60 * 60 * rate);
            if (h < 23) {
                h = h + 1;
            }
            return d + "天" + h + "小时" + suffix;
        } else if (nowTotalTime > oldTotalTime
                && (nowTotalTime - oldTotalTime) < 60L * 60 * 24 * 365 * rate) {
            long m = (nowTotalTime - oldTotalTime) / (60L * 60 * 24 * 30 * rate);
            long d = (nowTotalTime - oldTotalTime - m * 60L * 60 * 24 * 30 * rate)
                    / (60 * 60 * 24 * rate);
            if (d < 29) {
                d = d + 1;
            }
            return m + "个月" + d + "天" + suffix;
        } else {
            Date dt = new Date(Long.parseLong(actionTime.substring(0, 13)));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            int oldYear = Integer.parseInt(df.format(dt).substring(0,
                    df.format(dt).indexOf("-")));
            if (nowYear > oldYear) {
                df = new SimpleDateFormat("yyyy-MM-dd");
            }
            return df.format(dt);
        }
    }

    /**
     * 给图片url加放缩比例参数
     */
    public static String zoomPicture(String url, String ratio) {
        if (StringUtils.isEmpty(url) || !url.contains(".")
                || StringUtils.isEmpty(ratio)) {
            return url;
        }

        // 如果存在ratio 直接返回
        if (url.indexOf(ratio) != -1) {
            return url;
        }
        return url.substring(0, url.lastIndexOf(".")) + ratio
                + url.substring(url.lastIndexOf("."));
    }

    /**
     * 给图片url去掉压缩比例参数
     */
    public static String removeZoomPicture(String url) {
        if (StringUtils.isEmpty(url) || url.indexOf(".") == -1) {
            return url;
        }
        String temp = url.substring(0, url.lastIndexOf("."));
        String end = url.substring(url.lastIndexOf("."));
        if (-1 != temp.indexOf("=")) {
            String s1 = temp.substring(temp.lastIndexOf("=") + 1);
            if (-1 != s1.indexOf("x")) {
                String s2 = s1.substring(0, s1.lastIndexOf("x"));
                String s3 = s1.substring(s1.lastIndexOf("x") + 1);
                if (StringUtils.equals(s2, s3)) {
                    url = temp.substring(0, temp.length() - s1.length() - 1)
                            + end;
                }
            }
        }
        return url;
    }

    /**
     * 处理用户图片
     */
    public static String dealWithUserPicture(String sourceImgUrl,
                                             String userOldPic, String userWantPic, String userWantPicRatio,
                                             String uploadPreFix) {
        String imgUrl = sourceImgUrl;
        if (StringUtils.isEmpty(imgUrl)
                || StringUtils.equals(imgUrl, userOldPic)) {// 配图不需要比例放缩
            imgUrl = userWantPic;
        } else if (ChoutiWeb.getUrlIndex(imgUrl) == -1) {// 如果不是以http开头的，则加前缀
            imgUrl = uploadPreFix + imgUrl;
        }
        if (ChoutiWeb.getUrlIndex(imgUrl) != -1 && !imgUrl.contains("=")) {// 如果没有增加比例，则加显示比例
            imgUrl = ChoutiWeb.zoomPicture(imgUrl, userWantPicRatio);
        }

        return imgUrl;
    }

    public static boolean JudgeIsMobile(HttpServletRequest request) {
        boolean isMoblie = false;
        String[] mobileAgents = {"iphone", "android", "phone", "mobile",
                "wap", "netfront", "java", "opera mobi", "opera mini", "ucweb",
                "windows ce", "symbian", "series", "webos", "sony",
                "blackberry", "dopod", "nokia", "samsung", "palmsource", "xda",
                "pieplus", "meizu", "midp", "cldc", "motorola", "foma",
                "docomo", "up.browser", "up.link", "blazer", "helio", "hosin",
                "huawei", "novarra", "coolpad", "webos", "techfaith",
                "palmsource", "alcatel", "amoi", "ktouch", "nexian",
                "ericsson", "philips", "sagem", "wellcom", "bunjalloo", "maui",
                "smartphone", "iemobile", "spice", "bird", "zte-", "longcos",
                "pantech", "gionee", "portalmmm", "jig browser", "hiptop",
                "benq", "haier", "^lct", "320x320", "240x320", "176x220",
                "w3c ", "acs-", "alav", "alca", "amoi", "audi", "avan", "benq",
                "bird", "blac", "blaz", "brew", "cell", "cldc", "cmd-", "dang",
                "doco", "eric", "hipt", "inno", "ipaq", "java", "jigs", "kddi",
                "keji", "leno", "lg-c", "lg-d", "lg-g", "lge-", "maui", "maxo",
                "midp", "mits", "mmef", "mobi", "mot-", "moto", "mwbp", "nec-",
                "newt", "noki", "oper", "palm", "pana", "pant", "phil", "play",
                "port", "prox", "qwap", "sage", "sams", "sany", "sch-", "sec-",
                "send", "seri", "sgh-", "shar", "sie-", "siem", "smal", "smar",
                "sony", "sph-", "symb", "t-mo", "teli", "tim-", /*"tosh",*/ "tsm-",
                "upg1", "upsi", "vk-v", "voda", "wap-", "wapa", "wapi", "wapp",
                "wapr", "webc", "winw", "winw", "xda", "xda-",
                "Googlebot-Mobile"};
        if (request.getHeader("User-Agent") != null) {
            for (String mobileAgent : mobileAgents) {
                if (request.getHeader("User-Agent").toLowerCase().contains(mobileAgent)) {
                    isMoblie = true;
                    break;
                }
            }
        }
        return isMoblie;
    }


    public static boolean isWeChatClient(HttpServletRequest request) {
        if (null == request) {
            return false;
        }
        String userAgent = request.getHeader("user-agent");
        if (StringUtils.isEmpty(userAgent)) {
            return false;
        }
        return userAgent.toLowerCase().contains("micromessenger");
    }

    /**
     * 给url地址加前缀
     *
     * @param url
     * @return
     */
    public static String addPrefixForLinks(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String prefix = "http://";
        String prefixs = "https://";
        if (!StringUtils.startsWith(url, prefix) && !StringUtils.startsWith(url, prefixs)) {
            return prefix + url;
        }
        return url;
    }


    /**
     * 格式化日期格式
     *
     * @param date   要格式化的参数
     * @param format 格式化标准
     * @return
     */
    public static Long parseDate(String date, String format) {
        // 24小时制
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            Date strtodate = formatter.parse(date);
            return strtodate.getTime() * 1000l;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean serverRunning(String url) {
        if (StringUtils.isEmpty(url)) {
            return false;
        }
        url = StringUtils.replace(url, " ", "%20");
        try {
            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("HEAD"); // 只需要取头，减少没必要的传输
            if (conn.getResponseCode() == 200) // 正常
                return true;
            else
                log.info("http请求第三方服务url=" + url + ",http结果=" + conn.getResponseCode());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将时间戳转换成指定的日期格式
     */
    public static String dateFormat(long date, String format) {
        Date dt = new Date(date / 1000);
        DateFormat df = new SimpleDateFormat(format);
        return df.format(dt);
    }
}
