package cn.cuilan.ssmp.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Jag 2017/12/28 10:55
 */
public class TimeUtil {
    private final static String DATE_TIME_FORMAT_1 = "yyyy.MM.dd HH:mm:ss";
    private final static DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT_1);

    public static String now() {
        return DateTime.now().toString(DATE_TIME_FORMAT_1);
    }

    /**
     * 获取传入时间与当前时间的时间差
     */
    public static String differTime(String actionTime) {
        if (StringUtils.isEmpty(actionTime) || actionTime.length() < 13) {
            return "";
        }
        return getDifferTime(getCurrentTimestamp() / 1000, actionTime);
    }

    /**
     * 获取传入时间与当前时间的时间差
     */
    public static String differTime(Long actionTime) {
        return differTime(actionTime.toString());
    }

    /**
     * 时间比对的方法
     */
    private static String getDifferTime(long nowTotalTime, String actionTime) {
        return parseDifferTime(nowTotalTime, actionTime, "前");
    }

    public static Long localDate2Long(java.time.LocalDateTime localDateTime) {
        return localDateTime.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
    }

    /**
     * 获取当前时间的16位时间戳
     */
    public static Long getCurrentTimestamp() {
        return System.currentTimeMillis() * 1000;
    }

    /**
     * 计算时间差
     */
    public static String parseDifferTime(Long nowTotalTime, String actionTime, String suffix) {
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
            return ((nowTotalTime - oldTotalTime) / (60 * rate)) + "分钟"
                    + suffix;

        } else if (nowTotalTime > oldTotalTime
                && (nowTotalTime - oldTotalTime) < 60l * 60 * 24 * rate) {
            long h = (nowTotalTime - oldTotalTime) / (60 * 60 * rate);
            long min = (((nowTotalTime - oldTotalTime - h * 60 * 60 * rate) / (60 * rate)));
            if (min < 59) {
                min = min + 1;
            }
            return h + "小时" + min + "分钟" + suffix;

        } else if (nowTotalTime > oldTotalTime
                && (nowTotalTime - oldTotalTime) < 60l * 60 * 24 * 30 * rate) {
            long d = (nowTotalTime - oldTotalTime) / (60 * 60 * 24 * rate);
            long h = (nowTotalTime - oldTotalTime - d * 60l * 60 * 24 * rate)
                    / (60 * 60 * rate);
            if (h < 23) {
                h = h + 1;
            }
            return d + "天" + h + "小时" + suffix;

        } else {
            Date dt = new Date(Long.parseLong(actionTime.substring(0, 13)));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            int oldYear = Integer.parseInt(df.format(dt).substring(0,
                    df.format(dt).indexOf("-")));
            if (nowYear != oldYear && nowYear > oldYear) {
                df = new SimpleDateFormat("yyyy-MM-dd");
            }
            return df.format(dt);
        }
    }

    public static Long getBeginStamp(String dateStr, int offset) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(formatDay(dateStr));
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.DAY_OF_MONTH, offset);
        return cl.getTimeInMillis();
    }

    private static String formatDay(String date) {
        if (date.length() == 8) {
            return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);
        }
        return date;
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
        if (result <= (259200 * 1000000l)) {
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
        if (result <= (604800 * 1000000l)) {
            return true;
        }
        return false;
    }

    public static Long getTimeOfDaysAgo(int inDays){
        return (System.currentTimeMillis() * 1000 - 86400000000L * inDays);
    }

    //todo 加上天的逻辑
    public static String getDiffTimeByNow(Long beforeTime){
        if (null==beforeTime){
            return "";
        }
        beforeTime=beforeTime/1000;
        if (beforeTime>System.currentTimeMillis()){
            return "";
        }
        Calendar before = getCar(beforeTime);
        Calendar now = getCar(System.currentTimeMillis());
        int beforeYear = before.get(Calendar.YEAR);
        int beforeMonth = before.get(Calendar.MONTH);
        int beforeDate = before.get(Calendar.DATE);
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH);
        int nowDate = now.get(Calendar.DATE);
        if (nowMonth<beforeMonth){
            if (nowYear<=beforeYear){
                return "";
            }
            int diffMonth = nowMonth+12- beforeMonth;
            nowYear--;
            if (nowYear==beforeYear){
                return diffMonth+"个月";
            }
            return (nowYear-beforeYear)+"年";
        }
        if (nowYear<beforeYear){
            return "";
        }
        if (nowYear==beforeYear){
            int diffMonth = nowMonth-beforeMonth;
            if (diffMonth==0||diffMonth==1&&nowDate<beforeDate){
                //少于一个月，不满一天显示0天，否则显示x天
                long days = (System.currentTimeMillis()-beforeTime)/ 86400000L;
                return days+"天";
            }
            return diffMonth+"个月";
        }
        return (nowYear-beforeYear)+"年";
    }

    private static Calendar getCar(Long time){
        Calendar beforeCar = Calendar.getInstance();
        beforeCar.setTimeInMillis(time);
        return beforeCar;
    }

    /**
     *
     * @param count
     * @param timeUnit Calendar.YEAR/Calendar.MONTH/Calendar.DATE等
     */
    public static Long getTimeAgo(int count, int timeUnit){
        Calendar carNow = Calendar.getInstance();
        carNow.add(timeUnit,count);
        return carNow.getTimeInMillis()*1000;
    }
}
