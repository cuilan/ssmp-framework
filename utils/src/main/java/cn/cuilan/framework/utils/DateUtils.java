package cn.cuilan.framework.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Optional;

/**
 * 功能：
 * 作者：刘柏勋
 * 联系：wmsjhappy@gmail.com
 * 时间：17-3-29 下午4:02
 * 更新：
 * 备注：
 */
public final class DateUtils {

    public final static String DATE_TIME_FORMAT_1 = "yyyy.MM.dd HH:mm:ss";
    public final static String DATE_TIME_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_TIME_FORMAT_3 = "yyyyMMddHHmmss";
    public final static String DATE_TIME_FORMAT_4 = "yyyy-MM-dd";
    public final static String DATE_TIME_FORMAT_5 = "yyyy.MM.dd";

    private final static DateTimeFormatter FORMATTER = DateTimeFormat.forPattern(DATE_TIME_FORMAT_1);

    public static String now() {
        return DateTime.now().toString(DATE_TIME_FORMAT_1);
    }

    public static String nowWithFormat(String format) {
        return DateTime.now().toString(format);
    }

    public static Long plusDayMaxValueNanoByNow(int day) {
        // fixme joda-time 支持毫秒
        return DateTime.now().plusDays(day).millisOfDay().withMaximumValue().getMillis() * 1000;
    }

    public static String nanoTime(Long nanotime) {
        return new DateTime(nanotime / 1000L).toString(DATE_TIME_FORMAT_1);
    }

    public static String nanoTime(Long nanotime, String format) {
        return new DateTime(nanotime / 1000L).toString(StringUtils.isBlank(format) ? DATE_TIME_FORMAT_1 : format);
    }

    public static String millsTime(Long millstime) {
        return new DateTime(millstime).toString(DATE_TIME_FORMAT_1);
    }

    public static boolean checkDateTimeIsExpire(String strDateTime, Integer expireSeconds) {
        return DateTime.parse(strDateTime, FORMATTER).plusSeconds(expireSeconds).isBeforeNow();
    }

    public static Long formatStrToNanoTime(String datetime) {
        return DateTime.parse(datetime, FORMATTER).getMillis() * 1000;
    }

    public static Long format(String datetime, String pattern) {
        return DateTime.parse(datetime, DateTimeFormat.forPattern(pattern)).getMillis();
    }

    public static int toSencond(int day, int hour) {
        DateTime start = DateTime.now();
        DateTime end = start.plusHours(hour).plusDays(day);
        int value = Seconds.secondsBetween(start, end).getSeconds();
        return value;
    }

    /**
     * 根据当前时间计算偏移量，主要根据当前时间生成过期时间
     */
    public static Date plusYearMonthDay(int year, int month, int day) {
        return plusYearMonthDayHour(year, month, day, 0);
    }

    public static Date plusYearMonthDayHour(int year, int month, int day, int hour) {
        return DateTime.now().plusYears(year).plusMonths(month).plusDays(day).plusHours(hour).toDate();
    }

    public static String todayDateTime() {
        return DateTime.now().toString(DATE_TIME_FORMAT_3);
    }

    public static String todayDateTime(String format) {
        return DateTime.now().toString(Optional.ofNullable(format).orElse(DATE_TIME_FORMAT_3));
    }
}