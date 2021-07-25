package com.fpz.tomcat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigReader {
    public Config read(String name) throws IOException {
        Map<String, String> servletNameToServletClassNameMap = new HashMap<>();
        LinkedHashMap<String, String> urlToServletNameMap = new LinkedHashMap<>();

        //获取web.conf文件的路径
        String fileName=String.format("%s/%s/WEB-INF/web.conf",HttpServer.WEBAPPS_BASE,name);

        //进行文本内容的读取
        //有限自动机起始状态 三个状态：start servlets servlet-mappings
        String stage="start";
        try(InputStream is=new FileInputStream(fileName)){
            Scanner scanner=new Scanner(is,"UTF-8");
            while (scanner.hasNextLine()){
                //要过滤掉空格，自己的web.conf中内容前面有空格
                String line=scanner.nextLine().trim();
                //空行或注释跳过
                if (line.isEmpty()||line.startsWith("#")){
                    continue;
                }

                //switch切换状态
                switch (stage){
                    case "start":
                        if (line.equals("servlets:")){
                            stage="servlets";
                        }
                        break;
                    case "servlets":
                        if (line.equals("servlet-mappings:")){
                            stage="mappings";
                        }else{
                            //解析 servletName->servletClassName
                            String[] parts=line.split("=");
                            String servletName=parts[0].trim();

                            String servletClassName=parts[1].trim();

                            servletNameToServletClassNameMap.put(servletName,servletClassName);
                        }
                        break;
                    case "mappings":
                        //解析 url->servletName
                        String[] parts=line.split("=");
                        String url=parts[0].trim();
                        String servletName=parts[1].trim();
                        urlToServletNameMap.put(url,servletName);
                        break;
                }

            }
        }
        return new Config(servletNameToServletClassNameMap,urlToServletNameMap);
    }

}
