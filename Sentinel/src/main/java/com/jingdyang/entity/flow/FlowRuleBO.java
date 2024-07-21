package com.jingdyang.entity.flow;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class FlowRuleBO {
    private String resource;
    private Double count;
    private List<String> limitApp;
}
