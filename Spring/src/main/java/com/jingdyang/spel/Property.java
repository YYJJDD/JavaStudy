package com.jingdyang.spel;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Date;

public class Property {
    public static void main(String[] args) {
        // 定义Parser，可以定义全局的parser
        ExpressionParser parser = new SpelExpressionParser();

        // 注意！属性名的第一个字母不区分大小写。 birthdate.year等效于Birthdate.Year
        // 取出Inventor 中，birthdate属性的year属性
        Inventor zhangsan = new Inventor("zhangsan", new Date(), "China");
        // 定义StandardEvaluationContext ，传入一个操作对象
        StandardEvaluationContext zhangsanContext = new StandardEvaluationContext(zhangsan);
        int year = (Integer) parser.parseExpression("birthdate.year + 1900").getValue(zhangsanContext);
        System.out.println(year); // 2024
        System.out.println(parser.parseExpression("birthdate").getValue(zhangsanContext));

        //取出Inventor的placeOfBirth的city属性
        PlaceOfBirth placeOfBirth = new PlaceOfBirth("长沙", "中国");
        zhangsan.setPlaceOfBirth(placeOfBirth);
        String city = (String) parser.parseExpression("placeOfBirth.City").getValue(zhangsanContext);
        System.out.println(city); // 长沙

    }
}
