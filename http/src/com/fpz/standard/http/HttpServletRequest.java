package com.fpz.standard.http;

import com.fpz.standard.ServletRequest;
import com.fpz.tomcat.http.HttpSessionImpl;

public interface HttpServletRequest extends ServletRequest {
    Cookie[] getCookies();

    String getHeader(String name);

    String getMethod();

    String getContextPath();
    String getServletPath();
    String getRequestURI();

    HttpSession getSession();

    //void saveToRequest(HttpSession session);
}
