package com.jingdyang.spel;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class ConstructerTest {

    public static void main(String[] args) {
        ExpressionParser p = new SpelExpressionParser();

        Inventor einstein = p.parseExpression(
                "new com.jingdyang.spel.Inventor('Albert Einstein', 'German')")
                .getValue(Inventor.class);
        System.out.println(einstein.toString());

    }
}
