package com.jingdyang.aop.customAnnotation.controller;

import com.jingdyang.aop.customAnnotation.annotation.AsRangeEnum;
import com.jingdyang.aop.customAnnotation.entity.UserPositionDO;
import com.jingdyang.aop.customAnnotation.enums.RangeEnum;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class TestController {

    @PostMapping("/asDouble")
    public String testAsDoubleAnnotation(@RequestBody @Validated UserPositionDO request) {
        System.out.println(request);
        return "666";
    }

    @PostMapping("/asRange")
    // todo 自定义String转RangeEnum枚举序列化器及反序列化器
    public String testAsRangeEnumAnnotation(@RequestBody @Validated @AsRangeEnum(legalEnums = {RangeEnum.M1, RangeEnum.M3}) RangeEnum range) {
        System.out.println(range);
        return "666";
    }

}
