package com.marxmind.bean;

import com.marxmind.utils.PopupMessage;
import com.marxmind.enm.PassSlipStatus;
import java.util.ArrayList;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

import com.marxmind.utils.ReadConfig;
import com.marxmind.utils.AppConf;
import com.marxmind.controller.PassSlip;
import java.util.List;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("slipa")
@ViewScoped
public class PassSlipApproverBean implements Serializable
{
    private static final long serialVersionUID = 14345465466L;
    @Getter @Setter private List<PassSlip> slips;
    @Getter @Setter private String searchParam;
    
    @PostConstruct
    public void init() {
        final String val = ReadConfig.value(AppConf.SERVER_LOCAL);
        final HttpSession session = SessionBean.getSession();
        session.setAttribute("server-local", (Object)val);
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
    
    public void commandAction(final PassSlip slip, final String type) {
        if ("APPROVED".equalsIgnoreCase(type)) {
            slip.setStatus(PassSlipStatus.APPROVED.getId());
            PopupMessage.addMessage(1, "Success", "Successfully approved.");
        }
        else if ("DENIED".equalsIgnoreCase(type)) {
            slip.setStatus(PassSlipStatus.DENIED.getId());
            PopupMessage.addMessage(1, "Success", "Successfully Denied.");
        }
        slip.setApprovedBy("Henry E. Magbanua");
        slip.setApprovedPos("Municipal Assistant Treasurer");
        slip.save();
        this.search();
    }
}
