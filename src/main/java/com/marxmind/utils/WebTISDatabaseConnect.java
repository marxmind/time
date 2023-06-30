package com.marxmind.utils;

import jakarta.servlet.http.HttpSession;
import java.sql.DriverManager;
import com.marxmind.bean.SessionBean;
import java.sql.Connection;

public class WebTISDatabaseConnect
{
    public static String getDBName() {
        return ReadConfig.value(AppConf.DB_NAME_WEBTIS);
    }
    
    public static Connection getConnection() {
        Conf conf = Conf.getInstance();
        Connection conn = null;
        try {
            final HttpSession session = SessionBean.getSession();
            final String val = session.getAttribute("server-local").toString();
            final String driver = conf.getDatabaseDriver();
            Class.forName(driver);
            String db_url = conf.getDatabaseUrl();
            if ("false".equalsIgnoreCase(val)) {
                db_url = conf.getDatabaseUrlServer();
            }
            final String port = conf.getDatabasePort();
            final String dbName = conf.getDatabaseMain();
            String timezone = "";
            if (conf.getDatabaseTimeZone() != null && !conf.getDatabaseTimeZone().isEmpty()) {
                timezone = String.valueOf(conf.getDatabaseTimeZone()) + "&";
            }
            final String url = String.valueOf(db_url) + ":" + port + "/" + dbName + "?" + timezone + conf.getDatabaseSSL();
            System.out.println("URL DATA: " + url);
            final String u_name = conf.getDatabaseUserName();
            final String pword = conf.getDatabasePassword();
            conn = DriverManager.getConnection(url, u_name, pword);
            return conn;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void close(final Connection conn) {
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}