package org.tony.console.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author peng.hu1
 * @Date 2023/1/3 13:53
 */
public class DateUtil {

    private static SimpleDateFormat format1 =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static SimpleDateFormat format2 =  new SimpleDateFormat("yyyy-MM-dd");

    public static long getDateGapHour(Date endDate, Date nowDate) {
        long diff = nowDate.getTime() - endDate.getTime();

        return diff / (1000 * 60 * 60);
    }

    /**
     * 分钟差距
     * @param endDate
     * @return
     */
    public static long getDateGapMinNow(Date endDate) {
        return getDateGapM(endDate, new Date());
    }

    /**
     * 分钟差距
     * @param endDate
     * @param nowDate
     * @return
     */
    public static long getDateGapM(Date endDate, Date nowDate) {
        long diff = nowDate.getTime() - endDate.getTime();

        return diff / (1000 * 60);
    }

    /**
     * 天差距
     * @param endDate
     * @param nowDate
     * @return
     */
    public static double getDateGapDay(Date endDate, Date nowDate) {
        long diff = nowDate.getTime() - endDate.getTime();

        return diff*1.0 / (1000 * 60 * 60 * 24);
    }

    public static long getDateGapS(Date endDate, Date nowDate) {
        long diff = nowDate.getTime() - endDate.getTime();

        return diff / (1000);
    }

    public static Date getDayWithMin(Date now, int deta) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, deta);

        return cal.getTime();
    }

    public static Date getDay(Date now, int deta) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, deta);

        return cal.getTime();
    }

    public static String getToday() {
        Date date = new Date();
        return format2.format(date);
    }

    public static String getDateTime(Date date) {
        return format2.format(date);
    }

    public static Date getDate(String time) throws ParseException {
        Date now = new Date();
        String d = format2.format(now);
        Date date = format1.parse(d + " " + time);
        return date;
    }

    public static void main(String args[]) throws ParseException {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date p =  simpleDateFormat.parse("2023-01-03 14:00:00");
        long dif = getDateGapHour(p, date);
    }
}
