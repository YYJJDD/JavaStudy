package com.jingdyang.aop.customAnnotation.validator;

import com.jingdyang.aop.customAnnotation.annotation.AsRangeEnum;
import com.jingdyang.aop.customAnnotation.enums.RangeEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class AsRangeEnumValidator implements ConstraintValidator<AsRangeEnum, RangeEnum> {

    private List<RangeEnum> legalEnums;

    @Override
    public void initialize(AsRangeEnum asRangeEnum) {
//        ConstraintValidator.super.initialize(asRangeEnum);
        legalEnums = Arrays.asList(asRangeEnum.legalEnums());
    }

    @Override
    public boolean isValid(RangeEnum range, ConstraintValidatorContext constraintValidatorContext) {
        return legalEnums.contains(range);
    }
}
