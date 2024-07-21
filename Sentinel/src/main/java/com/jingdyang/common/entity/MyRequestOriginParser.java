package com.jingdyang.common.entity;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class MyRequestOriginParser implements RequestOriginParser {
    @Override
    public String parseOrigin(HttpServletRequest httpServletRequest) {
        String appId = ThreadLocalRequest.getString(ThreadLocalRequest.APP_ID);
        if(null != appId && appId.length() != 0){
            return appId;
        } else {
            return "UnknownOrigin";
        }
    }
}
