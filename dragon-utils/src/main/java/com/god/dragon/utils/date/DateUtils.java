package com.god.dragon.utils.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @author wuyuxiang
 * @version 1.0.0
 * @package com.pandora.tools.utils
 * @date 2023/7/12 10:40
 * @description TODO
 */
public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter SIMPLE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter SIMPLE_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /** JDK8日期格式转换 **/
    public static LocalDate parseToDate(LocalDateTime localDateTime){
        return localDateTime.toLocalDate();
    }

    /** Date转LocalDate **/
    public static LocalDate getLocalDate(Date date){
        return  date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /** LocalDate转Date **/
    public static Date getDate(LocalDate date){
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /** JDK8日期格式转换 **/
    public static LocalDateTime parseToDateTime(LocalDate localDate){
        return LocalDateTime.of(localDate,LocalTime.of(0,0,0));
    }

    /** Date转LocalDateTime **/
    public static LocalDateTime getDateTime(Date date){
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** LocalDateTime转Date **/
    public static Date getDate(LocalDateTime date){
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** DateTime清空时分秒 **/
    public static LocalDateTime clearDateTime(LocalDateTime date){
        return date.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

    /** DateTime清空时分秒 **/
    public static LocalDateTime clearDateTime(LocalDate date){
        return parseToDateTime(date);
    }

    /** DateTime获取当天的最后一刻 **/
    public static LocalDateTime endDateTime(LocalDateTime date){
        return date.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
    }

    /** DateTime获取当天的最后一刻 **/
    public static LocalDateTime endDateTime(LocalDate date){
        return LocalDateTime.of(date,LocalTime.of(23,59,59,999999999));
    }

    /** 格式化日期为 yyyy-MM-dd **/
    public static LocalDateTime parseDate(String dateString){
        return LocalDateTime.parse(dateString,DATE_FORMATTER);
    }

    /** 格式化日期为 yyyy-MM-dd */
    public static Date parseDate2Date(String dateString){
        return getDate(LocalDateTime.parse(dateString,DATE_FORMATTER));
    }

    /** 格式化日期为 yyyyMMdd **/
    public static LocalDateTime parseSimpleDate(String dateString){
        return LocalDateTime.parse(dateString,SIMPLE_DATE_FORMATTER);
    }

    /** 格式化日期为 yyyyMMdd **/
    public static Date parseSimpleDate2Date(String dateString){
        return getDate(LocalDateTime.parse(dateString,SIMPLE_DATE_FORMATTER));
    }

    /** 格式化日期为 HH:mm:ss **/
    public static LocalTime parseTime(String dateString){
        return LocalTime.parse(dateString,TIME_FORMATTER);
    }

    /** 格式化日期为 yyyy-MM-dd HH:mm:ss **/
    public static LocalDateTime parseDateTime(String dateString){
        return LocalDateTime.parse(dateString,DATE_TIME_FORMATTER);
    }

    /** 格式化日期为 yyyy-MM-dd HH:mm:ss **/
    public static Date parseDateTime2Date(String dateString){
        return getDate(LocalDateTime.parse(dateString,DATE_TIME_FORMATTER));
    }

    /** 格式化日期为 yyyyMMddHHmmss **/
    public static LocalDateTime parseSimpleDateTime(String dateString){
        return LocalDateTime.parse(dateString,SIMPLE_DATE_TIME_FORMATTER);
    }

    /** 格式化日期为 yyyyMMddHHmmss **/
    public static Date parseSimpleDateTime2Date(String dateString){
        return getDate(LocalDateTime.parse(dateString,SIMPLE_DATE_TIME_FORMATTER));
    }

    /** Date格式化指定时间格式字符串 */
    public static String format(Date date,DateTimeFormatter formatter){
       return formatter.format(getDateTime(date));
    }

    /** Date格式化yyyy-MM-dd格式字符串 */
    public static String formatDate(Date date){
        return DATE_FORMATTER.format(getDateTime(date));
    }

    /** Date格式化yyyyMMdd格式字符串 */
    public static String formatSimpleDate(Date date){
        return SIMPLE_DATE_FORMATTER.format(getDateTime(date));
    }

    /** Date格式化HH:mm:ss格式字符串 */
    public static String formatTime(Date date){
        return TIME_FORMATTER.format(getDateTime(date));
    }

    /** Date格式化yyyy-MM-dd HH:mm:ss字符串 */
    public static String formatDateTime(Date date){
        return DATE_TIME_FORMATTER.format(getDateTime(date));
    }

    /** Date格式化yyyyMMddHHmmss字符串 */
    public static String formatSimpleDateTime(Date date){
        return SIMPLE_DATE_TIME_FORMATTER.format(getDateTime(date));
    }

    /** LocalDate格式化指定时间格式字符串 */
    public static String format(LocalDate date,DateTimeFormatter formatter){
        return formatter.format(date);
    }

    /** LocalDate格式化yyyy-MM-dd格式字符串 */
    public static String formatDate(LocalDate date){
        return DATE_FORMATTER.format(date);
    }

    /** LocalDate格式化yyyyMMdd格式字符串 */
    public static String formatSimpleDate(LocalDate date){
        return SIMPLE_DATE_FORMATTER.format(date);
    }

    /** LocalDate格式化HH:mm:ss格式字符串 */
    public static String formatTime(LocalDate date){
        return TIME_FORMATTER.format(date);
    }

    /** LocalDate格式化yyyy-MM-dd HH:mm:ss字符串 */
    public static String formatDateTime(LocalDate date){
        return DATE_TIME_FORMATTER.format(date);
    }

    /** LocalDate格式化yyyyMMddHHmmss字符串 */
    public static String formatSimpleDateTime(LocalDate date){
        return SIMPLE_DATE_TIME_FORMATTER.format(date);
    }

    /** LocalDateTime格式化指定时间格式字符串 */
    public static String format(LocalDateTime date,DateTimeFormatter formatter){
        return formatter.format(date);
    }

    /** LocalDateTime格式化yyyy-MM-dd格式字符串 */
    public static String formatDate(LocalDateTime date){
        return DATE_FORMATTER.format(date);
    }

    /** LocalDateTime格式化yyyyMMdd格式字符串 */
    public static String formatSimpleDate(LocalDateTime date){
        return SIMPLE_DATE_FORMATTER.format(date);
    }

    /** LocalDateTime格式化HH:mm:ss格式字符串 */
    public static String formatTime(LocalDateTime date){
        return TIME_FORMATTER.format(date);
    }

    /** LocalDateTime格式化yyyy-MM-dd HH:mm:ss字符串 */
    public static String formatDateTime(LocalDateTime date){
        return DATE_TIME_FORMATTER.format(date);
    }

    /** LocalDateTime格式化yyyyMMddHHmmss字符串 */
    public static String formatSimpleDateTime(LocalDateTime date){
        return SIMPLE_DATE_TIME_FORMATTER.format(date);
    }

    /** 获取当前日期 */
    public static LocalDate getCurrentDate(){
        return LocalDate.now();
    }

    /** 获取当前时间 */
    public static LocalTime getCurrentTime(){
        return LocalTime.now();
    }

    /** 获取当前日期时间 */
    public static LocalDateTime getCurrentDateTime(){
        return LocalDateTime.now();
    }

    /** 判断连个日期是否在同一周 JDK8版本 **/
    public static boolean isSameWeek(LocalDate date1, LocalDate date2){
        LocalDate monday1 = date1.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monday2 = date2.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday1.equals(monday2);
    }

    /** 判断连个日期是否在同一周 JDK8版本 **/
    public static boolean isSameWeek(LocalDateTime date1, LocalDateTime date2){
        LocalDateTime monday1 = date1.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime monday2 = date2.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday1.equals(monday2);
    }

    /** 判断连个日期是否在同一月 JDK8版本 **/
    public static boolean isSameMonth(LocalDate date1, LocalDate date2){
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth();
    }

    /** 判断连个日期是否在同一月 JDK8版本 **/
    public static boolean isSameMonth(LocalDateTime date1, LocalDateTime date2){
        return date1.getYear() == date2.getYear() && date1.getMonth() == date2.getMonth();
    }

    /** 判断连个日期是否在同一年 JDK8版本 **/
    public static boolean isSameYear(LocalDate date1, LocalDate date2){
        return date1.getYear() == date2.getYear();
    }

    /** 判断连个日期是否在同一年 JDK8版本 **/
    public static boolean isSameYear(LocalDateTime date1, LocalDateTime date2){
        return  date1.getYear() == date2.getYear();
    }

    /** 获取某个时间的同一周的周一 **/
    public static LocalDate moveToMonday(LocalDate date){
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /** 获取某个时间的同一周的周一 **/
    public static LocalDateTime moveToMonday(LocalDateTime date){
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /** 获取某个时间的同一月的第一个月 **/
    public static LocalDate moveToFirstDayOfMonth(LocalDate date){
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /** 获取某个时间的同一月的第一个月 **/
    public static LocalDateTime moveToFirstDayOfMonth(LocalDateTime date){
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /** 自定义时间间隔 Period.getYears(),getMonths(),getDays()获取间隔的年月日,isNegative()判断date1是否大于date2*/
    public static Period between(LocalDate date1,LocalDate date2){
        return Period.between(date1, date2);
    }

    /** 获取两个时间的间隔,如果date1小于date2会产生负值 **/
    public static long betweenDays(LocalDate date1,LocalDate date2){
        return ChronoUnit.DAYS.between(date1,date2);
    }

    /** 获取两个时间的间隔,如果date1小于date2会产生负值 **/
    public static long betweenDays(LocalDateTime date1,LocalDateTime date2){
        return ChronoUnit.DAYS.between(date1,date2);
    }

    /** 获取两个时间的间隔,如果date1小于date2会产生负值 **/
    public static long betweenMonths(LocalDate date1,LocalDate date2){
        return ChronoUnit.MONTHS.between(date1,date2);
    }

    /** 获取两个时间的间隔,如果date1小于date2会产生负值 **/
    public static long betweenMonths(LocalDateTime date1,LocalDateTime date2){
        return ChronoUnit.MONTHS.between(date1,date2);
    }

    /** 获取两个时间的间隔,如果date1小于date2会产生负值 **/
    public static long betweenYears(LocalDate date1,LocalDate date2){
        return ChronoUnit.YEARS.between(date1,date2);
    }

    /** 获取两个时间的间隔,如果date1小于date2会产生负值 **/
    public static long betweenYears(LocalDateTime date1,LocalDateTime date2){
        return ChronoUnit.YEARS.between(date1,date2);
    }

    /** 获取昨天 **/
    public static LocalDate yesterday(LocalDate date){
        return date.minusDays(1);
    }

    /** 获取昨天 **/
    public static LocalDateTime yesterday(LocalDateTime date){
        return date.minusDays(1);
    }

    /** 获取昨天的刚开始 **/
    public static LocalDateTime yesterdayClear(LocalDateTime date){
        return clearDateTime(date.minusDays(1));
    }

    /** 获取昨天的结束 **/
    public static LocalDateTime yesterdayEnd(LocalDateTime date){
        return endDateTime(date.minusDays(1));
    }

    /** 获取明天 **/
    public static LocalDate tomorrow(LocalDate date){
        return date.plusDays(1);
    }

    /** 获取明天 **/
    public static LocalDateTime tomorrow(LocalDateTime date){
        return date.plusDays(1);
    }

    /** 获取明天的刚开始 **/
    public static LocalDateTime tomorrowClear(LocalDateTime date){
        return clearDateTime(date.plusDays(1));
    }

    /** 获取明天的结束 **/
    public static LocalDateTime tomorrowEnd(LocalDateTime date){
        return endDateTime(date.plusDays(1));
    }

    /** 往前N天 */
    public static LocalDate minusDay(LocalDate date,int days){
        return date.minusDays(days);
    }

    /** 往前N天 */
    public static LocalDateTime minusDay(LocalDateTime date,int days){
        return date.minusDays(days);
    }

    /** 往后N天 */
    public static LocalDate plusDay(LocalDate date,int days){
        return date.plusDays(days);
    }

    /** 往后N天 */
    public static LocalDateTime plusDay(LocalDateTime date,int days){
        return date.plusDays(days);
    }
}
