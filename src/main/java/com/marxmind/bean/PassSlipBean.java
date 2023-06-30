package com.marxmind.bean;

import com.marxmind.utils.PopupMessage;
import com.marxmind.enm.PassSlipStatus;
import java.util.Iterator;
import com.marxmind.utils.DateUtils;
import com.marxmind.enm.Department;
import jakarta.faces.model.SelectItem;
import com.marxmind.controller.Employee;
import java.util.ArrayList;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.Data;

import com.marxmind.utils.ReadConfig;
import com.marxmind.utils.AppConf;
import java.util.List;
import com.marxmind.controller.PassSlip;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("slip")
@ViewScoped
@Data
public class PassSlipBean implements Serializable
{
    private static final long serialVersionUID = 543676584644541L;
    private PassSlip passData;
    private int employeeId;
    private List employees;
    private int departmentId;
    private List departments;
    private List<PassSlip> slips;
    private String searchParam;
    
    @PostConstruct
    public void init() {
        final String val = ReadConfig.value(AppConf.SERVER_LOCAL);
        final HttpSession session = SessionBean.getSession();
        session.setAttribute("server-local", (Object)val);
        this.defaultValue();
        this.search();
    }
    
    public void search() {
        this.slips = new ArrayList<PassSlip>();
        String sql = "";
        if (this.getSearchParam() != null) {
            sql = " AND (pl.passdate like '%" + this.getSearchParam() + "%' OR emp.firstname like '%" + this.getSearchParam() + "%' OR emp.middlename like '%" + this.getSearchParam() + "%' OR emp.lastname like '%" + this.getSearchParam() + "%')";
            sql = String.valueOf(sql) + " ORDER BY pl.passdate DESC";
        }
        else {
            sql = " ORDER BY pl.passdate DESC limit 10";
        }
        this.slips = (List<PassSlip>)PassSlip.retrieve(sql, new String[0]);
    }
    
    private void defaultValue() {
        this.employeeId = 0;
        this.employees = new ArrayList();
        for (final Employee e : Employee.retrieve(" ORDER BY lastname", new String[0])) {
            this.employees.add(new SelectItem((Object)e.getId(), e.getFullName()));
        }
        this.departmentId = Department.TREASURER.getId();
        this.departments = new ArrayList();
        Department[] values;
        for (int length = (values = Department.values()).length, i = 0; i < length; ++i) {
            final Department d = values[i];
            this.departments.add(new SelectItem((Object)d.getId(), d.getName()));
        }
        this.passData = PassSlip.builder().tmpDateTrans(DateUtils.getDateToday()).isActive(1).employee(Employee.builder().id(1L).build()).department(Department.TREASURER.getId()).build();
    }
    
    public void clear() {
        this.passData = PassSlip.builder().tmpDateTrans(DateUtils.getDateToday()).isActive(1).employee(Employee.builder().id(1L).build()).department(Department.TREASURER.getId()).build();
    }
    
    public void click(final PassSlip slip) {
        slip.setTmpDateTrans(DateUtils.convertDateString(slip.getDateTrans(), "yyyy-MM-dd"));
        this.setPassData(slip);
    }
    
    public void deleteItem(final PassSlip slip) {
        if (PassSlipStatus.APPROVED.getId() == slip.getStatus()) {
            PopupMessage.addMessage(3, "Warning", "Approved passlip cannot be deleted");
        }
        else if (PassSlipStatus.DENIED.getId() == slip.getStatus()) {
            PopupMessage.addMessage(3, "Warning", "Denied passlip cannot be deleted");
        }
        else {
            slip.delete();
            this.search();
            PopupMessage.addMessage(1, "Success", "Successfully deleted");
        }
    }
    
    public void save() {
        boolean isOk = true;
        final PassSlip slip = this.getPassData();
        if (slip.getEmployee().getId() == 0L) {
            PopupMessage.addMessage(3, "Error", "Please provide employee name");
            isOk = false;
        }
        if (slip.getPermisionFrom().isEmpty()) {
            PopupMessage.addMessage(3, "Error", "Please provide permission from");
            isOk = false;
        }
        if (slip.getPermisionTo().isEmpty()) {
            PopupMessage.addMessage(3, "Error", "Please provide permission to");
            isOk = false;
        }
        if (slip.getReason().isEmpty()) {
            PopupMessage.addMessage(3, "Error", "Please provide reason");
            isOk = false;
        }
        if (slip.getStatus() == PassSlipStatus.APPROVED.getId()) {
            PopupMessage.addMessage(3, "Error", "Approved request cannot be rectify");
            isOk = false;
        }
        if (slip.getStatus() == PassSlipStatus.DENIED.getId()) {
            PopupMessage.addMessage(3, "Error", "Denied request cannot be change");
            isOk = false;
        }
        if (isOk) {
            if (slip.getId() == 0L) {
                slip.setStatus(PassSlipStatus.REQUEST.getId());
            }
            slip.setDateTrans(DateUtils.convertDate(slip.getTmpDateTrans(), "yyyy-MM-dd"));
            slip.save();
            this.search();
            this.clear();
            PopupMessage.addMessage(1, "Success", "Successfully saved.");
        }
    }
	
}
