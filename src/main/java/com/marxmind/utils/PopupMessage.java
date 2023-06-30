package com.marxmind.utils;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;

public class PopupMessage
{
    public static void addMessage(final int severityLevel, final String summary, final String detail) {
        FacesMessage message = null;
        switch (severityLevel) {
            case 1: {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
                FacesContext.getCurrentInstance().addMessage((String)null, message);
                break;
            }
            case 2: {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail);
                FacesContext.getCurrentInstance().addMessage((String)null, message);
                break;
            }
            case 3: {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
                FacesContext.getCurrentInstance().addMessage((String)null, message);
                break;
            }
            case 4: {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, detail);
                FacesContext.getCurrentInstance().addMessage((String)null, message);
                break;
            }
        }
    }
}