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
            //1.从Socket中解析得到请求对象
            Request request=parser.parse(socket.getInputStream());
            //2.实例化一个响应对象
            Response response=new Response();
            //3.根据request.getContextPath()判断是交给哪个Context进行处理
            Context handleContext=HttpServer.defaultContext;
            for (Context context:HttpServer.contextList){
                if (context.getName().equals(request.getContextPath())){
                    handleContext=context;
                    break;
                }
            }
            //4.根据request.getServletPath()判断判断是交给Context的哪个servlet对象处理
            Servlet servlet=handleContext.get(request.getServletPath());
            if (servlet==null){
                servlet=HttpServer.defaultServlet;
            }
            //5. 调用servlet.service(req,resp)，进行业务处理
            servlet.service(request,response);
            //6. 根据response对象中的数据，发送HTTP响应
            sendResponse(socket.getOutputStream(), request, response);
            socket.close();
        } catch (IOException | ClassNotFoundException | ServletException e) {
            e.printStackTrace();
        }
    }
    private void sendResponse(OutputStream outputStream, Request request, Response response) throws IOException {
        //保存session 保存文件
        if (request.session!=null){
            Cookie cookie=new Cookie("session-id",request.session.sessionId);
            //添加到响应体
            response.addCookie(cookie);
            //保存到本地文件-持久化
            request.session.saveSessionData();
        }
        Writer writer=new OutputStreamWriter(outputStream,"UTF-8");
        PrintWriter printWriter=new PrintWriter(writer);
        for (Cookie cookie:response.cookieList){
            response.setHeader("Set-Cookie",String.format("%s=%s",cookie.getName(),cookie.getValue()));
        }

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
