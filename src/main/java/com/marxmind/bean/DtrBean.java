package com.marxmind.bean;
//http://www.javadecompilers.com/
import java.lang.invoke.SerializedLambda;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.UUID;
import com.marxmind.utils.PopupMessage;
import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import java.io.File;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import java.util.Collection;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import com.marxmind.utils.Numbers;
import java.util.HashMap;
import com.marxmind.utils.TimeUtils;
import com.marxmind.controller.DTR;
import com.marxmind.enm.TimeAmPmType;
import com.marxmind.enm.TimeType;
import com.marxmind.controller.TimeRecord;
import java.util.Iterator;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.GregorianCalendar;
import jakarta.faces.model.SelectItem;
import com.marxmind.utils.DateUtils;
import java.util.ArrayList;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

import com.marxmind.controller.AppSetting;
import com.marxmind.utils.ReadConfig;
import com.marxmind.utils.AppConf;
import org.primefaces.model.StreamedContent;
import com.marxmind.controller.TimeData;
import com.marxmind.controller.Employee;
import java.util.Map;
import java.util.List;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("dtr")
@SessionScoped
public class DtrBean implements Serializable
{
    private static final long serialVersionUID = 1434556856564565L;
    @Getter @Setter private List employees;
    @Getter @Setter private int employeeId;
    @Getter @Setter private String adminPerson;
    @Getter @Setter private List years;
    @Getter @Setter private int year;
    @Getter @Setter private List months;
    @Getter @Setter private int monthId;
    @Getter @Setter private List selectedHoliday;
    @Getter @Setter private List holidays;
    @Getter @Setter private List selectedBz;
    @Getter @Setter private List officialBzs;
    @Getter @Setter private double timeInAM;
    @Getter @Setter private double timeOutAM;
    @Getter @Setter private double timeInPM;
    @Getter @Setter private double timeOutPM;
    @Getter @Setter private double lateCharge;
    @Getter @Setter private double totalLateCharge;
    @Getter @Setter private double totalMorning;
    @Getter @Setter private double totalAfternoon;
    @Getter @Setter private double totalOvertime;
    @Getter @Setter private double totalRenderedWork;
    @Getter @Setter private Map<Long, Employee> employeeData;
    @Getter @Setter private List<TimeData> timeData;
    @Getter @Setter private String firstWorkingDay;
    @Getter @Setter private String lastWorkingDay;
    private StreamedContent tempPdfFile;
    
    public DtrBean() {
        this.adminPerson = "FERDINAND L. LOPEZ";
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
        this.defaultValue();
    }
    
    public void defaultValue() {
        this.years = new ArrayList();
        final int yr = DateUtils.getCurrentYear();
        this.year = yr;
        for (int y = 2023; y <= yr; ++y) {
            this.years.add(new SelectItem((Object)y, new StringBuilder(String.valueOf(y)).toString()));
        }
        this.months = new ArrayList();
        for (int month = 1; month <= 12; ++month) {
            this.months.add(new SelectItem((Object)month, DateUtils.getMonthName(month)));
        }
        this.holidays = new ArrayList();
        final Calendar cal = new GregorianCalendar(this.getYear(), this.getMonthId() - 1, 1);
        do {
            final int day = cal.get(7);
            if (day != 7 && day != 1) {
                this.holidays.add(new SelectItem((Object)cal.get(5), new StringBuilder(String.valueOf(cal.get(5))).toString()));
            }
            cal.add(6, 1);
        } while (cal.get(2) == this.getMonthId() - 1);
        this.employeeData = new LinkedHashMap<Long, Employee>();
        (this.employees = new ArrayList()).add(new SelectItem((Object)0, "Please select employee"));
        for (final Employee e : Employee.retrieve(" ORDER BY fullname", new String[0])) {
            this.employees.add(new SelectItem((Object)e.getId(), e.getFullName()));
            this.employeeData.put(e.getId(), e);
        }
    }
    
    public void loadDates() {
        this.holidays = new ArrayList();
        Calendar cal = new GregorianCalendar(this.getYear(), this.getMonthId() - 1, 1);
        do {
            final int day = cal.get(7);
            if (day != 7 && day != 1) {
                this.holidays.add(new SelectItem((Object)cal.get(5), new StringBuilder(String.valueOf(cal.get(5))).toString()));
            }
            cal.add(6, 1);
        } while (cal.get(2) == this.getMonthId() - 1);
        this.officialBzs = new ArrayList();
        cal = new GregorianCalendar(this.getYear(), this.getMonthId() - 1, 1);
        do {
            this.officialBzs.add(new SelectItem((Object)cal.get(5), new StringBuilder(String.valueOf(cal.get(5))).toString()));
            cal.add(6, 1);
        } while (cal.get(2) == this.getMonthId() - 1);
    }
    
    public void showDtr() {
        this.totalLateCharge = 0.0;
        this.totalMorning = 0.0;
        this.totalAfternoon = 0.0;
        this.totalOvertime = 0.0;
        this.totalRenderedWork = 0.0;
        String sql = " AND emp.eid=" + this.getEmployeeId();
        sql = String.valueOf(sql) + " AND tm.monthrec=" + this.getMonthId();
        sql = String.valueOf(sql) + " AND tm.yearrec=" + this.getYear();
        sql = String.valueOf(sql) + " ORDER BY tm.tid";
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
        if (nameData != null && nameData.size() > 0) {
            final Map<Integer, TimeData> workData = new LinkedHashMap<Integer, TimeData>();
            this.timeData = new ArrayList<TimeData>();
            for (final long id2 : nameData.keySet()) {
                for (final int year : nameData.get(id2).keySet()) {
                    for (final int month2 : nameData.get(id2).get(year).keySet()) {
                        for (final int day2 : nameData.get(id2).get(year).get(month2).keySet()) {
                            TimeData dt = new TimeData();
                            final List<TimeRecord> tdata = new ArrayList<TimeRecord>();
                            TimeRecord rcTmp = null;
                            final List<TimeRecord> dataVals = nameData.get(id2).get(year).get(month2).get(day2);
                            for (final TimeRecord rc2 : dataVals) {
                                switch (rc2.getTimeInOutType()) {
                                    case 0: {
                                        dt.setTime1(rc2.getTimeRecord());
                                        dt.setDate(String.valueOf(month2) + "/" + day2 + "/" + year);
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
                                            dt.setTime2("??");
                                            rc3.setTimeRecord("00:00");
                                            rc3.setTimeInOutType(TimeAmPmType.AM_OUT.getId());
                                            dt.setData2(rc3);
                                            break;
                                        }
                                        case 2: {
                                            dt.setTime3("??");
                                            rc3.setTimeRecord("00:00");
                                            rc3.setTimeInOutType(TimeAmPmType.PM_IN.getId());
                                            dt.setData3(rc3);
                                            break;
                                        }
                                        case 3: {
                                            dt.setTime4("??");
                                            rc3.setTimeRecord("00:00");
                                            rc3.setTimeInOutType(TimeAmPmType.PM_OUT.getId());
                                            dt.setData4(rc3);
                                            break;
                                        }
                                        case 4: {
                                            dt.setTime5("??");
                                            rc3.setTimeRecord("00:00");
                                            rc3.setTimeInOutType(TimeAmPmType.OT_IN.getId());
                                            dt.setData5(rc3);
                                            break;
                                        }
                                        case 5: {
                                            dt.setTime6("??");
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
                            workData.put(day2, dt);
                        }
                    }
                }
            }
            int size2 = 0;
            if (this.getSelectedHoliday() != null && this.getSelectedHoliday().size() > 0) {
                size2 = this.getSelectedHoliday().size();
            }
            final int[] holidays = new int[size2];
            int j = 0;
            if (this.getSelectedHoliday() != null && this.getSelectedHoliday().size() > 0) {
                for (final Object obj : this.getSelectedHoliday()) {
                    final int hol = Integer.valueOf(obj.toString());
                    holidays[j++] = hol;
                }
            }
            int obSize = 0;
            if (this.getSelectedBz() != null && this.getSelectedBz().size() > 0) {
                obSize = this.getSelectedBz().size();
            }
            final int[] obz = new int[obSize];
            int x = 0;
            if (this.getSelectedBz() != null && this.getSelectedBz().size() > 0) {
                for (final Object obj2 : this.getSelectedBz()) {
                    final int ob = Integer.valueOf(obj2.toString());
                    obz[x++] = ob;
                }
            }
            final List<DTR> dtrs = new ArrayList<DTR>();
            final int mth = this.getMonthId() - 1;
            final Calendar cal = new GregorianCalendar(this.year, mth, 1);
            boolean isfirstWorkingDaySet = false;
            do {
                DTR dtr = new DTR();
                final int day3 = cal.get(7);
                if (day3 == 7 || day3 == 1) {
                    boolean isOb = false;
                    int[] array;
                    for (int length = (array = obz).length, k = 0; k < length; ++k) {
                        final int o = array[k];
                        if (o == cal.get(5)) {
                            isOb = true;
                        }
                    }
                    if (isOb) {
                        dtr.setF1(new StringBuilder(String.valueOf(cal.get(5))).toString());
                        dtr.setF2("OB");
                        dtr.setF3("OB");
                        dtr.setF4("OB");
                        dtr.setF5("OB");
                        dtr.setF6(new StringBuilder(String.valueOf(cal.get(5))).toString());
                        dtr.setF7("OB");
                        dtr.setF8("OB");
                        dtr.setF9("OB");
                        dtr.setF10("OB");
                    }
                    else {
                        dtr.setF1(new StringBuilder(String.valueOf(cal.get(5))).toString());
                        dtr.setF2("S");
                        dtr.setF3("S");
                        dtr.setF4("S");
                        dtr.setF5("S");
                        dtr.setF6(new StringBuilder(String.valueOf(cal.get(5))).toString());
                        dtr.setF7("S");
                        dtr.setF8("S");
                        dtr.setF9("S");
                        dtr.setF10("S");
                    }
                }
                else {
                    boolean isHoliday = false;
                    int[] array2;
                    for (int length2 = (array2 = holidays).length, l = 0; l < length2; ++l) {
                        final int hol2 = array2[l];
                        if (hol2 == cal.get(5)) {
                            isHoliday = true;
                        }
                    }
                    if (isHoliday) {
                        dtr.setF1(new StringBuilder(String.valueOf(cal.get(5))).toString());
                        dtr.setF2("***");
                        dtr.setF3("HOLI");
                        dtr.setF4("DAY");
                        dtr.setF5("***");
                        dtr.setF6(new StringBuilder(String.valueOf(cal.get(5))).toString());
                        dtr.setF7("***");
                        dtr.setF8("HOLI");
                        dtr.setF9("DAY");
                        dtr.setF10("***");
                    }
                    else {
                        boolean isOb2 = false;
                        int[] array3;
                        for (int length3 = (array3 = obz).length, n = 0; n < length3; ++n) {
                            final int o2 = array3[n];
                            if (o2 == cal.get(5)) {
                                isOb2 = true;
                            }
                        }
                        if (!isfirstWorkingDaySet) {
                            this.setFirstWorkingDay(new StringBuilder(String.valueOf(cal.get(5))).toString());
                            isfirstWorkingDaySet = true;
                        }
                        if (isOb2) {
                            dtr.setF1(new StringBuilder(String.valueOf(cal.get(5))).toString());
                            dtr.setF2("OB");
                            dtr.setF3("OB");
                            dtr.setF4("OB");
                            dtr.setF5("OB");
                            dtr.setF6(new StringBuilder(String.valueOf(cal.get(5))).toString());
                            dtr.setF7("OB");
                            dtr.setF8("OB");
                            dtr.setF9("OB");
                            dtr.setF10("OB");
                        }
                        else {
                            final int dy = cal.get(5);
                            if (workData != null && workData.size() > 0 && workData.containsKey(dy)) {
                                final TimeData d = workData.get(dy);
                                final String inPm = TimeUtils.convertTime(d.getTime3()).replace(" PM", "");
                                final String outPm = TimeUtils.convertTime(d.getTime4()).replace(" PM", "");
                                final String hh = d.getTotalRenderedTime();
                                dtr = DTR.builder().f1(new StringBuilder(String.valueOf(dy)).toString()).f2(d.getTime1()).f3(d.getTime2()).f4(inPm).f5(outPm).f13(hh).f14("").f6(new StringBuilder(String.valueOf(dy)).toString()).f7(d.getTime1()).f8(d.getTime2()).f9(inPm).f10(outPm).f11(hh).f12("").build();
                            }
                            else {
                                dtr = DTR.builder().f1(new StringBuilder(String.valueOf(dy)).toString()).f2("***").f3("A B S").f4("E N T").f5("***").f13("").f14("").f6(new StringBuilder(String.valueOf(dy)).toString()).f7("***").f8("A B S").f9("E N T").f10("***").f11("").f12("").build();
                            }
                        }
                        this.setLastWorkingDay(new StringBuilder(String.valueOf(cal.get(5))).toString());
                    }
                }
                dtrs.add(dtr);
                cal.add(6, 1);
            } while (cal.get(2) == mth);
            final String name = this.timeData.get(0).getEmployeeName();
            final String startwork = this.getFirstWorkingDay();
            final String endWork = this.getLastWorkingDay();
            final String path = ReadConfig.value(AppConf.REPORT_FOLDER);
            final String REPORT_NAME = "time-dtr";
            String jrxmlFile = "time-dtr";
            final HashMap param = new HashMap();
            param.put("PARAM_MONTH", String.valueOf(DateUtils.getMonthName(this.getMonthId())) + " " + startwork + "-" + endWork + ", " + this.getYear());
            param.put("PARAM_EMPLOYEE", name);
            param.put("PARAM_ADMIN", (this.getAdminPerson() == null) ? "" : this.getAdminPerson().toUpperCase());
            param.put("PARAM_TOTAL", new StringBuilder(String.valueOf(Numbers.roundOf(this.totalRenderedWork, 2))).toString());
            try {
                JasperCompileManager.compileReportToFile(String.valueOf(path) + REPORT_NAME + ".jrxml", String.valueOf(path) + REPORT_NAME + ".jasper");
                jrxmlFile = String.valueOf(path) + REPORT_NAME + ".jasper";
                final JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource((Collection)dtrs);
                final String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, (Map)param, (JRDataSource)beanColl);
                JasperExportManager.exportReportToPdfFile(jrprint, String.valueOf(path) + REPORT_NAME + ".pdf");
                final String pdfName = String.valueOf(REPORT_NAME) + ".pdf";
                final File pdfFile = new File(String.valueOf(path) + REPORT_NAME + ".pdf");
                this.tempPdfFile = (StreamedContent)DefaultStreamedContent.builder().contentType("application/pdf").name(String.valueOf(REPORT_NAME) + ".pdf").stream(() -> {
                    try {
                        return new FileInputStream(pdfFile);
                    }
                    catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return null;
                    }
                }).build();
                System.out.println("path>> " + path + " REPORT_NAME " + REPORT_NAME);
                System.out.println("DTR " + pdfName);
                final PrimeFaces pm = PrimeFaces.current();
                pm.executeScript("showPdf();hideButton();");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            final PrimeFaces pf = PrimeFaces.current();
            pf.executeScript("showPdf()");
        }
        else {
            PopupMessage.addMessage(2, "No Data", "There is no data to retrieve...");
        }
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
    
    public String generateRandomIdForNotCaching() {
        return UUID.randomUUID().toString();
    }
    
    public void setTempPdfFile(final StreamedContent tempPdfFile) {
        this.tempPdfFile = tempPdfFile;
    }
    
    public StreamedContent getTempPdfFile() throws IOException {
        if (this.tempPdfFile == null) {
            final String pdfName = "time-dtr.pdf";
            final String pdfFolfer = ReadConfig.value(AppConf.REPORT_FOLDER);
            System.out.println("pdf file >>>> " + pdfFolfer);
            return (StreamedContent)DefaultStreamedContent.builder().contentType("application/pdf").name(pdfName).stream(() -> this.getClass().getResourceAsStream(String.valueOf(pdfFolfer) + pdfName)).build();
        }
        return this.tempPdfFile;
    }
	
}
