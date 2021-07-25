package com.fpz.tomcat;

import com.fpz.standard.Servlet;

public class DefaultContext extends Context {
    public DefaultContext(ConfigReader reader) {
        super(reader,"/");
    }

    @Override
    public Servlet get(String servletPath) {
        return HttpServer.notFoundServlet;
    }
}
