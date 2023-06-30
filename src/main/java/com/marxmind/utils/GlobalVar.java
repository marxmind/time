package com.marxmind.utils;

import java.io.File;

public class GlobalVar
{
    public static final String PRIMARY_DRIVE;
    public static final String APP_NAME;
    public static final String SEP;
    public static final String APP_CONF_DIR;
    public static final String APP_DATABASE_CONF;
    public static final String LOG_FOLDER;
    public static final String REPORT_FOLDER;
    public static final boolean LOG_ENABLE = true;
    public static final int DEFAULT_BUFFER_SIZE = 10240;
    public static final String TIME_IMAGE_FOLDER;
    public static final String REPORT_DTR_NAME = "time-dtr";
    
    static {
        PRIMARY_DRIVE = System.getenv("SystemDrive");
        APP_NAME = AppConf.APP_CONFIG_FOLDER_NAME.getValue();
        SEP = File.separator;
        APP_CONF_DIR = "C:" + GlobalVar.SEP + GlobalVar.APP_NAME + GlobalVar.SEP + "conf" + GlobalVar.SEP;
        APP_DATABASE_CONF = "C:" + GlobalVar.SEP + GlobalVar.APP_NAME + GlobalVar.SEP + "conf" + GlobalVar.SEP + "dbconf.max";
        LOG_FOLDER = "C:" + GlobalVar.SEP + GlobalVar.APP_NAME + GlobalVar.SEP + "log" + GlobalVar.SEP;
        REPORT_FOLDER = "C:" + GlobalVar.SEP + GlobalVar.APP_NAME + GlobalVar.SEP + "reports" + GlobalVar.SEP;
        TIME_IMAGE_FOLDER = ReadConfig.value(AppConf.TIME_IMG);
    }
}