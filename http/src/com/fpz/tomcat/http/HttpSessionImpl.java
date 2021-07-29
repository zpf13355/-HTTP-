package com.fpz.tomcat.http;
import com.fpz.standard.http.HttpSession;
import com.sun.xml.internal.fastinfoset.tools.FI_DOM_Or_XML_DOM_SAX_SAXEvent;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
public class HttpSessionImpl implements HttpSession {
    public Map<String,Object> sessionData;
    public String sessionId;
    public Request request=null;
    //没有从 cookie 中拿到 session-id 时构建对象使用
    public HttpSessionImpl(){
        sessionId= UUID.randomUUID().toString();
        sessionData=new HashMap<>();
    }
    //从 cookie 中拿到 session-id 时构建对象使用
    public HttpSessionImpl(String sessionId) throws IOException, ClassNotFoundException {
        this.sessionId=sessionId;
        sessionData = loadSessionData(sessionId);

    }
    private  static final String SESSION_BASE="E:\\JavaNotes\\http\\sessions";
    private Map<String, Object> loadSessionData(String sessionId) throws IOException, ClassNotFoundException {
        String sessionFileName=String.format("%s\\%s.session",SESSION_BASE,sessionId);
        File file=new File(sessionFileName);

        if (!file.exists()) {
            return new HashMap<>();
        }
        try(InputStream is=new FileInputStream(file)) {
            try(ObjectInputStream objectInputStream=new ObjectInputStream(is)) {
                return (Map<String, Object>) objectInputStream.readObject();
            }
        }

    }
    //保存session数据到session文件
    public void saveSessionData() throws IOException {
        if (sessionData.isEmpty()){
            return;
        }
        String sessionFilename=String.format("%s\\%s.session",SESSION_BASE,sessionId);
        try(OutputStream outputStream=new FileOutputStream(sessionFilename)) {
            try(ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream)) {
                objectOutputStream.writeObject(sessionData);
                objectOutputStream.flush();
            }
        }
    }

    @Override
    public Object getAttribute(String name) {
        return sessionData.get(name);
    }

    @Override
    public void removeAttribute(String name) {
        sessionData.remove(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        sessionData.put(name,value);
    }

    @Override
    public String toString() {
        return "HttpSessionImpl{" +
                "sessionData=" + sessionData +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
