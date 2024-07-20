package com.jingdyang.aop.annotation;

import com.jingdyang.aop.validator.AsDoubleValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AsDoubleValidator.class)
public @interface AsDouble {
    String message() default "asDouble error!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Exclude
     */
    double min();

    /**
     * Include
     */
    double max();
}
