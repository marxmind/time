package com.marxmind.bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import com.marxmind.utils.GlobalVar;
import jakarta.faces.context.ExternalContext;
import java.io.File;
import jakarta.faces.context.FacesContext;
import org.primefaces.PrimeFaces;
import java.util.Iterator;
import com.marxmind.utils.Numbers;
import com.marxmind.enm.TimeAmPmType;
import com.marxmind.enm.TimeType;
import java.util.Map;
import java.util.LinkedHashMap;
import jakarta.faces.model.SelectItem;
import java.util.ArrayList;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.ToString;

import com.marxmind.controller.AppSetting;
import com.marxmind.controller.Employee;
import com.marxmind.utils.ReadConfig;
import com.marxmind.utils.AppConf;
import com.marxmind.utils.DateUtils;
import com.marxmind.utils.TimeUtils;
import com.marxmind.utils.PopupMessage;
import com.marxmind.controller.TimeData;
import com.marxmind.controller.TimeRecord;
import java.util.List;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("record")
@ViewScoped
@Data
@ToString
public class RecordBean implements Serializable
{
    private static final long serialVersionUID = 1434354646565L;
    private String searchParam;
    private List<TimeRecord> times;
    private List<TimeData> timeData;
    private double timeInAM;
    private double timeOutAM;
    private double timeInPM;
    private double timeOutPM;
    private double lateCharge;
    private TimeRecord timeSelected;
    private String timeChange;
    private List timeList;
    private String DEFAULT_IMG;
    private double totalLateCharge;
    private double totalMorning;
    private double totalAfternoon;
    private double totalOvertime;
    private double totalRenderedWork;
    
    private int monthId;
    private List months;
    
    public void deleteTime(final TimeRecord rc) {
        rc.delete();
        this.init();
        PopupMessage.addMessage(1, "Success", "Successfully deleted.");
    }
    
    public String convertTime(final String val) {
        if ("Set Time".equalsIgnoreCase(val)) {
            return val;
        }
        return TimeUtils.time12Format(val);
    }
    
    @PostConstruct
    public void init() {
        final String val = ReadConfig.value(AppConf.SERVER_LOCAL);
        final HttpSession session = SessionBean.getSession();
        session.setAttribute("server-local", (Object)val);
        final List<AppSetting> apps = (List<AppSetting>)AppSetting.retrieveTime("TIME-RECORD");
        this.timeInAM = Integer.valueOf(apps.get(0).getValue().split(":")[0]);
        this.timeOutAM = Integer.valueOf(apps.get(1).getValue().split(":")[0]);
        this.timeInPM = Integer.valueOf(apps.get(2).getValue().split(":")[0]);
        this.timeOutPM = Integer.valueOf(apps.get(3).getValue().split(":")[0]);
        this.lateCharge = Double.valueOf(apps.get(4).getValue());
        
        monthId = DateUtils.getCurrentMonth();
        months = new ArrayList<>();
        for(int m=1; m<=12; m++) {
        	months.add(new SelectItem(m, DateUtils.getMonthName(m)));
        }
        
        this.loadTimeList();
        this.loadSearch();
    }
    
    private void loadTimeList() {
        this.timeChange = "";
        this.timeList = new ArrayList();
        for (int hh = 1; hh <= 12; ++hh) {
            for (int mm = 0; mm <= 59; ++mm) {
                final String hours = (hh < 10) ? ("0" + hh) : new StringBuilder(String.valueOf(hh)).toString();
                final String minutes = (mm < 10) ? ("0" + mm) : new StringBuilder(String.valueOf(mm)).toString();
                this.timeList.add(new SelectItem((Object)(String.valueOf(hours) + ":" + minutes), String.valueOf(hours) + ":" + minutes));
            }
        }
    }
    
    public void loadSearch() {
        this.totalLateCharge = 0.0;
        this.totalMorning = 0.0;
        this.totalAfternoon = 0.0;
        this.totalOvertime = 0.0;
        this.totalRenderedWork = 0.0;
        String sql = "";
        this.times = new ArrayList<TimeRecord>();
        if (this.getSearchParam() != null && !this.getSearchParam().isEmpty() && this.getSearchParam().contains(":")) {
            final String[] vals = this.getSearchParam().split(":");
            switch (vals.length) {
                case 2: {
                    sql = String.valueOf(sql) + " AND tm.monthrec=" + vals[0];
                    sql = String.valueOf(sql) + " AND tm.yearrec=" + vals[1];
                    break;
                }
                case 3: {
                    sql = String.valueOf(sql) + " AND tm.monthrec=" + vals[0];
                    sql = String.valueOf(sql) + " AND tm.yearrec=" + vals[1];
                    int num = 0;
                    try {
                        num = Integer.valueOf(vals[2]);
                        sql = String.valueOf(sql) + " AND tm.dayrec=" + num;
                    }
                    catch (NumberFormatException nu) {
                        sql = String.valueOf(sql) + " AND (emp.firstname like '%" + vals[2] + "%' OR emp.middlename like '%" + vals[2] + "%' OR emp.lastname like '%" + vals[2] + "%')";
                    }
                    break;
                }
                case 4: {
                    sql = String.valueOf(sql) + " AND tm.monthrec=" + vals[0];
                    sql = String.valueOf(sql) + " AND tm.yearrec=" + vals[1];
                    int num = 0;
                    try {
                        num = Integer.valueOf(vals[2]);
                        sql = String.valueOf(sql) + " AND tm.dayrec=" + num;
                    }
                    catch (NumberFormatException nu) {
                        sql = String.valueOf(sql) + " AND (emp.firstname like '%" + vals[2] + "%' OR emp.middlename like '%" + vals[2] + "%' OR emp.lastname like '%" + vals[2] + "%')";
                    }
                    num = 0;
                    try {
                        num = Integer.valueOf(vals[3]);
                        sql = String.valueOf(sql) + " AND tm.dayrec=" + num;
                    }
                    catch (NumberFormatException nu) {
                        sql = String.valueOf(sql) + " AND (emp.firstname like '%" + vals[3] + "%' OR emp.middlename like '%" + vals[3] + "%' OR emp.lastname like '%" + vals[3] + "%')";
                    }
                    break;
                }
            }
        }
        else if (this.getSearchParam() != null) {
            sql = " AND (emp.firstname like '%" + this.getSearchParam() + "%' ";
            sql = String.valueOf(sql) + " OR emp.middlename like '%" + this.getSearchParam() + "%' ";
            sql = String.valueOf(sql) + " OR emp.lastname like '%" + this.getSearchParam() + "%')";
        }
        sql = String.valueOf(sql) + " ORDER BY emp.lastname";
        final Map<Long, Map<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>>> nameData = new LinkedHashMap<Long, Map<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>>>();
        Map<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>> yearData = new LinkedHashMap<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>>();
        Map<Integer, Map<Integer, List<TimeRecord>>> monthData = new LinkedHashMap<Integer, Map<Integer, List<TimeRecord>>>();
        Map<Integer, List<TimeRecord>> dayData = new LinkedHashMap<Integer, List<TimeRecord>>();
        List<TimeRecord> data = new ArrayList<TimeRecord>();
        for (final TimeRecord rc : TimeRecord.retrieve(sql, new String[0])) {
            final long id = rc.getEmployee().getId();
            final int year = rc.getYear();
            final int month = rc.getMonth();
            final int day = rc.getDay();
            if (nameData != null) {
                if (nameData.containsKey(id)) {
                    if (nameData.get(id).containsKey(year)) {
                        if (nameData.get(id).get(year).containsKey(month)) {
                            if (nameData.get(id).get(year).get(month).containsKey(day)) {
                                nameData.get(id).get(year).get(month).get(day).add(rc);
                            }
                            else {
                                data = new ArrayList<TimeRecord>();
                                data.add(rc);
                                nameData.get(id).get(year).get(month).put(day, data);
                            }
                        }
                        else {
                            data = new ArrayList<TimeRecord>();
                            dayData = new LinkedHashMap<Integer, List<TimeRecord>>();
                            data.add(rc);
                            dayData.put(day, data);
                            nameData.get(id).get(year).put(month, dayData);
                        }
                    }
                    else {
                        data = new ArrayList<TimeRecord>();
                        dayData = new LinkedHashMap<Integer, List<TimeRecord>>();
                        monthData = new LinkedHashMap<Integer, Map<Integer, List<TimeRecord>>>();
                        data.add(rc);
                        dayData.put(day, data);
                        monthData.put(month, dayData);
                        nameData.get(id).put(year, monthData);
                    }
                }
                else {
                    data = new ArrayList<TimeRecord>();
                    dayData = new LinkedHashMap<Integer, List<TimeRecord>>();
                    monthData = new LinkedHashMap<Integer, Map<Integer, List<TimeRecord>>>();
                    yearData = new LinkedHashMap<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>>();
                    data.add(rc);
                    dayData.put(day, data);
                    monthData.put(month, dayData);
                    yearData.put(year, monthData);
                    nameData.put(id, yearData);
                }
            }
            else {
                data.add(rc);
                dayData.put(day, data);
                monthData.put(month, dayData);
                yearData.put(year, monthData);
                nameData.put(id, yearData);
            }
        }
        this.timeData = new ArrayList<TimeData>();
        for (final long id2 : nameData.keySet()) {
            for (final int year2 : nameData.get(id2).keySet()) {
                for (final int month : nameData.get(id2).get(year2).keySet()) {
                    for (final int day2 : nameData.get(id2).get(year2).get(month).keySet()) {
                        TimeData dt = new TimeData();
                        final List<TimeRecord> tdata = new ArrayList<TimeRecord>();
                        TimeRecord rcTmp = null;
                        final List<TimeRecord> dataVals = nameData.get(id2).get(year2).get(month).get(day2);
                        for (final TimeRecord rc2 : dataVals) {
                            switch (rc2.getTimeInOutType()) {
                                case 0: {
                                    dt.setTime1(rc2.getTimeRecord());
                                    dt.setDate(String.valueOf(month) + "/" + day2 + "/" + year2);
                                    dt.setEmployeeName(rc2.getEmployee().getFullName());
                                    dt.setData1(rc2);
                                    break;
                                }
                                case 1: {
                                    dt.setTime2(rc2.getTimeRecord());
                                    dt.setData2(rc2);
                                    break;
                                }
                                case 2: {
                                    dt.setTime3(rc2.getTimeRecord());
                                    dt.setData3(rc2);
                                    break;
                                }
                                case 3: {
                                    dt.setTime4(rc2.getTimeRecord());
                                    dt.setData4(rc2);
                                    break;
                                }
                                case 4: {
                                    dt.setTime5(rc2.getTimeRecord());
                                    dt.setData5(rc2);
                                    break;
                                }
                                case 5: {
                                    dt.setTime6(rc2.getTimeRecord());
                                    dt.setData6(rc2);
                                    break;
                                }
                            }
                            tdata.add(rc2);
                            rcTmp = rc2;
                        }
                        final int size = dataVals.size();
                        System.out.println("size==========" + size);
                        if (size < 6 && rcTmp != null) {
                            for (int i = size; i <= 5; ++i) {
                                final TimeRecord rc3 = TimeRecord.builder().month(rcTmp.getMonth()).day(rcTmp.getDay()).year(rcTmp.getYear()).type(TimeType.REGULAR.getId()).employee(rcTmp.getEmployee()).isActive(1).build();
                                switch (i) {
                                    case 1: {
                                        dt.setTime2("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.AM_OUT.getId());
                                        dt.setData2(rc3);
                                        break;
                                    }
                                    case 2: {
                                        dt.setTime3("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.PM_IN.getId());
                                        dt.setData3(rc3);
                                        break;
                                    }
                                    case 3: {
                                        dt.setTime4("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.PM_OUT.getId());
                                        dt.setData4(rc3);
                                        break;
                                    }
                                    case 4: {
                                        dt.setTime5("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.OT_IN.getId());
                                        dt.setData5(rc3);
                                        break;
                                    }
                                    case 5: {
                                        dt.setTime6("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.OT_OUT.getId());
                                        dt.setData6(rc3);
                                        break;
                                    }
                                }
                                tdata.add(rc3);
                            }
                        }
                        dt = this.calculateTime(tdata, dt);
                        this.timeData.add(dt);
                    }
                }
            }
        }
        this.timeData.add(TimeData.builder().late("Total").lateCharge(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalLateCharge, 2))).toString()).morningTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalMorning, 2))).toString()).afternoonTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalAfternoon, 2))).toString()).totalOt(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalOvertime, 2))).toString()).totalRenderedTime(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalRenderedWork, 2))).toString()).build());
    }
    
    private TimeData calculateTime(final List<TimeRecord> times, final TimeData data) {
        final int size = times.size();
        if (size == 1) {
            final String[] time1 = times.get(0).getTimeRecord().split(":");
            final double hh1 = Integer.valueOf(time1[0]);
            final double mm1 = Integer.valueOf(time1[1]);
            double late = 0.0;
            double start = hh1 + mm1 / 60.0;
            if (start <= this.timeInAM) {
                start = this.timeInAM;
            }
            else {
                late = start - this.timeInAM;
                data.setLate(new StringBuilder(String.valueOf(Numbers.roundOf(late, 2))).toString());
                late *= 60.0;
                late *= this.lateCharge;
                data.setLateCharge(new StringBuilder(String.valueOf(Numbers.roundOf(late, 2))).toString());
            }
            this.totalLateCharge += late;
        }
        else if (size == 2 || size == 3) {
            final String[] time1 = times.get(0).getTimeRecord().split(":");
            final double hh1 = Integer.valueOf(time1[0]);
            final double mm1 = Integer.valueOf(time1[1]);
            final String[] time2 = times.get(1).getTimeRecord().split(":");
            final double hh2 = Integer.valueOf(time2[0]);
            final double mm2 = Integer.valueOf(time2[1]);
            double start2 = hh1 + mm1 / 60.0;
            double late2 = 0.0;
            if (start2 <= this.timeInAM) {
                start2 = this.timeInAM;
            }
            else {
                late2 = start2 - this.timeInAM;
                data.setLate(new StringBuilder(String.valueOf(Numbers.roundOf(late2, 2))).toString());
                late2 *= 60.0;
                late2 *= this.lateCharge;
                data.setLateCharge(new StringBuilder(String.valueOf(Numbers.roundOf(late2, 2))).toString());
            }
            double end = hh2 + mm2 / 60.0;
            if (end >= this.timeOutAM) {
                end = this.timeOutAM;
            }
            double total = end - start2;
            if (total < 0.0) {
                total = 0.0;
            }
            data.setMorningTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(total, 2))).toString());
            this.totalLateCharge += late2;
            this.totalMorning += total;
            this.totalRenderedWork += total;
        }
        else if (size == 4) {
            final String[] time1 = times.get(0).getTimeRecord().split(":");
            final double hh1 = Integer.valueOf(time1[0]);
            final double mm1 = Integer.valueOf(time1[1]);
            final String[] time2 = times.get(1).getTimeRecord().split(":");
            final double hh2 = Integer.valueOf(time2[0]);
            final double mm2 = Integer.valueOf(time2[1]);
            final String[] time3 = times.get(2).getTimeRecord().split(":");
            final double hh3 = Integer.valueOf(time3[0]);
            final double mm3 = Integer.valueOf(time3[1]);
            final String[] time4 = times.get(3).getTimeRecord().split(":");
            final double hh4 = Integer.valueOf(time4[0]);
            final double mm4 = Integer.valueOf(time4[1]);
            double startM = hh1 + mm1 / 60.0;
            double late3 = 0.0;
            if (startM <= this.timeInAM) {
                startM = this.timeInAM;
            }
            else {
                late3 = startM - this.timeInAM;
                System.out.println("late: " + late3);
                data.setLate(new StringBuilder(String.valueOf(Numbers.roundOf(late3, 2))).toString());
                late3 *= 60.0;
                late3 *= this.lateCharge;
                data.setLateCharge(new StringBuilder(String.valueOf(Numbers.roundOf(late3, 2))).toString());
            }
            double endM = hh2 + mm2 / 60.0;
            if (endM >= this.timeOutAM) {
                endM = this.timeOutAM;
            }
            double totalM = endM - startM;
            if (totalM < 0.0) {
                totalM = 0.0;
            }
            data.setMorningTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(totalM, 2))).toString());
            double startP = hh3 + mm3 / 60.0;
            if (startP <= this.timeInPM) {
                startP = this.timeInPM;
            }
            double endP = hh4 + mm4 / 60.0;
            if (endP >= this.timeOutPM) {
                endP = this.timeOutPM;
            }
            double totalP = endP - startP;
            if (totalP < 0.0) {
                totalP = 0.0;
            }
            data.setAfternoonTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(totalP, 2))).toString());
            final double totalRecorded = totalM + totalP;
            data.setTotalRenderedTime(new StringBuilder(String.valueOf(Numbers.roundOf(totalRecorded, 2))).toString());
            this.totalLateCharge += late3;
            this.totalMorning += totalM;
            this.totalAfternoon += totalP;
            this.totalRenderedWork += totalM + totalP;
        }
        else if (size == 5 || size == 6) {
            final String[] time1 = times.get(0).getTimeRecord().split(":");
            final double hh1 = Integer.valueOf(time1[0]);
            final double mm1 = Integer.valueOf(time1[1]);
            final String[] time2 = times.get(1).getTimeRecord().split(":");
            final double hh2 = Integer.valueOf(time2[0]);
            final double mm2 = Integer.valueOf(time2[1]);
            final String[] time3 = times.get(2).getTimeRecord().split(":");
            final double hh3 = Integer.valueOf(time3[0]);
            final double mm3 = Integer.valueOf(time3[1]);
            final String[] time4 = times.get(3).getTimeRecord().split(":");
            final double hh4 = Integer.valueOf(time4[0]);
            final double mm4 = Integer.valueOf(time4[1]);
            final String[] time5 = times.get(4).getTimeRecord().split(":");
            final double hh5 = Integer.valueOf(time5[0]);
            final double mm5 = Integer.valueOf(time5[1]);
            final double startOt = hh5 + mm5 / 60.0;
            String[] time6 = new String[2];
            double hh6 = 0.0;
            double mm6 = 0.0;
            double endOt = 0.0;
            double totalOt = 0.0;
            if (size == 6) {
                time6 = times.get(5).getTimeRecord().split(":");
                hh6 = Integer.valueOf(time6[0]);
                mm6 = Integer.valueOf(time6[1]);
                endOt = hh6 + mm6 / 60.0;
                totalOt = endOt - startOt;
            }
            double startM2 = hh1 + mm1 / 60.0;
            double late4 = 0.0;
            if (startM2 <= this.timeInAM) {
                startM2 = this.timeInAM;
            }
            else {
                late4 = startM2 - this.timeInAM;
                System.out.println("late: " + late4);
                data.setLate(new StringBuilder(String.valueOf(Numbers.roundOf(late4, 2))).toString());
                late4 *= 60.0;
                late4 *= this.lateCharge;
                data.setLateCharge(new StringBuilder(String.valueOf(Numbers.roundOf(late4, 2))).toString());
            }
            double endM2 = hh2 + mm2 / 60.0;
            if (endM2 >= this.timeOutAM) {
                endM2 = this.timeOutAM;
            }
            double totalM2 = endM2 - startM2;
            if (totalM2 < 0.0) {
                totalM2 = 0.0;
            }
            data.setMorningTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(totalM2, 2))).toString());
            double startP2 = hh3 + mm3 / 60.0;
            if (startP2 <= this.timeInPM) {
                startP2 = this.timeInPM;
            }
            double endP2 = hh4 + mm4 / 60.0;
            if (endP2 >= this.timeOutPM) {
                endP2 = this.timeOutPM;
            }
            double totalP2 = endP2 - startP2;
            if (totalP2 < 0.0) {
                totalP2 = 0.0;
            }
            data.setAfternoonTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(totalP2, 2))).toString());
            final double totalRecorded2 = totalM2 + totalP2 + totalOt;
            data.setTotalRenderedTime(new StringBuilder(String.valueOf(Numbers.roundOf(totalRecorded2, 2))).toString());
            data.setTotalOt(new StringBuilder(String.valueOf(Numbers.roundOf(totalOt, 2))).toString());
            this.totalLateCharge += late4;
            this.totalMorning += totalM2;
            this.totalAfternoon += totalP2;
            this.totalOvertime += totalOt;
            this.totalRenderedWork += totalM2 + totalP2 + totalOt;
        }
        return data;
    }
    
    public void timeDetails(final TimeRecord rc) {
        final PrimeFaces pf = PrimeFaces.current();
        pf.executeScript("PF('panelWgr').show(1000)");
        System.out.println("Type of Time : " + rc.getTimeInOutType());
        this.copiedIMG(rc.getPhotoid());
        rc.setPhotoid(this.replacePhotoIfNotExist(rc.getPhotoid()));
        this.setTimeSelected(rc);
        String timeNew = rc.getTimeRecord();
        System.out.println("added time to before change: " + timeNew);
        if (rc.getTimeInOutType() >= 2) {
            timeNew = TimeUtils.time12Format(timeNew);
            timeNew = timeNew.replace(" PM", "");
            timeNew = timeNew.replace(" AM", "");
        }
        System.out.println("added time to change: " + timeNew);
        this.setTimeChange(timeNew);
    }
    
    private String replacePhotoIfNotExist(final String filename) {
        if (filename == null) {
            return this.DEFAULT_IMG;
        }
        final ExternalContext EXTERNAL_CONTEXT = FacesContext.getCurrentInstance().getExternalContext();
        final String newCopiedFile = String.valueOf(EXTERNAL_CONTEXT.getRealPath("")) + "resources" + File.separator + "time" + File.separator + "images" + File.separator + "photocam" + File.separator + filename;
        final File file = new File(newCopiedFile);
        if (!file.exists()) {
            System.out.println("File is not exist....");
            return this.DEFAULT_IMG;
        }
        return filename;
    }
    
    private void copiedIMG(final String filename) {
        final ExternalContext EXTERNAL_CONTEXT = FacesContext.getCurrentInstance().getExternalContext();
        final String newCopiedFile = String.valueOf(EXTERNAL_CONTEXT.getRealPath("")) + "resources" + File.separator + "time" + File.separator + "images" + File.separator + "photocam" + File.separator + filename;
        final File file = new File(String.valueOf(GlobalVar.TIME_IMAGE_FOLDER) + filename);
        try {
            Files.copy(file.toPath(), new File(newCopiedFile).toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Successfully copying image : " + newCopiedFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void saveDate() {
        final TimeRecord rec = this.getTimeSelected();
        System.out.println("Type of Time : " + rec.getTimeInOutType());
        String timeNew = this.getTimeChange();
        System.out.println("added time to before saving: " + timeNew);
        if (rec.getTimeInOutType() >= 2) {
            timeNew = TimeUtils.time24Format(timeNew);
            timeNew = timeNew.replace(" PM", "");
            timeNew = timeNew.replace(" AM", "");
        }
        System.out.println("AMPM OUT " + rec.getTimeInOutType());
        System.out.println("added time to after saving: " + timeNew);
        rec.setTimeRecord(timeNew);
        rec.save();
        this.loadSearch();
    }
    
    public void loadLateMonth() {
    	this.timeData = new ArrayList<TimeData>();
    	String sql = ""; 
    	List<Employee> emps = Employee.retrieve(" AND departmentid=1", new String[0]);
    	
    	for(Employee e : emps) {
    		
    		this.totalLateCharge = 0.0;
            this.totalMorning = 0.0;
            this.totalAfternoon = 0.0;
            this.totalOvertime = 0.0;
            this.totalRenderedWork = 0.0;	
    		
    	sql = " AND emp.eid=" + e.getId();
    	sql += " AND tm.monthrec="+getMonthId();
    	sql += " AND tm.yearrec="+DateUtils.getCurrentYear();
    	sql += String.valueOf(sql) + " ORDER BY tm.tid";
    	System.out.println("SQL Month ::>> " + sql);
        final Map<Long, Map<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>>> nameData = new LinkedHashMap<Long, Map<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>>>();
        Map<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>> yearData = new LinkedHashMap<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>>();
        Map<Integer, Map<Integer, List<TimeRecord>>> monthData = new LinkedHashMap<Integer, Map<Integer, List<TimeRecord>>>();
        Map<Integer, List<TimeRecord>> dayData = new LinkedHashMap<Integer, List<TimeRecord>>();
        List<TimeRecord> data = new ArrayList<TimeRecord>();
        for (final TimeRecord rc : TimeRecord.retrieve(sql, new String[0])) {
            final long id = rc.getEmployee().getId();
            final int year = rc.getYear();
            final int month = rc.getMonth();
            final int day = rc.getDay();
            if (nameData != null) {
                if (nameData.containsKey(id)) {
                    if (nameData.get(id).containsKey(year)) {
                        if (nameData.get(id).get(year).containsKey(month)) {
                            if (nameData.get(id).get(year).get(month).containsKey(day)) {
                                nameData.get(id).get(year).get(month).get(day).add(rc);
                            }
                            else {
                                data = new ArrayList<TimeRecord>();
                                data.add(rc);
                                nameData.get(id).get(year).get(month).put(day, data);
                            }
                        }
                        else {
                            data = new ArrayList<TimeRecord>();
                            dayData = new LinkedHashMap<Integer, List<TimeRecord>>();
                            data.add(rc);
                            dayData.put(day, data);
                            nameData.get(id).get(year).put(month, dayData);
                        }
                    }
                    else {
                        data = new ArrayList<TimeRecord>();
                        dayData = new LinkedHashMap<Integer, List<TimeRecord>>();
                        monthData = new LinkedHashMap<Integer, Map<Integer, List<TimeRecord>>>();
                        data.add(rc);
                        dayData.put(day, data);
                        monthData.put(month, dayData);
                        nameData.get(id).put(year, monthData);
                    }
                }
                else {
                    data = new ArrayList<TimeRecord>();
                    dayData = new LinkedHashMap<Integer, List<TimeRecord>>();
                    monthData = new LinkedHashMap<Integer, Map<Integer, List<TimeRecord>>>();
                    yearData = new LinkedHashMap<Integer, Map<Integer, Map<Integer, List<TimeRecord>>>>();
                    data.add(rc);
                    dayData.put(day, data);
                    monthData.put(month, dayData);
                    yearData.put(year, monthData);
                    nameData.put(id, yearData);
                }
            }
            else {
                data.add(rc);
                dayData.put(day, data);
                monthData.put(month, dayData);
                yearData.put(year, monthData);
                nameData.put(id, yearData);
            }
        }
       
        for (final long id2 : nameData.keySet()) {
            for (final int year2 : nameData.get(id2).keySet()) {
                for (final int month : nameData.get(id2).get(year2).keySet()) {
                    for (final int day2 : nameData.get(id2).get(year2).get(month).keySet()) {
                        TimeData dt = new TimeData();
                        final List<TimeRecord> tdata = new ArrayList<TimeRecord>();
                        TimeRecord rcTmp = null;
                        final List<TimeRecord> dataVals = nameData.get(id2).get(year2).get(month).get(day2);
                        for (final TimeRecord rc2 : dataVals) {
                            switch (rc2.getTimeInOutType()) {
                                case 0: {
                                    dt.setTime1(rc2.getTimeRecord());
                                    dt.setDate(String.valueOf(month) + "/" + day2 + "/" + year2);
                                    dt.setEmployeeName(rc2.getEmployee().getFullName());
                                    dt.setData1(rc2);
                                    break;
                                }
                                case 1: {
                                    dt.setTime2(rc2.getTimeRecord());
                                    dt.setData2(rc2);
                                    break;
                                }
                                case 2: {
                                    dt.setTime3(rc2.getTimeRecord());
                                    dt.setData3(rc2);
                                    break;
                                }
                                case 3: {
                                    dt.setTime4(rc2.getTimeRecord());
                                    dt.setData4(rc2);
                                    break;
                                }
                                case 4: {
                                    dt.setTime5(rc2.getTimeRecord());
                                    dt.setData5(rc2);
                                    break;
                                }
                                case 5: {
                                    dt.setTime6(rc2.getTimeRecord());
                                    dt.setData6(rc2);
                                    break;
                                }
                            }
                            tdata.add(rc2);
                            rcTmp = rc2;
                        }
                        final int size = dataVals.size();
                        System.out.println("size==========" + size);
                        if (size < 6 && rcTmp != null) {
                            for (int i = size; i <= 5; ++i) {
                                final TimeRecord rc3 = TimeRecord.builder().month(rcTmp.getMonth()).day(rcTmp.getDay()).year(rcTmp.getYear()).type(TimeType.REGULAR.getId()).employee(rcTmp.getEmployee()).isActive(1).build();
                                switch (i) {
                                    case 1: {
                                        dt.setTime2("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.AM_OUT.getId());
                                        dt.setData2(rc3);
                                        break;
                                    }
                                    case 2: {
                                        dt.setTime3("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.PM_IN.getId());
                                        dt.setData3(rc3);
                                        break;
                                    }
                                    case 3: {
                                        dt.setTime4("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.PM_OUT.getId());
                                        dt.setData4(rc3);
                                        break;
                                    }
                                    case 4: {
                                        dt.setTime5("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.OT_IN.getId());
                                        dt.setData5(rc3);
                                        break;
                                    }
                                    case 5: {
                                        dt.setTime6("Set Time");
                                        rc3.setTimeRecord("00:00");
                                        rc3.setTimeInOutType(TimeAmPmType.OT_OUT.getId());
                                        dt.setData6(rc3);
                                        break;
                                    }
                                }
                                tdata.add(rc3);
                            }
                        }
                        dt = this.calculateTime(tdata, dt);
                        this.timeData.add(dt);
                    }
                }
            }
        }
        if(totalRenderedWork>0) {
        this.timeData.add(TimeData.builder().late("Total").lateCharge(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalLateCharge, 2))).toString()).morningTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalMorning, 2))).toString()).afternoonTotalTime(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalAfternoon, 2))).toString()).totalOt(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalOvertime, 2))).toString()).totalRenderedTime(new StringBuilder(String.valueOf(Numbers.roundOf(this.totalRenderedWork, 2))).toString()).build());
        this.timeData.add(TimeData.builder().employeeName("").build());
        }
    	}
    }
}
