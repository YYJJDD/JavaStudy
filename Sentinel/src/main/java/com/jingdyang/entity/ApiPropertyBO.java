package com.jingdyang.entity;

import com.jingdyang.entity.auth.AuthorityRuleBO;
import com.jingdyang.entity.flow.FlowRuleBO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class ApiPropertyBO implements Serializable {
    private String api;
    private AuthorityRuleBO authorityRule;
    private List<FlowRuleBO> flowRule;
}
