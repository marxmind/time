package com.marxmind.bean;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

public class SessionBean
{
    public static HttpSession getSession() {
        return (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
    }
    
    public static HttpServletRequest getRequest() {
        return (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
    }
    
    public static String getUserName() {
        final HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        return session.getAttribute("username").toString();
    }
    
    public static String getUserId() {
        final HttpSession session = getSession();
        if (session != null) {
            return (String)session.getAttribute("userid");
        }
        return null;
    }
}
