package com.jingdyang.entity.degrade;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "degrade-properties")
@Data
@NoArgsConstructor
public class DegradeProperties {
    private List<DegradeRuleBO> rules;
}
