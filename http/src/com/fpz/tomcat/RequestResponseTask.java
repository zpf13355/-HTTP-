package com.fpz.tomcat;

import com.fpz.standard.Servlet;
import com.fpz.standard.ServletException;
import com.fpz.standard.http.Cookie;
import com.fpz.tomcat.http.HttpRequestParser;
import com.fpz.tomcat.http.Request;
import com.fpz.tomcat.http.Response;
import com.fpz.tomcat.servlets.DefaultServlet;

import java.io.*;
import java.net.Socket;
import java.util.Map;

public class RequestResponseTask implements Runnable {
    private static final HttpRequestParser parser=new HttpRequestParser();
    public Socket socket;
    public RequestResponseTask(Socket socket){
        this.socket=socket;
    }
    @Override
    public void run() {


        try {
            //一个请求响应周期如下：
            //1.从Socket中解析得到请求对象
            Request request=parser.parse(socket.getInputStream());
            System.out.println(request);
            //空响应验证
//            OutputStream os=socket.getOutputStream();
//            Writer writer=new OutputStreamWriter(os,"UTF-8");
//            PrintWriter printWriter=new PrintWriter(writer);
//            printWriter.printf("HTTP/1.0 200 OK\r\n");
//            printWriter.flush();

            //2.实例化一个响应对象
            Response response=new Response();
            //System.out.println(response);
            //3.根据request.getContextPath()判断是交给哪个Context进行处理
            //Context handleContext=null;
            Context handleContext=HttpServer.defaultContext;
            for (Context context:HttpServer.contextList){

                if (context.getName().equals(request.getContextPath())){
                    handleContext=context;

                    break;
                }
            }

            //4.根据request.getServletPath()判断判断是交给Context的哪个servlet对象处理
            Servlet servlet=handleContext.get(request.getServletPath());
            //静态资源
            if (servlet==null){
                servlet=HttpServer.defaultServlet;
            }
//            if (handleContext!=null){
//                servlet=handleContext.get(request.getServletPath());
//            }


            //5. 调用servlet.service(req,resp)，进行业务处理
            servlet.service(request,response);
            System.out.println(request.session);
            System.out.println(request);
            //System.out.println(response);

            //6. 根据response对象中的数据，发送HTTP响应
            sendResponse(socket.getOutputStream(), request, response);
            //System.out.println(response);

            socket.close();
        } catch (IOException | ClassNotFoundException | ServletException e) {
            e.printStackTrace();
        }
    }
    //不带cookie和session的响应
    /*private void sendResponse(OutputStream outputStream, Request request, Response response) throws IOException {
        Writer writer=new OutputStreamWriter(outputStream,"UTF-8");
        PrintWriter printWriter=new PrintWriter(writer);

        //响应行
        printWriter.printf("HTTP/1.0 200 OK\r\n");

        //响应头
        for (Map.Entry<String,String> entry:response.headers.entrySet()){
            String name=entry.getKey();
            String value=entry.getValue();

            printWriter.printf("%s:%s\r\n",name,value);
        }

        //空行
        printWriter.printf("\r\n");

        //响应体
        response.bodPrintWriter.flush();
        response.bodyByteArrayOutputStream.flush();
        printWriter.flush();

        byte[] bytes = response.bodyByteArrayOutputStream.toByteArray();
        outputStream.write(bytes);
        outputStream.flush();


    }*/

    //带cookie和session的响应
    private void sendResponse(OutputStream outputStream, Request request, Response response) throws IOException {

        System.out.println(request.session);
        //保存session 保存文件
        if (request.session!=null){
            Cookie cookie=new Cookie("session-id",request.session.sessionId);
            //添加到响应体
            //System.out.println(cookie.toString());
            response.addCookie(cookie);
            System.out.println(response);

            //保存到本地文件-持久化
            //System.out.println(response.toString());
            request.session.saveSessionData();
        }

        Writer writer=new OutputStreamWriter(outputStream,"UTF-8");
        PrintWriter printWriter=new PrintWriter(writer);
        //响应行


        //响应头
        //种cookie
        for (Cookie cookie:response.cookieList){
            System.out.println(cookie.getName()+":"+cookie.getValue());
            response.setHeader("Set-Cookie",String.format("%s=%s",cookie.getName(),cookie.getValue()));
        }

        //发送响应
        //响应行
        printWriter.printf("HTTP/1.0 %d\r\n",response.status);
        //响应头
        for (Map.Entry<String,String> entry:response.headers.entrySet()){
            String name=entry.getKey();
            String value=entry.getValue();

            printWriter.printf("%s: %s\r\n",name,value);
        }

        //空行
        printWriter.printf("\r\n");

        //响应体
        //确保数据刷新在缓冲区
        response.bodPrintWriter.flush();
        response.bodyByteArrayOutputStream.flush();
        printWriter.flush();

        //响应体发送出去
        byte[] bytes = response.bodyByteArrayOutputStream.toByteArray();
        outputStream.write(bytes);
        outputStream.flush();

    }
}
