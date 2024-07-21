package com.jingdyang.common.config;


import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.jingdyang.common.entity.MyRequestOriginParser;
import com.jingdyang.common.exception.SentinelExceptionHandler;
import com.jingdyang.entity.ApiPropertyBO;
import com.jingdyang.entity.SentinelProperties;
import com.jingdyang.entity.auth.AuthorityRuleBO;
import com.jingdyang.entity.auth.AuthorityRules;
import com.jingdyang.entity.degrade.DegradeProperties;
import com.jingdyang.entity.degrade.DegradeRuleBO;
import com.jingdyang.entity.degrade.DegradeRules;
import com.jingdyang.entity.flow.FlowRuleBO;
import com.jingdyang.entity.flow.FlowRules;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@Configuration
@ConditionalOnClass(SentinelWebInterceptor.class)
public class SentinelConfiguration {
    @Resource
    private SentinelProperties sentinelProperties;
    @Resource
    private DegradeProperties degradeProperties;
    @Resource
    private SentinelExceptionHandler sentinelExceptionHandler;
    @Resource
    private MyRequestOriginParser myRequestOriginParser;

    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @Bean
    public SentinelWebInterceptor sentinelWebInterceptor() {
        SentinelWebMvcConfig sentinelWebMvcConfig = new SentinelWebMvcConfig();
        sentinelWebMvcConfig.setBlockExceptionHandler(sentinelExceptionHandler);
        sentinelWebMvcConfig.setOriginParser(myRequestOriginParser);
        return new SentinelWebInterceptor(sentinelWebMvcConfig);
    }

    @Bean
    public AuthorityRules authorityRules() {
        List<ApiPropertyBO> apiPropertyBOList = sentinelProperties.getApis();
        HashMap<String, AuthorityRuleBO> authorityRuleBOHashMap = new HashMap<>();
        for (ApiPropertyBO apiProperty : apiPropertyBOList) {
            authorityRuleBOHashMap.put(apiProperty.getApi(), apiProperty.getAuthorityRule());
        }
        return new AuthorityRules()
                .setAuthorityRuleBOHashMap(authorityRuleBOHashMap)
                .initAuthorityRules();
    }

    @Bean
    public FlowRules flowRules() {
        List<ApiPropertyBO> apiPropertyBOList = sentinelProperties.getApis();
        HashMap<String, FlowRuleBO> flowRuleBOHashMap = new HashMap<>();
        for (ApiPropertyBO apiProperty : apiPropertyBOList) {
            List<FlowRuleBO> flowRuleBOList = apiProperty.getFlowRule();
            for (int i = 0; i < flowRuleBOList.size(); i++) {
                flowRuleBOHashMap.put(apiProperty.getApi() + i, flowRuleBOList.get(i));
            }
        }
        return new FlowRules()
                .setFlowRuleBOHashMap(flowRuleBOHashMap)
                .initFlowRules();
    }

    @Bean
    public DegradeRules degradeRules(){
        List<DegradeRuleBO> degradeRuleBOList = degradeProperties.getRules();
        HashMap<String, DegradeRuleBO> degradeRuleBOHashMap = new HashMap<>();
        for (DegradeRuleBO degradeRuleBO : degradeRuleBOList) {
            degradeRuleBOHashMap.put(degradeRuleBO.getRule(), degradeRuleBO);
        }
        return new DegradeRules()
                .setDegradeRuleBOHashMap(degradeRuleBOHashMap)
                .initDegradeRules();
    }

}
