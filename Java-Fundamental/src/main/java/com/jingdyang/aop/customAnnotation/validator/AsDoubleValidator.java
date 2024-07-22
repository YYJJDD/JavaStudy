package com.jingdyang.aop.customAnnotation.validator;

import com.jingdyang.aop.customAnnotation.annotation.AsDouble;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AsDoubleValidator implements ConstraintValidator<AsDouble, String> {

    private AsDouble asDoubleAnnotation;

    public AsDoubleValidator(AsDouble asDoubleAnnotation) {
        this.asDoubleAnnotation = asDoubleAnnotation;
    }

    @Override
    public void initialize(AsDouble constraintAnnotation) {
        this.asDoubleAnnotation = asDoubleAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        try {
            double dValue = Double.parseDouble(value);
            if (dValue > asDoubleAnnotation.min() && dValue <= asDoubleAnnotation.max()) {
                return true;
            }
        } catch (Exception ignored) {

        }
        return false;
    }
}
