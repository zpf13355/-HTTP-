package com.fpz.tomcat;

import com.fpz.standard.Servlet;
import com.fpz.standard.ServletException;
import com.fpz.tomcat.servlets.DefaultServlet;
import com.fpz.tomcat.servlets.NotFoundServlet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    public static DefaultServlet defaultServlet=new DefaultServlet();
    public static NotFoundServlet notFoundServlet=new NotFoundServlet();
    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ServletException {
//        //创建线程池-不是最优写法
//        ExecutorService executorService= Executors.newFixedThreadPool(10);
//
//        ServerSocket serverSocket=new ServerSocket(8080);

        //1.找到所有的servlet对象进行初始化
        /*for (Servlet servlet:servlets){
            servlet.init();
        }*/
        initServer();
        //验证初始化成功
//        for (Context context:contextList){
//            for (Servlet servlet:context.servletList){
//                System.out.println(servlet);
//            }
//        }

        //2.每次循环处理一个请求
        startServer();
//        while (true){
//            //accept()函数是从已经建立好连接的队列中获取一个连接进行数据传输-三次握手
//            Socket socket=serverSocket.accept();
//            //实现Runnable接口-多态(向上转型)
//            Runnable task=new RequestResponseTask(socket);
//            //线程池提交任务
//            executorService.execute(task);
//
//        }
        //3.找到所有的servlet对象进行销毁

        /*for (Servlet servlet:servlets){
            servlet.destroy();
        }*/
        destroyServer();
    }



    private static void initServer() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, ServletException {
        //1. 扫描所有的Context
        scanContexts();
        //2. 解析每个Context下的web.conf配置文件
        parseContextConf();
        //3.进行类的加载(利用反射进行类的加载)
        loadServletClasses();
        //4. 进行实例化Servlet对象过程(new是一种，反射也可以进行实例化)
        instantiatServletObjects();
        //5. 进行Servlet对象的初始化
        initializeServletObjects();
    }

    private static void startServer() throws IOException {
        //创建线程池-不是最优写法
        ExecutorService executorService= Executors.newFixedThreadPool(10);

        ServerSocket serverSocket=new ServerSocket(8080);
        while (true){
            //accept()函数是从已经建立好连接的队列中获取一个连接进行数据传输-三次握手
            Socket socket=serverSocket.accept();
            //实现Runnable接口-多态(向上转型)
            Runnable task=new RequestResponseTask(socket);
            //线程池提交任务
            executorService.execute(task);

        }
    }

    private static void destroyServer() {
        for (Context context:contextList){
            context.destroyServlets();
        }
    }

    //定义一个基准目录用于扫描
    public static String WEBAPPS_BASE="E:\\JavaNotes\\http\\webapps";
    //定义一个存放Context的列表
    public static List<Context> contextList=new ArrayList<>();

    //ConfigReader对象
    public static final ConfigReader configReader=new ConfigReader();

    public static final DefaultContext defaultContext=new DefaultContext(configReader);

    private static void scanContexts() {
        System.out.println("第一步：扫描出所有个 contexts");
        File webappsRoot=new File(WEBAPPS_BASE);
        //获取基准目录下的文件  结果[dictionary]
        File[] files=webappsRoot.listFiles();

        for (File file:files){
            //System.out.println(file.getName());
            if(file==null){
                throw new RuntimeException();
            }
            //不是目录就不是Context
            if (!file.isDirectory()){
                continue;
            }

            //是目录获取目录名
            String contextName=file.getName();
            System.out.println(contextName);
            //封装为一个Context对象管理对应Context信息-面向对象思想
            Context context=new Context(configReader,contextName);
            contextList.add(context);

        }

    }

    private static void parseContextConf() throws IOException {
        System.out.println("第二步：解析每个 Context 下的配置文件");
        for (Context context:contextList){
            context.readConfig();
        }



    }
    private static void loadServletClasses() throws ClassNotFoundException {
        System.out.println("第三步：加载每个 Servlet 类");
        for (Context context:contextList){
            context.loadServletClass();
        }
    }

    private static void instantiatServletObjects() throws InstantiationException, IllegalAccessException {
        System.out.println("第四步：实例化每个 servlet 对象");
        for (Context context:contextList){
            context.instantiateServletObjects();
        }
    }
    private static void initializeServletObjects() throws ServletException {
        System.out.println("第五步：执行每个 servlet 对象的初始化");
        for (Context context:contextList){
            context.initializeServletObjects();
        }
        defaultServlet.init();
        notFoundServlet.init();
    }





}
