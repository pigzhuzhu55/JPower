package com.wlcb.jpower.module.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName DateUtils
 * @Description TODO 时间工具类
 * @Author 郭丁志
 * @Date 2020-02-03 18:40
 * @Version 1.0
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm:ss";

    public static final String DATE_FORMAT = "yyyyMMdd";
    public static final String TIME_FORMAT = "HHmmss";
    public static final String DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    public static final String DATE_TIME_MS_FORMAT = "yyyyMMddHHmmssSSS";

    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    public DateUtil() {
    }

    public static String getDateTime() {
        return (new SimpleDateFormat(DATE_TIME_FORMAT)).format(Calendar.getInstance().getTime());
    }

    public static String getDate() {
        return (new SimpleDateFormat(DATE_FORMAT)).format(Calendar.getInstance().getTime());
    }

    public static String getTime() {
        return (new SimpleDateFormat(TIME_FORMAT)).format(Calendar.getInstance().getTime());
    }

    /**
     * 改变当前时间天
     * @author mr.g
     * @param nday
     * @return java.util.Date
     */
    public static String getDateFormat(int nday) {
        Date d = Calendar.getInstance().getTime();
        return (new SimpleDateFormat(DATE_FORMAT)).format(getDate(d, nday));
    }

    /**
     * 改变当前时间天
     * @author mr.g
     * @param nday
     * @return java.util.Date
     */
    public static Date getDate(int nday) {
        return getDate(Calendar.getInstance().getTime(), nday);
    }

    public static String getDate(String date, int nday) {
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
            Date d = f.parse(date);
            return f.format(getDate(d, nday));
        } catch (ParseException var4) {
            return null;
        }
    }

    public static Date getDate(Date d, int nday) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_YEAR, nday);
        return c.getTime();
    }

    public static String getDate(String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);
        return sf.format(Calendar.getInstance().getTime());
    }

    public static String getDate(Date date, String format) {
        return (new SimpleDateFormat(format)).format(date);
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }


    /**
     * 获取周
     * 启始周是周一
     */
    public static Integer getWeek(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        weekday--;
        if (weekday <= 0){
            weekday = 7;
        }
        return weekday;
    }

    public static Date parseDateFormat(String date, String format) {
        SimpleDateFormat sf = new SimpleDateFormat(format);

        try {
            return sf.parse(date);
        } catch (ParseException var4) {
            return null;
        }
    }

    public static DateAndTime getDateAndTime() {
        return new DateAndTime();
    }

    public static DateAndTime getDateAndTime(Date date) {
        return new DateAndTime(date);
    }

    public static class DateAndTime {
        private String date;
        private String time;

        public DateAndTime() {
            this(Calendar.getInstance().getTime());
        }

        public DateAndTime(Date d) {
            String s = (new SimpleDateFormat("yyyyMMddHHmmss")).format(d);
            this.date = s.substring(0, 8);
            this.time = s.substring(8, 14);
        }

        public String getDate() {
            return this.date;
        }

        public String getTime() {
            return this.time;
        }
    }

}
