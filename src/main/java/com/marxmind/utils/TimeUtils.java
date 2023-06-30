package com.marxmind.utils;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

public class TimeUtils
{
    public static String convertTime(final String val) {
        if ("Set Time".equalsIgnoreCase(val)) {
            return val;
        }
        if ("??".equalsIgnoreCase(val)) {
            return val;
        }
        return time12Format(val);
    }
    
    public static String getCurrentDateMMDDYYYYTIME() {
        final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm: a");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getTime12Format() {
        final DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getTime12FormatPlain() {
        final DateFormat dateFormat = new SimpleDateFormat("hh:mm");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getTime24Format() {
        final DateFormat dateFormat = new SimpleDateFormat("kk:mm aa");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getTime24FormatPlain() {
        final DateFormat dateFormat = new SimpleDateFormat("kk:mm");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static void main(final String[] args) {
        System.out.println(getTime12Format());
        System.out.println(getTime24Format());
    }
    
    public static String checkTime(final String time) {
        final String ampm = time.split(" ")[1];
        final String mm = time.split(":")[1];
        if ("PM".equalsIgnoreCase(ampm)) {
            final int hh = time24Format(Integer.valueOf(time.split(":")[0]));
            return String.valueOf(hh) + ":" + mm;
        }
        return time;
    }
    
    public static int time24Format(final int hour) {
        switch (hour) {
            case 1: {
                return 13;
            }
            case 2: {
                return 14;
            }
            case 3: {
                return 15;
            }
            case 4: {
                return 16;
            }
            case 5: {
                return 17;
            }
            case 6: {
                return 18;
            }
            case 7: {
                return 19;
            }
            case 8: {
                return 20;
            }
            case 9: {
                return 21;
            }
            case 10: {
                return 22;
            }
            case 11: {
                return 23;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static int time12Format(final int hour) {
        switch (hour) {
            case 13: {
                return 1;
            }
            case 14: {
                return 2;
            }
            case 15: {
                return 3;
            }
            case 16: {
                return 4;
            }
            case 17: {
                return 5;
            }
            case 18: {
                return 6;
            }
            case 19: {
                return 7;
            }
            case 20: {
                return 8;
            }
            case 21: {
                return 9;
            }
            case 22: {
                return 10;
            }
            case 23: {
                return 11;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static String time12Format(final String time) {
        if (time == null || time.isEmpty()) {
            return "";
        }
        int hour = 0;
        final String[] times = time.split(":");
        hour = Integer.valueOf(times[0]);
        switch (hour) {
            case 12: {
                return "12:" + times[1] + " PM";
            }
            case 13: {
                return "01:" + times[1] + " PM";
            }
            case 14: {
                return "02:" + times[1] + " PM";
            }
            case 15: {
                return "03:" + times[1] + " PM";
            }
            case 16: {
                return "04:" + times[1] + " PM";
            }
            case 17: {
                return "05:" + times[1] + " PM";
            }
            case 18: {
                return "06:" + times[1] + " PM";
            }
            case 19: {
                return "07:" + times[1] + " PM";
            }
            case 20: {
                return "08:" + times[1] + " PM";
            }
            case 21: {
                return "09:" + times[1] + " PM";
            }
            case 22: {
                return "10:" + times[1] + " PM";
            }
            case 23: {
                return "11:" + times[1] + " PM";
            }
            default: {
                return String.valueOf(time) + " AM";
            }
        }
    }
    
    public static String time24Format(final String time) {
        if (time == null || time.isEmpty()) {
            return "";
        }
        int hour = 0;
        final String[] times = time.split(":");
        hour = Integer.valueOf(times[0]);
        switch (hour) {
            case 1: {
                return "13:" + times[1] + " PM";
            }
            case 2: {
                return "14:" + times[1] + " PM";
            }
            case 3: {
                return "15:" + times[1] + " PM";
            }
            case 4: {
                return "16:" + times[1] + " PM";
            }
            case 5: {
                return "17:" + times[1] + " PM";
            }
            case 6: {
                return "18:" + times[1] + " PM";
            }
            case 7: {
                return "19:" + times[1] + " PM";
            }
            case 8: {
                return "20:" + times[1] + " PM";
            }
            case 9: {
                return "21:" + times[1] + " PM";
            }
            case 10: {
                return "22:" + times[1] + " PM";
            }
            case 11: {
                return "23:" + times[1] + " PM";
            }
            case 12: {
                return "12:" + times[1] + " PM";
            }
            default: {
                return String.valueOf(time) + " AM";
            }
        }
    }
}