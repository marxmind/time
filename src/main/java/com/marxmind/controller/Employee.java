package com.marxmind.controller;

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
public class Employee
{
    private long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String fullName;
    
    public static List<Employee> retrieve(final String sqlAdd, final String[] params) {
        System.out.println("reading sql " + sqlAdd);
        final List<Employee> emps = new ArrayList<Employee>();
        String sql = "SELECT * FROM employee WHERE isactiveemployee=1 ";
        sql = String.valueOf(sql) + sqlAdd;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            conn = WebTISDatabaseConnect.getConnection();
            ps = conn.prepareStatement(sql);
            System.out.println("SQL: " + ps.toString());
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; ++i) {
                    ps.setString(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                final Employee em = builder().id(rs.getLong("eid")).firstName(rs.getString("firstname")).middleName(rs.getString("middlename")).lastName(rs.getString("lastname")).fullName(rs.getString("fullname")).build();
                emps.add(em);
            }
            rs.close();
            ps.close();
            WebTISDatabaseConnect.close(conn);
        }
        catch (Exception e) {
            e.getMessage();
        }
        return emps;
    }

}
