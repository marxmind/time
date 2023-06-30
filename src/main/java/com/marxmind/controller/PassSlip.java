package com.marxmind.controller;

import java.sql.SQLException;
import com.marxmind.utils.LogU;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Connection;
import com.marxmind.enm.PassSlipStatus;
import com.marxmind.utils.WebTISDatabaseConnect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class PassSlip
{
    private long id;
    private String dateTrans;
    private String permisionFrom;
    private String permisionTo;
    private String reason;
    private String approvedBy;
    private String approvedPos;
    private int isActive;
    private int department;
    private Employee employee;
    private int status;
    private Date tmpDateTrans;
    private String statusName;
    
    public static List<PassSlip> retrieve(String sql, final String[] params) {
        final List<PassSlip> trans = new ArrayList<PassSlip>();
        final String tableEm = "emp";
        final String tablePass = "pl";
        final String sqlAdd = "SELECT * FROM passlip " + tablePass + ",  employee " + tableEm + " WHERE  " + tablePass + ".isactivep=1 AND " + tablePass + ".eid=" + tableEm + ".eid ";
        sql = String.valueOf(sqlAdd) + sql;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    ps.setString(i + 1, params[i]);
                }
            }
            System.out.println("client SQL " + ps.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                final Employee em = Employee.builder().id(rs.getLong("eid")).firstName(rs.getString("firstname")).middleName(rs.getString("middlename")).lastName(rs.getString("lastname")).fullName(String.valueOf(rs.getString("firstname")) + " " + rs.getString("middlename") + " " + rs.getString("lastname")).build();
                final PassSlip pl = builder().id(rs.getLong("pid")).dateTrans(rs.getString("passdate")).permisionFrom(rs.getString("permissionfrom")).permisionTo(rs.getString("permissionto")).reason(rs.getString("reason")).approvedBy(rs.getString("approvedby")).approvedPos(rs.getString("approvedpos")).employee(em).department(rs.getInt("depid")).isActive(rs.getInt("isactivep")).status(rs.getInt("status")).statusName(PassSlipStatus.getSlipStatus(rs.getInt("status"))).build();
                trans.add(pl);
            }
            rs.close();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (Exception e) {
            e.getMessage();
        }
        return trans;
    }
    
    public static PassSlip save(PassSlip st) {
        if (st != null) {
            final long id = getInfo((st.getId() == 0L) ? (getLatestId() + 1L) : st.getId());
            LogU.add("checking for new added data");
            if (id == 1L) {
                LogU.add("insert new Data ");
                st = insertData(st, "1");
            }
            else if (id == 2L) {
                LogU.add("update Data ");
                st = updateData(st);
            }
            else if (id == 3L) {
                LogU.add("added new Data ");
                st = insertData(st, "3");
            }
        }
        return st;
    }
    
    public void save() {
        final long id = getInfo((this.getId() == 0L) ? (getLatestId() + 1L) : this.getId());
        LogU.add("checking for new added data");
        if (id == 1L) {
            LogU.add("insert new Data ");
            insertData(this, "1");
        }
        else if (id == 2L) {
            LogU.add("update Data ");
            updateData(this);
        }
        else if (id == 3L) {
            LogU.add("added new Data ");
            insertData(this, "3");
        }
    }
    
    public static PassSlip insertData(final PassSlip st, final String type) {
        final String sql = "INSERT INTO passlip (pid,passdate,permissionfrom,permissionto,reason,approvedby,approvedpos,eid,depid,isactivep,status) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            long id = 1L;
            int cnt = 1;
            LogU.add("===========================START=========================");
            LogU.add("inserting data into table passlip");
            if ("1".equalsIgnoreCase(type)) {
                ps.setLong(cnt++, id);
                st.setId(id);
                LogU.add("id: 1");
            }
            else if ("3".equalsIgnoreCase(type)) {
                id = getLatestId() + 1L;
                ps.setLong(cnt++, id);
                st.setId(id);
                LogU.add("id: " + id);
            }
            ps.setString(cnt++, st.getDateTrans());
            ps.setString(cnt++, st.getPermisionFrom());
            ps.setString(cnt++, st.getPermisionTo());
            ps.setString(cnt++, st.getReason());
            ps.setString(cnt++, st.getApprovedBy());
            ps.setString(cnt++, st.getApprovedPos());
            ps.setLong(cnt++, st.getEmployee().getId());
            ps.setInt(cnt++, st.getDepartment());
            ps.setInt(cnt++, st.getIsActive());
            ps.setInt(cnt++, st.getStatus());
            LogU.add(st.getDateTrans());
            LogU.add(st.getPermisionFrom());
            LogU.add(st.getPermisionTo());
            LogU.add(st.getReason());
            LogU.add(st.getApprovedBy());
            LogU.add(st.getApprovedPos());
            LogU.add(st.getEmployee().getId());
            LogU.add(st.getDepartment());
            LogU.add(st.getIsActive());
            LogU.add(st.getStatus());
            LogU.add("executing for saving...");
            ps.execute();
            LogU.add("closing...");
            ps.close();
            WebTISDatabaseConnect.close(conn);
            LogU.add("data has been successfully saved...");
        }
        catch (SQLException s) {
            LogU.add("error inserting data to passlip : " + s.getMessage());
        }
        LogU.add("===========================END=========================");
        return st;
    }
    
    public static PassSlip updateData(final PassSlip st) {
        final String sql = "UPDATE passlip SET passdate=?,permissionfrom=?,permissionto=?,reason=?,approvedby=?,approvedpos=?,eid=?,depid=?,status=? WHERE pid=?";
        PreparedStatement ps = null;
        Connection conn = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            int cnt = 1;
            LogU.add("===========================START=========================");
            LogU.add("updating data into table passlip");
            ps.setString(cnt++, st.getDateTrans());
            ps.setString(cnt++, st.getPermisionFrom());
            ps.setString(cnt++, st.getPermisionTo());
            ps.setString(cnt++, st.getReason());
            ps.setString(cnt++, st.getApprovedBy());
            ps.setString(cnt++, st.getApprovedPos());
            ps.setLong(cnt++, st.getEmployee().getId());
            ps.setInt(cnt++, st.getDepartment());
            ps.setInt(cnt++, st.getStatus());
            ps.setLong(cnt++, st.getId());
            LogU.add(st.getDateTrans());
            LogU.add(st.getPermisionFrom());
            LogU.add(st.getPermisionTo());
            LogU.add(st.getReason());
            LogU.add(st.getApprovedBy());
            LogU.add(st.getApprovedPos());
            LogU.add(st.getEmployee().getId());
            LogU.add(st.getDepartment());
            LogU.add(st.getStatus());
            LogU.add(st.getId());
            LogU.add("executing for saving...");
            ps.execute();
            LogU.add("closing...");
            ps.close();
            WebTISDatabaseConnect.close(conn);
            LogU.add("data has been successfully saved...");
        }
        catch (SQLException s) {
            LogU.add("error updating data to passlip : " + s.getMessage());
        }
        LogU.add("===========================END=========================");
        return st;
    }
    
    public static long getLatestId() {
        long id = 0L;
        Connection conn = null;
        PreparedStatement prep = null;
        ResultSet rs = null;
        String sql = "";
        try {
            sql = "SELECT pid FROM passlip  ORDER BY pid DESC LIMIT 1";
            conn = WebTISDatabaseConnect.getConnection();
            prep = conn.prepareStatement(sql);
            rs = prep.executeQuery();
            while (rs.next()) {
                id = rs.getLong("pid");
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
            ps = conn.prepareStatement("SELECT pid FROM passlip WHERE pid=?");
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
        final String sql = "UPDATE passlip set isactivep=0 WHERE pid=?";
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
