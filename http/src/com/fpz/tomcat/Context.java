package com.fpz.tomcat;
import com.fpz.standard.Servlet;
import com.fpz.standard.ServletException;
import java.io.IOException;
import java.util.*;
public class Context {
    private String name;
    private Config config;
    private ConfigReader reader;
    public Context(ConfigReader reader,String name){
        this.reader=reader;
        this.name=name;
    }
    public String getName() {
        return name;
    }
    public void readConfig() throws IOException {
        this.config=reader.read(name);
    }
    private final ClassLoader webappClassLoader=Context.class.getClassLoader();
    private List<Class<?>> servletClassList=new ArrayList<>();
    public void loadServletClass() throws ClassNotFoundException {
        Set<String> servletClassNames=new HashSet<>(config.servletNameToServletClassNameMap.values());
        for (String servletClassName:servletClassNames){
            Class<?> servletClass=webappClassLoader.loadClass(servletClassName);
            servletClassList.add(servletClass);
        }
        for (Class<?> servletClass:servletClassList){
            System.out.println(servletClass);
        }
    }
    List<Servlet> servletList=new ArrayList<>();
    public void instantiateServletObjects() throws IllegalAccessException, InstantiationException {
        for (Class<?> servletClass:servletClassList){
            Servlet servlet=(Servlet) servletClass.newInstance();
            servletList.add(servlet);
        }
    }
    public void initializeServletObjects() throws ServletException {
        for (Servlet servlet:servletList){
            servlet.init();
        }
    }
    public void destroyServlets() {
        for (Servlet servlet:servletList){
            servlet.destroy();
        }
    }
    public Servlet get(String servletPath) {
        //根据servletPath获取servletName
        String servletName=config.urlToServletNameMap.get(servletPath);
        String servletClassName=config.servletNameToServletClassNameMap.get(servletName);
        for (Servlet servlet:servletList){
            //获取当前servlet的类名
            String currentServeltClassName=servlet.getClass().getCanonicalName();
            if (currentServeltClassName.equals(servletClassName)){
                return servlet;
            }
        }
        return null;
    }
}
