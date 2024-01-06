package com.birlax.dbCommonUtils.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BirlaxUtil {

    static Logger LOGGER = LoggerFactory.getLogger(BirlaxUtil.class);

    public static final String YYYYMMDD_DATE_FORMAT = "yyyyMMdd";

    public static Date getDateFromString(String strDate) {

        try {
            return DateUtils.parseDate(strDate, YYYYMMDD_DATE_FORMAT);
        } catch (ParseException e1) {
            throw new IllegalArgumentException("Failed to Parser the String : " + strDate + " as Date");
        }
    }

    public static Date getDateFromString(String strDate, String format) {

        try {
            return DateUtils.parseDate(strDate, format);
        } catch (ParseException e1) {
            throw new IllegalArgumentException("Failed to Parser the String : " + strDate + " as Date");
        }
    }

    public static Date addDate(Date date, int years, int months, int days) {
        // convert date to localdatetime
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        // System.out.println("localDateTime : " + dateFormat8.format(localDateTime));

        // plus one
        localDateTime = localDateTime.plusYears(years).plusMonths(months).plusDays(days);
        // localDateTime = localDateTime.plusHours(1).plusMinutes(2).minusMinutes(1).plusSeconds(1);

        // convert LocalDateTime to date
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String getFormattedStringDate(Date date, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static long diffInDays(Date secondDate, Date firstDate) {
        try {
            return ChronoUnit.DAYS.between(secondDate.toInstant(), firstDate.toInstant());
        } catch (Exception e) {
            throw new RuntimeException(
                    "Something went wrong, secondDate :  " + secondDate + ", firstDate : " + firstDate, e);
        }
    }

}
