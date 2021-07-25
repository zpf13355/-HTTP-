package com.fpz.tomcat.http;

import com.fpz.standard.http.Cookie;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

public class HttpRequestParser {

    public Request parse(InputStream inputStream) throws IOException, ClassNotFoundException {
        //字节流封装非字符流
        Scanner scanner=new Scanner(inputStream,"UTF-8");

        //解析请求行  请求方法 空格 请求路径 空格 版本信息
        //请求方法大写
        String method=scanner.next().toUpperCase();
        //请求路径 path:/dictionary/translate?name=zpf
        String path=scanner.next();

        //解析请求参数queryString:就是?之后的内容

        Map<String,String> paramters=new HashMap<>();
        String requestURI=path;
        int i=path.indexOf("?");
        if (i!=-1){
            requestURI=path.substring(0,i);
            //k1=v1&k2=v2&k3=v3
            String queryString=path.substring(i+1);
            //k1=v1
//            String[] paramterskvs=queryString.split("&");
//            for (String paramterskv:paramterskvs){
//                //k1 v1
//                String[] kvPart=paramterskv.split("=");
//                //要进行解码：浏览器只认识ASCII码，不认识中文
//                //k1
//                String name= URLDecoder.decode(kvPart[0].trim(),"UTF-8");
//                //v1
//                String value=URLDecoder.decode(kvPart[1].trim(),"UTF-8");
//
//                paramters.put(name,value);
//
//            }


            for (String paramterskv:queryString.split("&")){
                //k1 v1
                String[] kvPart=paramterskv.split("=");
                //要进行解码：浏览器只认识ASCII码，不认识中文
                //k1
                String name= URLDecoder.decode(kvPart[0].trim(),"UTF-8");
                //v1
                String value=URLDecoder.decode(kvPart[1].trim(),"UTF-8");

                paramters.put(name,value);

            }

        }

        //解析ContextPath和ServletPath

        //找第二个斜杠(/)
        int j=requestURI.indexOf('/',1);
        String contextPath="/";
        String servletPath=requestURI;
        if (j!=-1){
            contextPath=requestURI.substring(1,j);
            servletPath=requestURI.substring(j);
        }


        //版本信息
        String version=scanner.nextLine();

        //解析请求头
        Map<String,String> headers=new HashMap<>();
        List<Cookie> cookieList=new ArrayList<>();
        String headLine;

        //next()不会吸取字符前/后的空格/Tab键，只吸取字符，开始吸取字符（字符前后不算）直到遇到空格/Tab键/回车截止吸取；
        //nextLine()吸取字符前后的空格/Tab键，回车键截止。
        //hasNext()方法会判断接下来是否有非空字符.如果有,则返回true,否则返回false
        //hasNextLine() 方法会根据行匹配模式去判断接下来是否有一行(包括空行),如果有,则返回true,否则返回false

        //请求头以空行结束
        //因为hasNextLine()也会把空行读取所以还要加一个判断去读到的这行是不是为空
        while (scanner.hasNextLine()&&!(headLine=scanner.nextLine().trim()).isEmpty()){
            //对每一行请求头进行拆分
            // name:value
            String[] part=headLine.split(":");
            //name
            String name=part[0].trim().toLowerCase();
            //value
            String value=part[1].trim();
            headers.put(name,value);
            //解析cookie 格式：key1=value1;key2=value2;key3=value3;key4=value4;key5=value5;
            if (name.equals("cookie")){
                //数组保存key=value
                String[] kvs=value.split(";");

                for (String kv:kvs){
                    //可能是空cookie
                    if (kv.trim().isEmpty()){
                        continue;
                    }

                    //解析每一个key=value
                    String[] nv=kv.split("=");
                    String cookieName=nv[0].trim();
                    String cookieValue=nv[1].trim();
                    //封装Cookie对象
                    Cookie cookie=new Cookie(cookieName,cookieValue);

                    cookieList.add(cookie);

                }
            }

        }

        //解析请求体

        return new Request(method,requestURI,contextPath,servletPath,paramters,headers,cookieList);
    }
}
