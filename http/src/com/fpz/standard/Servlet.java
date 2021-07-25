package com.fpz.standard;

import java.io.IOException;

public interface Servlet {
    //初始化Servlet对象
    void init() throws ServletException;

    void service(ServletRequest req, ServletResponse resp) throws ServletException, IOException;

    void destroy();
}
