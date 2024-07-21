package com.jingdyang.reflect.controller;

import com.jingdyang.reflect.entity.IndicatorDTO;
import com.jingdyang.reflect.entity.IndicatorRequestDTO;
import com.jingdyang.reflect.service.IndicatorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class IndicatorController {

    @Resource
    private IndicatorService indicatorService;

    /**
     * 传递List类型的参数到后端，两种方式：
     * 1、以Post方式，后端创建dto @PostMapping + @RequestBody 接收；
     * 2、以GET方式，后端@GetMapping + @RequestParam接收，前端请求：http://localhost:8080/indicator2?wmpIds="333","555"
     */

    @PostMapping("/indicator")
    public List<IndicatorDTO> getIndicatorsByWmpIds(@RequestBody @Validated IndicatorRequestDTO requestDTO) {
        return indicatorService.getIndicators(requestDTO.getWmpIds());
    }

    @GetMapping("/indicator2")
    public List<IndicatorDTO> getIndicatorsByWmpIdsV2(@RequestParam("wmpIds") @Validated List<String> wmpIds) {
        System.out.println(wmpIds);
        return indicatorService.getIndicators(wmpIds);
    }


}
