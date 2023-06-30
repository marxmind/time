package com.marxmind.utils;

import java.io.File;

public enum AppConf
{
    DB_NAME_BANK("DB_NAME_BANK", 0, "databaseBank"), 
    DB_NAME_TAX("DB_NAME_TAX", 1, "databaseTax"), 
    DB_NAME_WEBTIS("DB_NAME_WEBTIS", 2, "databaseWebTis"), 
    DB_AGRICULTURE("DB_AGRICULTURE", 3, "da"), 
    DB_NAME_CASH("DB_NAME_CASH", 4, "databaseCash"), 
    DB_NAME_LICENSING("DB_NAME_LICENSING", 5, "licensing"), 
    DB_DRIVER("DB_DRIVER", 6, "driver"), 
    DB_URL("DB_URL", 7, "url"), 
    DB_PORT("DB_PORT", 8, "port"), 
    DB_SSL("DB_SSL", 9, "SSL"), 
    USER_NAME("USER_NAME", 10, "username"), 
    USER_PASS("USER_PASS", 11, "password"), 
    APP_EXP("APP_EXP", 12, "applicationExp"), 
    APP_VER("APP_VER", 13, "applicationVersion"), 
    APP_COPYRIGHT("APP_COPYRIGHT", 14, "copyright"), 
    APP_OWNER("APP_OWNER", 15, "author"), 
    APP_EMAIL("APP_EMAIL", 16, "supportEamil"), 
    APP_PHONE("APP_PHONE", 17, "supportNo"), 
    SECURITY_ENCRYPTION_FORMAT("SECURITY_ENCRYPTION_FORMAT", 18, "utf-8"), 
    PRIMARY_DRIVE("PRIMARY_DRIVE", 19, System.getenv("SystemDrive")), 
    APP_CONFIG_FOLDER_NAME("APP_CONFIG_FOLDER_NAME", 20, "time"), 
    APP_CONFIG_SETTING_FOLDER("APP_CONFIG_SETTING_FOLDER", 21, "conf"), 
    APP_CONFIG_SETTING_FILE_NAME("APP_CONFIG_SETTING_FILE_NAME", 22, "application.xml"), 
    APP_LOG_INCLUDE("APP_LOG_INCLUDE", 23, "includeLog"), 
    APP_LOG_PATH("APP_LOG_PATH", 24, "logPath"), 
    REPORT_FOLDER("REPORT_FOLDER", 25, "reports"), 
    SEPERATOR("SEPERATOR", 26, File.separator), 
    DTR_REPORT("DTR_REPORT", 27, "dtr"), 
    LICENSING_IMG("LICENSING_IMG", 28, "license-img"), 
    SERVER_LOCAL("SERVER_LOCAL", 29, "server-local"), 
    TIME_IMG("TIME_IMG", 30, "time-img");
    
    private String value;
    
    private AppConf(final String name, final int ordinal, final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}