package com.jingdyang.aop.annotation;

import com.jingdyang.aop.enums.RangeEnum;
import com.jingdyang.aop.validator.AsRangeEnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AsRangeEnumValidator.class)
public @interface AsRangeEnum {
    String message() default "请求参数区间类型异常";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    RangeEnum[] legalEnums();
}
