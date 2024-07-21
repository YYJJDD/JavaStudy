package com.jingdyang.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "sentinel-properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentinelProperties implements Serializable {
    private List<ApiPropertyBO> apis;
}
