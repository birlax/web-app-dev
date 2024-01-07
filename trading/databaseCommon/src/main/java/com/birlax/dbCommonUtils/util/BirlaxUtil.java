package com.birlax.dbCommonUtils.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BirlaxUtil {


    public static final String YYYYMMDD_DATE_FORMAT = "yyyyMMdd";
    static Logger LOGGER = LoggerFactory.getLogger(BirlaxUtil.class);

    public static LocalDate getDateFromString(String strDate) {

        try {
            //return DateUtils.parseDate(strDate, YYYYMMDD_DATE_FORMAT);
            return LocalDate.parse(strDate, DateTimeFormatter.ofPattern(YYYYMMDD_DATE_FORMAT));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Failed to Parser the String : " + strDate + " as Date", e);
        }
    }

    public static LocalDate getDateFromString(String strDate, String format) {

        try {
            return LocalDate.parse(strDate, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Failed to Parser the String : " + strDate + " as Date", e);
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

    public static String getFormattedStringDate(LocalDate date, String format) {
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static long diffInDays(LocalDate secondDate, LocalDate firstDate) {
        try {
            return ChronoUnit.DAYS.between(
                    secondDate.atStartOfDay().toInstant(ZoneOffset.UTC),
                    firstDate.atStartOfDay().toInstant(ZoneOffset.UTC));
        } catch (Exception e) {
            throw new RuntimeException(
                    "Something went wrong, secondDate :  " + secondDate + ", firstDate : " + firstDate, e);
        }
    }

}

