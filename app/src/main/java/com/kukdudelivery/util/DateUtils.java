package com.kukdudelivery.util;

import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by admin on 8/2/2017.
 */

public class DateUtils {

    public static String GETLOCALE_DATETIME() {
        Date date = new Date();
        String stringDate = DateFormat.getDateTimeInstance().format(date);
        return stringDate;
    }

    public static String GETLOCALE_DATETIME(Date date) {
        String stringDate = DateFormat.getDateTimeInstance().format(date);
        return stringDate;
    }

    public static String GETLOCALE_DATE(Date date) {
        String stringDate = DateFormat.getDateTimeInstance().format(date);
        return stringDate;
    }

    public static String GETLOCALE_DATE() {
        Date date = new Date();
        String stringDate = DateFormat.getDateInstance().format(date);
        return stringDate;
    }

    public static String GETLOCALE_TIME() {
        Date date = new Date();
        String stringDate = DateFormat.getTimeInstance().format(date);
        return stringDate;
    }

    public static String getYearTodayandDate(String time) {
        Date dateObj = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dateObj = sdf.parse(time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(new SimpleDateFormat("EEEE,MMM.dd,yyyy").format(dateObj));
    }

    public static String getTimeFromTime(String time) {
        Date dateObj = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            dateObj = sdf.parse(time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(new SimpleDateFormat("hh:mm a").format(dateObj));
    }

    public static String getTimeFromCLAIM(String time) {
        Date dateObj = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dateObj = sdf.parse(time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(new SimpleDateFormat("hh:mm a").format(dateObj));
    }

    public static String getOnlyTime(String time) {
        //first you need to use proper date formatter
        DateFormat df = new SimpleDateFormat("hh:mm");
        Date date = null;// converting String to date
        try {
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df.format(date);
    }

    public static String ArabicTimeFromData(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        Date dt = null;
        try {
            dt = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(new SimpleDateFormat("HH:mm").format(dt));
    }

    public static String ArabicDateFromData(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date dt = null;
        try {
            dt = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(new SimpleDateFormat("dd MMM").format(dt));
    }


    public static String VideoDate(String date) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt = null;
        try {
            dt = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(new SimpleDateFormat("MMMM dd,yyyy").format(dt));
    }

    public static String DDMMMYYYY() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate1 = df1.format(c.getTime());
        return formattedDate1;
    }


    public static String getDateFromTime(String time) {
        Date dateObj = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            dateObj = sdf.parse(time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(new SimpleDateFormat("dd MMMM yyyy").format(dateObj));
    }

    private static SimpleDateFormat getDefaultFormat(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateFormat.setTimeZone(TimeZone.getDefault());
        return dateFormat;
    }

    public static String getFormattedDate(Calendar cal, String format) {
        return getDefaultFormat(format).format(cal.getTime());
    }

    public static String getFormattedDate(String date, String inFormat, String outFormat) {
        if (TextUtils.isEmpty(date))
            return "-";
        try {
            return getDefaultFormat(outFormat).format(getDefaultFormat(inFormat).parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "-";
    }

    public static Date getFormattedDate(String date, String format) {
        if (TextUtils.isEmpty(date))
            return new Date();
        try {
            return getDefaultFormat(format).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public interface DATE_FORMAT {
        String SERVER_FORMAT = "yyyy-MM-dd";
        String SERVER_FORMAT_FULL = "yyyy-MM-dd hh:mm:ss";
        String DISPLAY_FORMAT = "dd MMMM yyyy";
    }

}
