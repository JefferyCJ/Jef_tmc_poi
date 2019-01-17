package com.tumi.data.poi.utils;

import com.googlecode.easyec.sika.converters.Date2StringConverter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/16 5:02 PM
 * @describe:
 */
public class DateUtils {

    private static final int SECONDS_PER_MINUTE = 60;
    private static final int MINUTES_PER_HOUR = 60;
    private static final int HOURS_PER_DAY = 24;
    private static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);
    /**
     * 一天的毫秒数
     **/
    private static final long DAY_MILLISECONDS = SECONDS_PER_DAY * 1000L;
    private static SimpleDateFormat sdFormat;

    /**
     * 转换方法
     *
     * @parma numberString 要转换的浮点数
     * @parma pattern 要获得的格式 例如"hh:mm:ss"
     **/
    public static String number2DateString(double numberString, String pattern) {
        sdFormat = new SimpleDateFormat(pattern);
        int wholeDays = (int) Math.floor(numberString);
        int millisecondsInday = (int) ((numberString - wholeDays) * DAY_MILLISECONDS + 0.5);
        Calendar calendar = new GregorianCalendar();
        setCalendar(calendar, wholeDays, millisecondsInday, false);
        return sdFormat.format(calendar.getTime());
    }

    private static void setCalendar(Calendar calendar, int wholeDays,
                                    int millisecondsInDay, boolean use1904windowing) {
        int startYear = 1900;
        int dayAdjust = -1;
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1;
        } else if (wholeDays < 61) {
            dayAdjust = 0;
        }
        calendar.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(GregorianCalendar.MILLISECOND, millisecondsInDay);
    }

    /**
     * Date 转换 String
     *
     * @param pattern
     * @param date
     * @return
     */
    public static String getDate2String(String pattern, Date date) {
        Date2StringConverter converter = new Date2StringConverter(pattern);
        return converter.adorn(date);
    }
}
