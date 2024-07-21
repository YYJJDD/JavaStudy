package com.jingdyang.entity.degrade;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DegradeRuleBO {
    private String rule;
    private List<String> resource;
    private DegradeConfigBO slow;
    private DegradeConfigBO error;
}
