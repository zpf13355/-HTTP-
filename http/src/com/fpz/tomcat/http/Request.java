package com.fpz.tomcat.http;
import com.fpz.standard.http.Cookie;
import com.fpz.standard.http.HttpServletRequest;
import com.fpz.standard.http.HttpSession;
import javax.print.DocFlavor;
import java.io.IOException;
import java.util.List;
import java.util.Map;
public class Request implements HttpServletRequest {
    private final String method;
    private final String requestURI;
    private final String contextPath;
    private final String servletPath;
    public final Map<String,String> paramters;
    public final Map<String,String> headers;
    public final List<Cookie> cookieList;
    public HttpSessionImpl session=null;
    public Request(String method, String requestURI, String contextPath, String servletPath, Map<String, String> paramters, Map<String, String> headers, List<Cookie> cookieList) throws IOException, ClassNotFoundException {
        this.method=method;
        this.requestURI=requestURI;
        this.contextPath=contextPath;
        this.servletPath=servletPath;
        this.paramters=paramters;
        this.headers=headers;
        this.cookieList=cookieList;
        for (Cookie cookie:cookieList){
            if (cookie.getName().equals("session-id")){
                String sessionid=cookie.getValue();
                session=new HttpSessionImpl(sessionid);
                break;
            }
        }
    }

    @Override
    public Cookie[] getCookies() {

        return cookieList.toArray(new Cookie[0]);
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public HttpSession getSession() {
        if (session!=null){
            return session;
        }

        session=new HttpSessionImpl();
        return session;
    }

    @Override
    public String getParameter(String name) {
        return paramters.get(name);
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", requestURI='" + requestURI + '\'' +
                ", contextPath='" + contextPath + '\'' +
                ", servletPath='" + servletPath + '\'' +
                ", paramters=" + paramters +
                ", headers=" + headers +
                ", cookieList=" + cookieList+
                ", session=" + session +
                '}';
    }
}
