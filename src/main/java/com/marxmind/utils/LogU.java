package com.marxmind.utils;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class LogU
{
    public static void add(final Timestamp action) {
        try {
            add(new StringBuilder().append(action).toString());
        }
        catch (Exception e) {
            add("null");
        }
    }
    
    public static void add(final Double action) {
        try {
            add(new StringBuilder().append(action).toString());
        }
        catch (Exception e) {
            add("null");
        }
    }
    
    public static void add(final BigDecimal action) {
        try {
            add(new StringBuilder().append(action).toString());
        }
        catch (Exception e) {
            add("null");
        }
    }
    
    public static void add(final long action) {
        try {
            add(new StringBuilder(String.valueOf(action)).toString());
        }
        catch (Exception e) {
            add("null");
        }
    }
    
    public static void add(final int action) {
        try {
            add(new StringBuilder(String.valueOf(action)).toString());
        }
        catch (Exception e) {
            add("null");
        }
    }
    
    public static void add(final String action) {
        if ("yes".equalsIgnoreCase(ReadConfig.value(AppConf.APP_LOG_INCLUDE))) {
            try {
                final String FILE_LOG_NAME = "systemlog";
                final String FILE_LOG_TMP_NAME = "tmpsystemlog";
                final String EXT = ".log";
                final String logpath = ReadConfig.value(AppConf.APP_LOG_PATH);
                final String finalFile = String.valueOf(logpath) + FILE_LOG_NAME + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + EXT;
                final String tmpFileName = String.valueOf(logpath) + FILE_LOG_TMP_NAME + "-" + DateUtils.getCurrentDateMMDDYYYYPlain() + EXT;
                final File originalFile = new File(finalFile);
                final File logdirectory = new File(ReadConfig.value(AppConf.APP_LOG_PATH));
                if (!logdirectory.isDirectory()) {
                    logdirectory.mkdir();
                }
                if (!originalFile.exists()) {
                    originalFile.createNewFile();
                }
                final BufferedReader br = new BufferedReader(new FileReader(originalFile));
                final File tempFile = new File(tmpFileName);
                final PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
                String line = null;
                while ((line = br.readLine()) != null) {
                    pw.println(line);
                }
                pw.println(String.valueOf(DateUtils.getCurrentDateMMDDYYYYTIME()) + " INFO: " + action);
                pw.flush();
                pw.close();
                br.close();
                if (!originalFile.delete()) {
                    System.out.println("Could not delete file");
                    return;
                }
                if (!tempFile.renameTo(originalFile)) {
                    System.out.println("Could not rename file");
                }
            }
            catch (Exception e) {
                e.getMessage();
            }
        }
    }
}