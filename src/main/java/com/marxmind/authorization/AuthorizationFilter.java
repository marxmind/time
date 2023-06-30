package com.marxmind.authorization;

import java.io.IOException;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.Filter;

@WebFilter(filterName = "AuthFilter", urlPatterns = { "*.xhtml" })
public class AuthorizationFilter implements Filter
{
    public void init(final FilterConfig filterConfig) throws ServletException {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        try {
            final HttpServletRequest reqt = (HttpServletRequest)request;
            final HttpServletResponse resp = (HttpServletResponse)response;
            final HttpSession session = reqt.getSession(false);
            final String reqURI = reqt.getRequestURI();
            if (reqURI.indexOf("/main.xhtml") >= 0 || reqURI.indexOf("/hello.xhtml") >= 0 || reqURI.indexOf("/record.xhtml") >= 0 || reqURI.indexOf("/slip.xhtml") >= 0 || reqURI.indexOf("/dtr.xhtml") >= 0 || reqURI.indexOf("/slip-approver.xhtml") >= 0) {
                chain.doFilter(request, response);
            }
            else {
                resp.sendRedirect(String.valueOf(reqt.getContextPath()) + "/marxmind/main.xhtml");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void destroy() {
    }
}
