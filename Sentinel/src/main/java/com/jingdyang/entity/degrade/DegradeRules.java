package com.jingdyang.entity.degrade;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 降级
 */
public class DegradeRules {

    private HashMap<String, DegradeRuleBO> degradeRuleBOHashMap;

    public DegradeRules() {}

    public  DegradeRules setDegradeRuleBOHashMap(HashMap<String, DegradeRuleBO> degradeRuleBOHashMap) {
        this.degradeRuleBOHashMap = degradeRuleBOHashMap;
        return this;
    }

    public DegradeRules initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<>();
        addDegradeRule(rules);
        DegradeRuleManager.loadRules(rules);
        return this;
    }

    private void addDegradeRule(List<DegradeRule> rules) {
        for (String ruleName : degradeRuleBOHashMap.keySet()) {
            DegradeRuleBO degradeRuleBO = degradeRuleBOHashMap.get(ruleName);
            List<String> resources = degradeRuleBO.getResource();
            resources.forEach(resource -> {
                rules.add(initDegradeRule("slow", resource, degradeRuleBO.getSlow()));
                rules.add(initDegradeRule("error", resource, degradeRuleBO.getError()));
            });
        }
    }

    private DegradeRule initDegradeRule(String type,
                                        String resource,
                                        DegradeConfigBO configBO) {
        DegradeRule rule = new DegradeRule();
        rule.setResource(resource);
        rule.setTimeWindow(configBO.getTimeWindow());
        rule.setMinRequestAmount(configBO.getMinRequestAmount());
        if (type.equals("slow")) {
            rule.setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType());
            rule.setCount(Integer.parseInt(configBO.getCount()));
            rule.setSlowRatioThreshold(configBO.getSlowRadio());
        } else {
            rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);
            rule.setCount(Double.parseDouble(configBO.getCount()));
        }
        return rule;
    }

}
