package com.mukesh.apigateway.Configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.*;

public class CustomServletRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String,String> customheaders= new HashMap<>();
    public CustomServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    public void addHeader(String name,String value){
        customheaders.put(name.toLowerCase(Locale.ROOT),value);
    }
    @Override
    public String getHeader(String name){
        String value=customheaders.get(name.toLowerCase(Locale.ROOT));
        if(value!=null){
            return value;
        }
        return super.getHeader(name);
    }
    @Override
    public Enumeration<String> getHeaderNames(){
        Set<String> set=new HashSet<>();
        Enumeration<String> e=super.getHeaderNames();
        while(e.hasMoreElements()){
            set.add(e.nextElement());
        }
        set.addAll(customheaders.keySet());
        return Collections.enumeration(set);
    }
    @Override
    public Enumeration<String> getHeaders(String name){
        String value=customheaders.get(name);
        if(value!=null){
            return Collections.enumeration(List.of(value));
        }
        return super.getHeaders(name);
    }
}
