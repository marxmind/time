package com.marxmind.controller;

import java.sql.SQLException;
import com.marxmind.utils.LogU;
import com.marxmind.utils.DateUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import com.marxmind.utils.WebTISDatabaseConnect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class AppSetting
{
    private long id;
    private String name;
    private String value;
    private int isActive;
    
    public static List<AppSetting> getTargetBudget() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        final List<AppSetting> apps = new ArrayList<AppSetting>();
        final String sql = "SELECT namesetting,settingvalue FROM appsettings WHERE isactivesett=1 AND userdtlsid=0 ORDER BY settingvalue";
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                final AppSetting app = builder().name(rs.getString("namesetting")).value(rs.getString("settingvalue")).build();
                apps.add(app);
            }
            rs.close();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (Exception e) {
            e.getMessage();
        }
        return apps;
    }
    
    public static String getReportSeries() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        final String sql = "SELECT settingvalue FROM appsettings WHERE isactivesett=1 AND namesetting='SERIES' ORDER BY settingvalue DESC LIMIT 1";
        final String[] params = new String[0];
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    ps.setString(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("settingvalue");
            }
            rs.close();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (Exception e) {
            e.getMessage();
        }
        final String year = new StringBuilder(String.valueOf(DateUtils.getCurrentYear())).toString();
        final String month = (DateUtils.getCurrentMonth() < 10) ? ("0" + DateUtils.getCurrentMonth()) : new StringBuilder(String.valueOf(DateUtils.getCurrentMonth())).toString();
        return String.valueOf(year) + "-" + month + "-#000";
    }
    
    private static boolean checkSeriesExist() {
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        final String sql = "SELECT * FROM appsettings WHERE isactivesett=1 AND namesetting='SERIES'";
        final String[] params = new String[0];
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    ps.setString(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            rs.close();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (Exception e) {
            e.getMessage();
        }
        return false;
    }
    
    public static List<AppSetting> retrieveTime(final String param) {
        final List<AppSetting> apps = new ArrayList<AppSetting>();
        final String sql = "SELECT * FROM appsettings  WHERE  isactivesett=1 AND  namesetting like '%" + param + "%'";
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                final AppSetting app = builder().id(rs.getLong("sid")).name(rs.getString("namesetting")).value(rs.getString("settingvalue")).isActive(rs.getInt("isactivesett")).build();
                apps.add(app);
            }
            rs.close();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (Exception e) {
            e.getMessage();
        }
        return apps;
    }
    
    public static AppSetting save(AppSetting pos) {
        if (pos != null) {
            final long id = getInfo((pos.getId() == 0L) ? ((long)(getLatestId() + 1)) : pos.getId());
            LogU.add("checking for new added data");
            if (id == 1L) {
                LogU.add("insert new Data ");
                pos = insertData(pos, "1");
            }
            else if (id == 2L) {
                LogU.add("update Data ");
                pos = updateData(pos);
            }
            else if (id == 3L) {
                LogU.add("added new Data ");
                pos = insertData(pos, "3");
            }
        }
        return pos;
    }
    
    public void save() {
        final long id = getInfo((this.getId() == 0L) ? ((long)(getLatestId() + 1)) : this.getId());
        LogU.add("checking for new added data");
        if (id == 1L) {
            LogU.add("insert new Data ");
            this.insertData("1");
        }
        else if (id == 2L) {
            LogU.add("update Data ");
            this.updateData();
        }
        else if (id == 3L) {
            LogU.add("added new Data ");
            this.insertData("3");
        }
    }
    
    public static AppSetting insertData(final AppSetting pos, final String type) {
        final String sql = "INSERT INTO appsettings (sid,namesetting,settingvalue,isactivesett,userdtlsid)values(?,?,?,?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            int id = 1;
            int cnt = 1;
            LogU.add("===========================START=========================");
            LogU.add("inserting data into table appsettings");
            if ("1".equalsIgnoreCase(type)) {
                ps.setInt(cnt++, id);
                pos.setId(id);
                LogU.add("id: 1");
            }
            else if ("3".equalsIgnoreCase(type)) {
                id = getLatestId() + 1;
                ps.setInt(cnt++, id);
                pos.setId(id);
                LogU.add("id: " + id);
            }
            ps.setString(cnt++, pos.getName());
            ps.setString(cnt++, pos.getValue());
            ps.setInt(cnt++, pos.getIsActive());
            ps.setLong(cnt++, 1L);
            LogU.add(pos.getName());
            LogU.add(pos.getValue());
            LogU.add(pos.getIsActive());
            LogU.add(1);
            LogU.add("executing for saving...");
            ps.execute();
            LogU.add("closing...");
            ps.close();
            WebTISDatabaseConnect.close(conn);
            LogU.add("data has been successfully saved...");
        }
        catch (SQLException s) {
            LogU.add("error inserting data to appsettings : " + s.getMessage());
        }
        LogU.add("===========================END=========================");
        return pos;
    }
    
    public void insertData(final String type) {
        final String sql = "INSERT INTO appsettings (sid,namesetting,settingvalue,isactivesett,userdtlsid)values(?,?,?,?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            int id = 1;
            int cnt = 1;
            LogU.add("===========================START=========================");
            LogU.add("inserting data into table appsettings");
            if ("1".equalsIgnoreCase(type)) {
                ps.setInt(cnt++, id);
                this.setId(id);
                LogU.add("id: 1");
            }
            else if ("3".equalsIgnoreCase(type)) {
                id = getLatestId() + 1;
                ps.setInt(cnt++, id);
                this.setId(id);
                LogU.add("id: " + id);
            }
            ps.setString(cnt++, this.getName());
            ps.setString(cnt++, this.getValue());
            ps.setInt(cnt++, this.getIsActive());
            ps.setLong(cnt++, 1L);
            LogU.add(this.getName());
            LogU.add(this.getValue());
            LogU.add(this.getIsActive());
            LogU.add(1);
            LogU.add("executing for saving...");
            ps.execute();
            LogU.add("closing...");
            ps.close();
            WebTISDatabaseConnect.close(conn);
            LogU.add("data has been successfully saved...");
        }
        catch (SQLException s) {
            LogU.add("error inserting data to appsettings : " + s.getMessage());
        }
        LogU.add("===========================END=========================");
    }
    
    public static AppSetting updateData(final AppSetting pos) {
        final String sql = "UPDATE appsettings SET namesetting=?,settingvalue=?,userdtlsid=? WHERE sid=?";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            int cnt = 1;
            LogU.add("===========================START=========================");
            LogU.add("updating data into table appsettings");
            ps.setString(cnt++, pos.getName());
            ps.setString(cnt++, pos.getValue());
            ps.setLong(cnt++, 1L);
            ps.setLong(cnt++, pos.getId());
            LogU.add(pos.getName());
            LogU.add(pos.getValue());
            LogU.add(1);
            LogU.add(pos.getId());
            LogU.add("executing for saving...");
            ps.execute();
            LogU.add("closing...");
            ps.close();
            WebTISDatabaseConnect.close(conn);
            LogU.add("data has been successfully saved...");
        }
        catch (SQLException s) {
            LogU.add("error updating data to appsettings : " + s.getMessage());
        }
        LogU.add("===========================END=========================");
        return pos;
    }
    
    public void updateData() {
        final String sql = "UPDATE appsettings SET namesetting=?,settingvalue=?,userdtlsid=? WHERE sid=?";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            int cnt = 1;
            LogU.add("===========================START=========================");
            LogU.add("updating data into table appsettings");
            ps.setString(cnt++, this.getName());
            ps.setString(cnt++, this.getValue());
            ps.setLong(cnt++, 1L);
            ps.setLong(cnt++, this.getId());
            LogU.add(this.getName());
            LogU.add(this.getValue());
            LogU.add(1);
            LogU.add(this.getId());
            LogU.add("executing for saving...");
            ps.execute();
            LogU.add("closing...");
            ps.close();
            WebTISDatabaseConnect.close(conn);
            LogU.add("data has been successfully saved...");
        }
        catch (SQLException s) {
            LogU.add("error updating data to appsettings : " + s.getMessage());
        }
        LogU.add("===========================END=========================");
    }
    
    public static int getLatestId() {
        int id = 0;
        Connection conn = null;
        PreparedStatement prep = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = "SELECT sid FROM appsettings  ORDER BY sid DESC LIMIT 1";
            conn = WebTISDatabaseConnect.getConnection();
            prep = conn.prepareStatement(sql);
            rs = prep.executeQuery();
            while (rs.next()) {
                id = rs.getInt("sid");
            }
            rs.close();
            prep.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }
    
    public static long getInfo(final long id) {
        boolean isNotNull = false;
        long result = 0L;
        final long val = getLatestId();
        if (val == 0L) {
            isNotNull = true;
            result = 1L;
            System.out.println("First data");
        }
        if (!isNotNull) {
            isNotNull = isIdNoExist(id);
            if (isNotNull) {
                result = 2L;
                System.out.println("update data");
            }
            else {
                result = 3L;
                System.out.println("add new data");
            }
        }
        return result;
    }
    
    public static boolean isIdNoExist(final long id) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        boolean result = false;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement("SELECT sid FROM appsettings WHERE sid=?");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                result = true;
            }
            rs.close();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void delete(final String sql, final String[] params) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    ps.setString(i + 1, params[i]);
                }
            }
            ps.executeUpdate();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (SQLException ex) {}
    }
    
    public void delete() {
        Connection conn = null;
        PreparedStatement ps = null;
        final String sql = "UPDATE appsettings set isactivesett=0 WHERE sid=?";
        final String[] params = { new StringBuilder(String.valueOf(this.getId())).toString() };
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    ps.setString(i + 1, params[i]);
                }
            }
            ps.executeUpdate();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (SQLException ex) {}
    }
    
    
}
