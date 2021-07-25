package com.fpz.tomcat;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Config {
    Map<String, String> servletNameToServletClassNameMap = new HashMap<>();
    LinkedHashMap<String, String> urlToServletNameMap = new LinkedHashMap<>();

    public Config(Map<String, String> servletNameToServletClassNameMap,LinkedHashMap<String, String> urlToServletNameMap){
        this.servletNameToServletClassNameMap=servletNameToServletClassNameMap;
        this.urlToServletNameMap=urlToServletNameMap;
    }
}
