package com.jingdyang.common.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
@Slf4j
public class SentinelExceptionHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       BlockException e) {
        response.setContentType("application/json;charset=utf-8");
        if (e instanceof FlowException) {
            log.error("Flow Control:{}", e.getMessage());
        } else if (e instanceof AuthorityException) {
            log.error("Unauthorized:{}", e.getMessage());
        }

    }
}
