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
        String method=scanner.next().toUpperCase();
        //请求路径 path:/dictionary/translate?name=zpf
        String path=scanner.next();
        //解析请求参数queryString:就是?之后的内容
        Map<String,String> paramters=new HashMap<>();
        String requestURI=path;
        int i=path.indexOf("?");
        if (i!=-1){
            requestURI=path.substring(0,i);
            String queryString=path.substring(i+1);
            for (String paramterskv:queryString.split("&")){
                String[] kvPart=paramterskv.split("=");
                //要进行解码：浏览器只认识ASCII码，不认识中文
                String name= URLDecoder.decode(kvPart[0].trim(),"UTF-8");
                String value=URLDecoder.decode(kvPart[1].trim(),"UTF-8");
                paramters.put(name,value);
            }
        }
        //解析ContextPath和ServletPath
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
        while (scanner.hasNextLine()&&!(headLine=scanner.nextLine().trim()).isEmpty()){
            //对每一行请求头进行拆分
            String[] part=headLine.split(":");
            String name=part[0].trim().toLowerCase();
            String value=part[1].trim();
            headers.put(name,value);
            //解析cookie 
            if (name.equals("cookie")){
                String[] kvs=value.split(";");
                for (String kv:kvs){
                    if (kv.trim().isEmpty()){
                        continue;
                    }
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
