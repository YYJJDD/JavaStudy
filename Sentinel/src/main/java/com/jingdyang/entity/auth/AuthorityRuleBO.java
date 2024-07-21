package com.jingdyang.entity.auth;

import lombok.Data;

@Data
public class AuthorityRuleBO {
    private String resource;
    private String limitApp;
}
