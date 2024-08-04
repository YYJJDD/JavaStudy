package com.jingdyang.spel;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public String getString(String name) {
        return name + ": Hello, World!";
    }
}