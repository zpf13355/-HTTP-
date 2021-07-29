package com.fpz.tomcat.http;
import com.fpz.standard.http.Cookie;
import com.fpz.standard.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Response implements HttpServletResponse {
    //状态码
    public int status=200;
    //cookie
    public final List<Cookie> cookieList;
    //响应头
    public final Map<String,String> headers;
    //响应体
    public final ByteArrayOutputStream bodyByteArrayOutputStream;
    public final PrintWriter bodPrintWriter;
    public Response() throws UnsupportedEncodingException {
        cookieList =new ArrayList<>();
        headers=new HashMap<>();
        bodyByteArrayOutputStream=new ByteArrayOutputStream(1024);
        Writer writer=new OutputStreamWriter(bodyByteArrayOutputStream,"UTF-8");
        bodPrintWriter=new PrintWriter(writer);
    }
    //响应头
    //响应体
    @Override
    public void addCookie(Cookie cookie) {
        cookieList.add(cookie);
    }

    @Override
    public void sendError(int sc) {
        //TODO
    }

    @Override
    public void sendRedirect(String location) {
        setStatus(307);
        setHeader("Location",location);
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name,value);
    }

    @Override
    public void setStatus(int sc) {
        status=sc;
    }

    //响应体(字节流)
    @Override
    public OutputStream getOutputStream() throws IOException {
            return bodyByteArrayOutputStream;
    }

    //响应体(字符流)
    @Override
    public PrintWriter getWriter() throws IOException {
        return bodPrintWriter;
    }

    @Override
    public void setContentType(String type) {
        if (type.startsWith("text/")) {
            type = type + "; charset=utf-8";
        }
        setHeader("Content-Type", type);
    }

    @Override
    public String toString() {
        try {
            bodPrintWriter.flush();
            bodyByteArrayOutputStream.flush();
        } catch (IOException exc) {
            exc.printStackTrace(System.out);
        }
        return String.format("Response{%d %s %s}", status, headers, bodyByteArrayOutputStream.toString());
    }
}
