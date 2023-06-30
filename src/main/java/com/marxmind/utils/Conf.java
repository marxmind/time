package com.marxmind.utils;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class Conf
{
    private static volatile Conf conf;
    @Getter @Setter private String databaseBank;
    @Getter @Setter private String databaseLand;
    @Getter @Setter private String databaseMain;
    @Getter @Setter private String databaseLicensing;
    @Getter @Setter private String databaseAgriculture;
    @Getter @Setter private String databaseCashBook;
    @Getter @Setter private String databasePort;
    @Getter @Setter private String databaseUrl;
    @Getter @Setter private String databaseUrlServer;
    @Getter @Setter private String databaseDriver;
    @Getter @Setter private String databaseSSL;
    @Getter @Setter private String databaseTimeZone;
    @Getter @Setter private String databaseUserName;
    @Getter @Setter private String databasePassword;
    @Getter @Setter private String serverDatabase;
    @Getter @Setter private String serverDatabaseIp;
    @Getter @Setter private String databaseHomePath;
    
    private Conf() {
        System.out.println("initializing database information...");
    }
    
    public static Conf getInstance() {
        if (Conf.conf == null) {
            synchronized (Conf.class) {
                if (Conf.conf == null) {
                    (Conf.conf = new Conf()).readConf();
                    System.out.println("reading database information");
                }
            }
            // monitorexit(Conf.class)
        }
        return Conf.conf;
    }
    
    private void readConf() {
        try {
            final File file = new File(GlobalVar.APP_DATABASE_CONF);
            final Properties prop = new Properties();
            prop.load(new FileInputStream(file));
            String u_name = SecureChar.decode(prop.getProperty("DATABASE_UNAME"));
            u_name = u_name.replaceAll("mark", "");
            u_name = u_name.replaceAll("rivera", "");
            u_name = u_name.replaceAll("italia", "");
            String pword = SecureChar.decode(prop.getProperty("DATABASE_PASSWORD"));
            pword = pword.replaceAll("mark", "");
            pword = pword.replaceAll("rivera", "");
            pword = pword.replaceAll("italia", "");
            Conf.conf.setDatabaseBank(prop.getProperty("DATABASE_NAME_BANK"));
            Conf.conf.setDatabaseLand(prop.getProperty("DATABASE_NAME_LAND"));
            Conf.conf.setDatabaseMain(prop.getProperty("DATABASE_NAME_MAIN"));
            Conf.conf.setDatabaseLicensing(prop.getProperty("DATABASE_NAME_LICENSING"));
            Conf.conf.setDatabaseAgriculture(prop.getProperty("DATABASE_NAME_AGRICULTURE"));
            Conf.conf.setDatabaseCashBook(prop.getProperty("DATABASE_NAME_CASHBOOK"));
            Conf.conf.setDatabaseDriver(prop.getProperty("DATABASE_DRIVER"));
            Conf.conf.setDatabaseUrl(prop.getProperty("DATABASE_URL"));
            Conf.conf.setDatabaseUrlServer(prop.getProperty("DATABASE_URL_SERVER"));
            Conf.conf.setDatabasePort(prop.getProperty("DATABASE_PORT"));
            Conf.conf.setDatabaseSSL(prop.getProperty("DATABASE_SSL"));
            Conf.conf.setDatabaseTimeZone(prop.getProperty("DATABASE_SERVER_TIME_ZONE"));
            Conf.conf.setDatabaseUserName(u_name);
            Conf.conf.setDatabasePassword(pword);
            Conf.conf.setServerDatabase(prop.getProperty("DATABASE_SERVER_DB_URL"));
            Conf.conf.setServerDatabaseIp(prop.getProperty("DATABASE_SERVER_IP"));
            Conf.conf.setDatabaseHomePath(prop.getProperty("DATABASE_HOME_PATH"));
        }
        catch (Exception e) {
            System.out.println("Configuration file was not set. See error: " + e.getMessage());
        }
    }
    
    
}