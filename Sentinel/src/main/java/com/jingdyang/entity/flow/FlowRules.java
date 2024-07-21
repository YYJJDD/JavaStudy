package com.jingdyang.entity.flow;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 限流
 */
public class FlowRules {
    private HashMap<String, FlowRuleBO> flowRuleBOHashMap;

    public FlowRules() {
    }

    public FlowRules initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        addFlowRule(rules);
        FlowRuleManager.loadRules(rules);
        return this;
    }

    private void addFlowRule(List<FlowRule> rules) {
        for (String ruleName : flowRuleBOHashMap.keySet()) {
            FlowRuleBO flowRuleBO = flowRuleBOHashMap.get(ruleName);
            List<String> limitApps = flowRuleBO.getLimitApp();
            limitApps.forEach(appId -> {
                FlowRule rule = new FlowRule();
                rule.setRefResource(flowRuleBO.getResource());
                rule.setCount(flowRuleBO.getCount());
                rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
                rule.setLimitApp(appId);
                rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
                // 冷启动 or 直接拒绝 or 匀速通过
                rule.setWarmUpPeriodSec(10);
                rules.add(rule);
            });
        }
    }

    public HashMap<String, FlowRuleBO> getFlowRuleBOHashMap() {
        return  flowRuleBOHashMap;
    }

    public FlowRules setFlowRuleBOHashMap(HashMap<String, FlowRuleBO> flowRuleBOHashMap) {
        this.flowRuleBOHashMap = flowRuleBOHashMap;
        return this;
    }
}
