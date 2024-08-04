package com.jingdyang.spel;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpringExpressionLanguageTest {

    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();

        // evaluates to "Hello World"
        String helloWorld = (String) parser.parseExpression("'Hello World'").getValue();
        System.out.println(helloWorld);

        // evaluates to "Tony's Pizza"
        String pizzaParlor = (String) parser.parseExpression("'Tony''s Pizza'").getValue();
        System.out.println(pizzaParlor);

        double avogadrosNumber = (Double) parser.parseExpression("6.0221415E+23").getValue();
        System.out.println(avogadrosNumber);

        // evaluates to 2147483647
        int maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();
        System.out.println(maxValue);

        boolean trueValue = (Boolean) parser.parseExpression("true").getValue();
        System.out.println(trueValue);

        Object nullValue = parser.parseExpression("null").getValue();
        System.out.println(nullValue);
    }

}
