package com.marxmind.utils;

import java.time.Period;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;

public class DateUtils
{
    public static long getNumberOfDays(final String dateFrom, final String dateTo) {
        final SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            final Date date1 = myFormat.parse(dateFrom);
            final Date date2 = myFormat.parse(dateTo);
            final long diff = date2.getTime() - date1.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }
    
    public static long getNumberyDaysNow(final String dateValue) {
        return getNumberOfDays(dateValue, getCurrentDateYYYYMMDD());
    }
    
    public static String dayNaming(String date) {
        String result = "";
        final int len = date.length();
        int day = 0;
        if (len == 1) {
            day = Integer.valueOf(date);
        }
        else {
            day = Integer.valueOf(date.substring(1, 2));
        }
        final int first = Integer.valueOf(date.substring(0, 1));
        if (first == 0 || first == 2 || first == 3) {
            switch (day) {
                case 0: {
                    result = "th";
                    break;
                }
                case 1: {
                    result = "st";
                    break;
                }
                case 2: {
                    result = "nd";
                    break;
                }
                case 3: {
                    result = "rd";
                    break;
                }
                case 4: {
                    result = "th";
                    break;
                }
                case 5: {
                    result = "th";
                    break;
                }
                case 6: {
                    result = "th";
                    break;
                }
                case 7: {
                    result = "th";
                    break;
                }
                case 8: {
                    result = "th";
                    break;
                }
                case 9: {
                    result = "th";
                    break;
                }
            }
        }
        if (first == 1) {
            switch (day) {
                case 0: {
                    result = "th";
                    break;
                }
                case 1: {
                    result = "th";
                    break;
                }
                case 2: {
                    result = "th";
                    break;
                }
                case 3: {
                    result = "th";
                    break;
                }
                case 4: {
                    result = "th";
                    break;
                }
                case 5: {
                    result = "th";
                    break;
                }
                case 6: {
                    result = "th";
                    break;
                }
                case 7: {
                    result = "th";
                    break;
                }
                case 8: {
                    result = "th";
                    break;
                }
                case 9: {
                    result = "th";
                    break;
                }
            }
        }
        date = removeFirstZero(date);
        return String.valueOf(date) + result;
    }
    
    private static String removeFirstZero(String number) {
        final String s;
        switch (s = number) {
            case "01": {
                number = "1";
                break;
            }
            case "02": {
                number = "2";
                break;
            }
            case "03": {
                number = "3";
                break;
            }
            case "04": {
                number = "4";
                break;
            }
            case "05": {
                number = "5";
                break;
            }
            case "06": {
                number = "6";
                break;
            }
            case "07": {
                number = "7";
                break;
            }
            case "08": {
                number = "8";
                break;
            }
            case "09": {
                number = "9";
                break;
            }
            default:
                break;
        }
        return number;
    }
    
    public static String getCurrentDateMMMMDDYYYY() {
        final DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getCurrentDateMMDDYYYY() {
        final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getCurrentDateYYYYMMDD() {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getCurrentDateMonthDayYear() {
        final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getCurrentDateMMDDYYYYPlain() {
        final DateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getCurrentDateMMDDYYYYTIME() {
        final DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static String getCurrentDateMMDDYYYYTIMEPlain() {
        final DateFormat dateFormat = new SimpleDateFormat("MMddyyyyhhmmss");
        final Date date = new Date();
        return dateFormat.format(date);
    }
    
    public static int getCurrentDay() {
        final Date date = new Date();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int day = cal.get(5);
        return day;
    }
    
    public static int getCurrentYear() {
        final Calendar now = Calendar.getInstance();
        final int year = now.get(1);
        return year;
    }
    
    public static int getCurrentMonth() {
        final Date date = new Date();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int month = cal.get(2) + 1;
        return month;
    }
    
    public static int monthStartWeek(final int month, final int year) {
        final Calendar cal = new GregorianCalendar(year, month - 1, 1);
        final int dayOfWeek = cal.get(7);
        return dayOfWeek;
    }
    
    public static String getFirstDayOfTheMonth(final String dateFormat, final String dateInputed, final Locale locale) {
        final String date = "";
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
        final LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
        final LocalDate initial = LocalDate.of(getCurrentYear(), getCurrentMonth(), 13);
        final LocalDate start = initial.withDayOfMonth(1);
        return start.toString();
    }
    
    public static int getFirstDay(final int month, final int year, final Locale locale) {
        final String dateInputed = String.valueOf(year) + "-" + ((month < 10) ? ("0" + month) : Integer.valueOf(month)) + "-01";
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale);
        final LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
        final LocalDate initial = LocalDate.of(year, month, 13);
        final LocalDate start = initial.withDayOfMonth(1);
        return start.getDayOfMonth();
    }
    
    public static String getLastDayOfTheMonth(final String dateFormat, final String dateInputed, final Locale locale) {
        final String date = "";
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
        final LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
        final LocalDate initial = LocalDate.of(getCurrentYear(), getCurrentMonth(), 13);
        final LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
        return end.toString();
    }
    
    public static int getLastDay(final int month, final int year, final Locale locale) {
        final String dateInputed = String.valueOf(year) + "-" + ((month < 10) ? ("0" + month) : Integer.valueOf(month)) + "-01";
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale);
        final LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
        final LocalDate initial = LocalDate.of(year, month, 13);
        final LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
        return end.getDayOfMonth();
    }
    
    public static String getLastDayOfTheMonth(final String dateFormat, final String dateInputed, final Locale locale, final int year, final int month) {
        final String date = "";
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
        final LocalDate now = LocalDate.parse(dateInputed, dateTimeFormatter);
        final LocalDate initial = LocalDate.of(year, month, 13);
        final LocalDate end = initial.withDayOfMonth(initial.lengthOfMonth());
        return end.toString();
    }
    
    public static String getEndOfMonthDate(final String dateFormat, final Locale locale) {
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat, locale);
        final LocalDate now = LocalDate.parse(getCurrentDateMMDDYYYY(), dateTimeFormatter);
        final LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());
        String mm = "";
        String dd = "";
        String yyyy = "";
        final String endOfMonth = lastDay.atStartOfDay().toString().split("T")[0];
        yyyy = endOfMonth.split("-")[0];
        mm = endOfMonth.split("-")[1];
        dd = endOfMonth.split("-")[2];
        return String.valueOf(mm) + "-" + dd + "-" + yyyy;
    }
    
    public static String getEndOfMonthDate(final int dateFormat, final Locale locale) {
        String datePatern = "yyyy-MM-dd";
        if (dateFormat == 1) {
            datePatern = "MM-dd-yyyy";
        }
        else if (dateFormat == 2) {
            datePatern = "yyyy-MM-dd";
        }
        else if (dateFormat == 3) {
            datePatern = "dd-MM-yyyy";
        }
        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePatern, locale);
        final LocalDate now = LocalDate.parse(getCurrentDateMMDDYYYY(), dateTimeFormatter);
        final LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());
        String mm = "";
        String dd = "";
        String yyyy = "";
        final String endOfMonth = lastDay.atStartOfDay().toString().split("T")[0];
        yyyy = endOfMonth.split("-")[0];
        mm = endOfMonth.split("-")[1];
        dd = endOfMonth.split("-")[2];
        if (dateFormat == 1) {
            return String.valueOf(mm) + "-" + dd + "-" + yyyy;
        }
        if (dateFormat == 2) {
            return String.valueOf(yyyy) + "-" + mm + "-" + dd;
        }
        if (dateFormat == 3) {
            datePatern = "dd-MM-yyyy";
            return String.valueOf(dd) + "-" + mm + "-" + yyyy;
        }
        return String.valueOf(yyyy) + "-" + mm + "-" + dd;
    }
    
    public static String timeTo12Format(final String time, final boolean isIncludePM) {
        final int hh = Integer.valueOf(time.split(":")[0]);
        final String mm = time.split(":")[1];
        final String ss = time.split(":")[2];
        String result = isIncludePM ? ("00:" + mm + ":" + ss + " PM") : ("00:" + mm + ":" + ss);
        if (hh <= 12) {
            if (hh <= 9) {
                result = (isIncludePM ? ("0" + hh + ":" + mm + ":" + ss + " AM") : ("00:" + mm + ":" + ss));
            }
            else {
                result = (isIncludePM ? (String.valueOf(hh) + ":" + mm + ":" + ss + " AM") : ("00:" + mm + ":" + ss));
            }
        }
        switch (hh) {
            case 13: {
                result = (isIncludePM ? ("01:" + mm + ":" + ss + " PM") : ("01:" + mm + ":" + ss));
            }
            case 14: {
                result = (isIncludePM ? ("02:" + mm + ":" + ss + " PM") : ("02:" + mm + ":" + ss));
            }
            case 15: {
                result = (isIncludePM ? ("03:" + mm + ":" + ss + " PM") : ("03:" + mm + ":" + ss));
            }
            case 16: {
                result = (isIncludePM ? ("04:" + mm + ":" + ss + " PM") : ("04:" + mm + ":" + ss));
            }
            case 17: {
                result = (isIncludePM ? ("05:" + mm + ":" + ss + " PM") : ("05:" + mm + ":" + ss));
            }
            case 18: {
                result = (isIncludePM ? ("06:" + mm + ":" + ss + " PM") : ("06:" + mm + ":" + ss));
            }
            case 19: {
                result = (isIncludePM ? ("07:" + mm + ":" + ss + " PM") : ("07:" + mm + ":" + ss));
            }
            case 20: {
                result = (isIncludePM ? ("08:" + mm + ":" + ss + " PM") : ("08:" + mm + ":" + ss));
            }
            case 21: {
                result = (isIncludePM ? ("09:" + mm + ":" + ss + " PM") : ("09:" + mm + ":" + ss));
            }
            case 22: {
                result = (isIncludePM ? ("10:" + mm + ":" + ss + " PM") : ("10:" + mm + ":" + ss));
            }
            case 23: {
                result = (isIncludePM ? ("11:" + mm + ":" + ss + " PM") : ("11:" + mm + ":" + ss));
            }
            case 0: {
                result = (isIncludePM ? ("12:" + mm + ":" + ss + " PM") : ("12:" + mm + ":" + ss));
                break;
            }
        }
        return result;
    }
    
    public static String convertDateToMonthDayYear(String dateVal) {
        if (dateVal == null || dateVal.isEmpty()) {
            dateVal = getCurrentDateYYYYMMDD();
        }
        final int month = Integer.valueOf(dateVal.split("-")[1]);
        final String year = dateVal.split("-")[0];
        final int day = Integer.valueOf(dateVal.split("-")[2]);
        if (day < 10) {
            dateVal = String.valueOf(getMonthName(month)) + " 0" + day + ", " + year;
        }
        else {
            dateVal = String.valueOf(getMonthName(month)) + " " + day + ", " + year;
        }
        return dateVal;
    }
    
    public static String convertDateToYearMontyDay(String dateVal) {
        if (dateVal == null || dateVal.isEmpty()) {
            dateVal = getCurrentDateMMMMDDYYYY();
        }
        final String tmp = dateVal.split(",")[0];
        final String month = tmp.split(" ")[0];
        final String day = tmp.split(" ")[1];
        final String year = dateVal.split(",")[1].trim();
        dateVal = String.valueOf(year) + "-" + getMonthNumber(month) + "-" + day;
        return dateVal;
    }
    
    public static String getMonthNumber(final String month) {
        switch (month) {
            case "February": {
                return "02";
            }
            case "January": {
                return "01";
            }
            case "September": {
                return "09";
            }
            case "May": {
                return "05";
            }
            case "July": {
                return "07";
            }
            case "June": {
                return "06";
            }
            case "October": {
                return "10";
            }
            case "April": {
                return "04";
            }
            case "March": {
                return "03";
            }
            case "December": {
                return "12";
            }
            case "November": {
                return "11";
            }
            case "August": {
                return "08";
            }
            default:
                break;
        }
        return "January";
    }
    
    public static String getMonthName(final int month) {
        switch (month) {
            case 1: {
                return "January";
            }
            case 2: {
                return "February";
            }
            case 3: {
                return "March";
            }
            case 4: {
                return "April";
            }
            case 5: {
                return "May";
            }
            case 6: {
                return "June";
            }
            case 7: {
                return "July";
            }
            case 8: {
                return "August";
            }
            case 9: {
                return "September";
            }
            case 10: {
                return "October";
            }
            case 11: {
                return "November";
            }
            case 12: {
                return "December";
            }
            default: {
                return "January";
            }
        }
    }
    
    public static String convertDate(final Date date, String format) {
        try {
            if (format == null || format.isEmpty()) {
                format = "yyyy-MM-dd";
            }
            final DateFormat dateFormat = new SimpleDateFormat(format);
            return dateFormat.format(date);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static Date convertDateString(final String datevalue, final String format) {
        try {
            Date date = new Date();
            final DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(datevalue);
            return date;
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static String convertDateCustom(final String datevalue) {
        try {
            final String[] tmp = datevalue.split("-");
            final String month = tmp[1];
            final String day = tmp[2];
            final String year = tmp[0];
            return String.valueOf(month) + "/" + day + "/" + year;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Date getDateToday() {
        return new Date();
    }
    
    public static void main(final String[] args) {
        System.out.println(getFirstDayOfTheMonth("yyyy-MM-dd", getCurrentDateYYYYMMDD(), Locale.TAIWAN));
        System.out.println(getLastDayOfTheMonth("yyyy-MM-dd", getCurrentDateYYYYMMDD(), Locale.TAIWAN));
        System.out.println("year: " + new StringBuilder(String.valueOf(getCurrentYear())).toString().substring(2, 4));
        System.out.println("month: " + getCurrentMonth());
    }
    
    public static int calculateAge(final LocalDate birthDate, final LocalDate currentDate) {
        if (birthDate != null && currentDate != null) {
            return Period.between(birthDate, currentDate).getYears();
        }
        return 0;
    }
    
    public static int calculateAge(final String birthdate) {
        System.out.println("Calculating age");
        if (birthdate != null && !birthdate.isEmpty()) {
            final int year = Integer.valueOf(birthdate.split("-")[0]);
            final int month = Integer.valueOf(birthdate.split("-")[1]);
            final int day = Integer.valueOf(birthdate.split("-")[2]);
            int age = getCurrentYear() - year;
            if (month == getCurrentMonth()) {
                if (day > getCurrentDay()) {
                    --age;
                }
            }
            else if (month > getCurrentMonth()) {
                --age;
            }
            return age;
        }
        return 0;
    }
    
    public static double calculateAgeNow(final String birthdate) {
        if (birthdate != null && !birthdate.isEmpty()) {
            final int year = Integer.valueOf(birthdate.split("-")[0]);
            final int month = Integer.valueOf(birthdate.split("-")[1]);
            final int day = Integer.valueOf(birthdate.split("-")[2]);
            double age = 0.0;
            final LocalDate birtdate = LocalDate.of(year, month, day);
            age = calculateAge(birtdate, LocalDate.of(getCurrentYear(), getCurrentMonth(), getCurrentDay()));
            return age;
        }
        return 0.0;
    }
}