package com.jingdyang.entity.auth;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 鉴权
 */
public class AuthorityRules {
    private HashMap<String, AuthorityRuleBO> authorityRuleBOHashMap;

    public AuthorityRules() {
    }

    public AuthorityRules setAuthorityRuleBOHashMap(HashMap<String, AuthorityRuleBO> authorityRuleBOHashMap) {
        this.authorityRuleBOHashMap = authorityRuleBOHashMap;
        return this;
    }

    public AuthorityRules initAuthorityRules() {
        List<AuthorityRule> rules = new ArrayList<>();
        addAuthorityRule(rules);
        AuthorityRuleManager.loadRules(rules);
        return this;
    }

    private void addAuthorityRule(List<AuthorityRule> rules) {
        for (String ruleName : authorityRuleBOHashMap.keySet()) {
            AuthorityRuleBO authorityRuleBO = authorityRuleBOHashMap.get(ruleName);
            AuthorityRule rule = new AuthorityRule();
            rule.setResource(authorityRuleBO.getResource());
            rule.setStrategy(RuleConstant.AUTHORITY_WHITE);
            rule.setLimitApp(authorityRuleBO.getLimitApp());
            rules.add(rule);
        }
    }

}
