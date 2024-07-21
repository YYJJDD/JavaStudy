package com.jingdyang.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/indicators")
public class TestController {

    @GetMapping("/interpretation")
    public String test(){
        return "88888888";
    }

}
