package com.fpz.tomcat.servlets;

import com.fpz.standard.ServletException;
import com.fpz.standard.http.HttpServlet;
import com.fpz.standard.http.HttpServletRequest;
import com.fpz.standard.http.HttpServletResponse;
import com.fpz.tomcat.HttpServer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DefaultServlet extends HttpServlet {
    //静态文件
    private final String welcomeFile="/index.html";
    //contentType类型对照表
    private final Map<String,String> mime=new HashMap<>();
    //默认contentType
    private final String defaultContentType="text/plain";

    //contentType对照表初始化
    @Override
    public void init() throws ServletException {
        mime.put("html","text/html");
        mime.put("htm","text/htm");
        mime.put("jpg","image/jpeg");

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("there处理静态资源");
        String contextPath=req.getContextPath();
        String servletPath=req.getServletPath();

        if (servletPath.equals("/")){
            servletPath=welcomeFile;
        }

        //响应

        String fileName=String.format("%s\\%s\\%s", HttpServer.WEBAPPS_BASE,contextPath,servletPath);
        //判断文件是否存在
        File file=new File(fileName);

        if (!file.exists()){
            //404
            HttpServer.notFoundServlet.service(req, resp);
            return;
        }

        //文件存在响应内容
        // 设置编码格式
        String contentType=getContentType(servletPath);
        resp.setContentType(contentType);
        //设置响应体
        OutputStream outputStream=resp.getOutputStream();

        try (InputStream inputStream=new FileInputStream(file)){
            byte[] buffer=new byte[1024];
            int len=-1;
            while ((len=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);
            }
            outputStream.flush();
        }




    }

    public String getContentType(String servletPath){
        String contentType=defaultContentType;
        int j=servletPath.indexOf(".");
        if (j!=-1){
            //文件后缀名
            String extendName=servletPath.substring(j+1);
            contentType=mime.getOrDefault(extendName,defaultContentType);
        }
        return contentType;
    }
}
