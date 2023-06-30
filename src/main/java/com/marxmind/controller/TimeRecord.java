package com.marxmind.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.marxmind.enm.TimeAmPmType;
import com.marxmind.enm.TimeType;
import com.marxmind.utils.DateUtils;
import com.marxmind.utils.LogU;
import com.marxmind.utils.WebTISDatabaseConnect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TimeRecord {

	private long id;
	private int day;
	private int month;
	private int year;
	private int type;
	private int timeInOutType;
	private String timeRecord;
	private String remarks;
	private int isActive;
	private String photoid;
	
	private Employee employee;
	
	private String typeName;
	private String inOutTypeName;
	
	private static String checkIDPresent(String inputId) {
		String eid=inputId;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		try{
			conn = WebTISDatabaseConnect.getConnection();
			ps = conn.prepareStatement("SELECT eid FROM employee WHERE isactiveemployee=1 AND (cctsid='"+ inputId +"' OR eid="+ eid +")");
			rs = ps.executeQuery();
			while(rs.next()){
				eid = rs.getString("eid");
			}
			rs.close();
			ps.close();
			WebTISDatabaseConnect.close(conn);
			}catch(Exception e){e.getMessage();}
		
		return eid;
	}
	
	public static boolean checkiFDoubleEntry(String eid,String time) {
		boolean isExist = false;
		eid = checkIDPresent(eid);
		if(eid!= null && !eid.isEmpty()) {
			
			int day = DateUtils.getCurrentDay();
			int month = DateUtils.getCurrentMonth();
			int year = DateUtils.getCurrentYear();
			String sql = "SELECT eid FROM timerec WHERE isactivetime=1 AND eid="+eid + " AND timerec='" + time+"'";
			sql += " AND dayrec="+day;
			sql += " AND monthrec=" + month;
			sql += " AND yearrec=" + year;
			
			Connection conn = null;
			ResultSet rs = null;
			PreparedStatement ps = null;
			
			try{
			conn = WebTISDatabaseConnect.getConnection();
			System.out.println("is exist checking " + sql);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				System.out.println("time found");
				isExist = true;
			}
			rs.close();
			ps.close();
			WebTISDatabaseConnect.close(conn);
			}catch(Exception e){e.getMessage();}
			
		}
		
		return isExist;
	}
	
	public static List<TimeRecord> retrieve(String sql, String[] params){
		List<TimeRecord> trans = new ArrayList<TimeRecord>();
		
		String tableEm = "emp";
		String tableTime = "tm";
		String sqlAdd = "SELECT * FROM timerec "+ tableTime +",  employee " + tableEm + " WHERE  " + tableTime + ".isactivetime=1 AND " +
				tableTime + ".eid=" + tableEm + ".eid ";
		
		sql = sqlAdd + sql;
		
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
		
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		System.out.println("client SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			 Employee em = Employee.builder()
					   .id(rs.getLong("eid"))
					  .firstName(rs.getString("firstname")) .middleName(rs.getString("middlename"))
					  .lastName(rs.getString("lastname")) .fullName(rs.getString("firstname") + " "
					  + rs.getString("middlename") +" " +rs.getString("lastname")) .build();
			
			TimeRecord rec = TimeRecord.builder()
					.id(rs.getLong("tid"))
					.day(rs.getInt("dayrec"))
					.month(rs.getInt("monthrec"))
					.year(rs.getInt("yearrec"))
					.type(rs.getInt("typetime"))
					.timeInOutType(rs.getInt("timeinout"))
					.timeRecord(rs.getString("timerec"))
					.remarks(rs.getString("remarks"))
					.isActive(rs.getInt("isactivetime"))
					.typeName(TimeType.nameId(rs.getInt("typetime")))
					.inOutTypeName(TimeAmPmType.nameId(rs.getInt("timeinout")))
					.employee(em)
					.photoid(rs.getString("photoid"))
					.build();
			
			trans.add(rec);
			
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){e.getMessage();}
		
		return trans;
	}
	
	
	public static TimeRecord save(TimeRecord st){
		if(st!=null){
			
			long id = TimeRecord.getInfo(st.getId() ==0? TimeRecord.getLatestId()+1 : st.getId());
			LogU.add("checking for new added data");
			if(id==1){
				LogU.add("insert new Data ");
				st = TimeRecord.insertData(st, "1");
			}else if(id==2){
				LogU.add("update Data ");
				st = TimeRecord.updateData(st);
			}else if(id==3){
				LogU.add("added new Data ");
				st = TimeRecord.insertData(st, "3");
			}
			
		}
		return st;
	}
	
	public void save(){
		
		long id = getInfo(getId() ==0? getLatestId()+1 : getId());
		LogU.add("checking for new added data");
		if(id==1){
			LogU.add("insert new Data ");
			TimeRecord.insertData(this, "1");
		}else if(id==2){
			LogU.add("update Data ");
			TimeRecord.updateData(this);
		}else if(id==3){
			LogU.add("added new Data ");
			TimeRecord.insertData(this, "3");
		}
		
 }
	
	
	public static TimeRecord insertData(TimeRecord st, String type){
		String sql = "INSERT INTO timerec ("
				+ "tid,"
				+ "dayrec,"
				+ "monthrec,"
				+ "yearrec,"
				+ "typetime,"
				+ "timeinout,"
				+ "timerec,"
				+ "remarks,"
				+ "eid,"
				+ "isactivetime,"
				+ "photoid)" 
				+ " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("inserting data into table timerec");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			st.setId(id);
			LogU.add("id: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			st.setId(id);
			LogU.add("id: " + id);
		}
		
		ps.setInt(cnt++, st.getDay());
		ps.setInt(cnt++, st.getMonth());
		ps.setInt(cnt++, st.getYear());
		ps.setInt(cnt++, st.getType());
		ps.setInt(cnt++, st.getTimeInOutType());
		ps.setString(cnt++, st.getTimeRecord());
		ps.setString(cnt++, st.getRemarks());
		ps.setLong(cnt++, st.getEmployee().getId());
		ps.setInt(cnt++, st.getIsActive());
		ps.setString(cnt++, st.getPhotoid());
		
		LogU.add(st.getDay());
		LogU.add(st.getMonth());
		LogU.add(st.getYear());
		LogU.add(st.getType());
		LogU.add(st.getTimeInOutType());
		LogU.add(st.getTimeRecord());
		LogU.add(st.getRemarks());
		LogU.add(st.getEmployee().getId());
		LogU.add(st.getIsActive());
		LogU.add(st.getPhotoid());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error inserting data to timerec : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static TimeRecord updateData(TimeRecord st){
		String sql = "UPDATE timerec SET "
				+ "dayrec=?,"
				+ "monthrec=?,"
				+ "yearrec=?,"
				+ "typetime=?,"
				+ "timeinout=?,"
				+ "timerec=?,"
				+ "remarks=?,"
				+ "eid=?,"
				+ "photoid=?" 
				+ " WHERE tid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		int cnt = 1;
		LogU.add("===========================START=========================");
		LogU.add("updating data into table timerec");
		
		ps.setInt(cnt++, st.getDay());
		ps.setInt(cnt++, st.getMonth());
		ps.setInt(cnt++, st.getYear());
		ps.setInt(cnt++, st.getType());
		ps.setInt(cnt++, st.getTimeInOutType());
		ps.setString(cnt++, st.getTimeRecord());
		ps.setString(cnt++, st.getRemarks());
		ps.setLong(cnt++, st.getEmployee().getId());
		ps.setString(cnt++, st.getPhotoid());
		ps.setLong(cnt++, st.getId());
		
		LogU.add(st.getDay());
		LogU.add(st.getMonth());
		LogU.add(st.getYear());
		LogU.add(st.getType());
		LogU.add(st.getTimeInOutType());
		LogU.add(st.getTimeRecord());
		LogU.add(st.getRemarks());
		LogU.add(st.getEmployee().getId());
		LogU.add(st.getPhotoid());
		LogU.add(st.getId());
		
		LogU.add("executing for saving...");
		ps.execute();
		LogU.add("closing...");
		ps.close();
		WebTISDatabaseConnect.close(conn);
		LogU.add("data has been successfully saved...");
		}catch(SQLException s){
			LogU.add("error updating data to timerec : " + s.getMessage());
		}
		LogU.add("===========================END=========================");
		return st;
	}
	
	public static long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT tid FROM timerec  ORDER BY tid DESC LIMIT 1";	
		conn = WebTISDatabaseConnect.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("tid");
		}
		
		rs.close();
		prep.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static long getInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement("SELECT tid FROM timerec WHERE tid=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		
		Connection conn = null;
		PreparedStatement ps = null;
		String sql = "UPDATE timerec set isactivetime=0 WHERE tid=?";
		
		String[] params = new String[1];
		params[0] = getId()+"";
		try{
		conn = WebTISDatabaseConnect.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		WebTISDatabaseConnect.close(conn);
		}catch(SQLException s){}
		
	}
	
}
