package com.jingdyang;

import com.jingdyang.spel.Inventor;
import com.jingdyang.spel.PlaceOfBirth;
import com.jingdyang.spel.Society;
import com.jingdyang.spel.TestService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * https://springdoc.cn/spring/core.html#expressions-example-classes
 * https://www.cnblogs.com/kongbubihai/p/15925254.html
 */
public class SpringApplicationTest {

    SpelExpressionParser parser = new SpelExpressionParser();

    SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

    /**
     * Hello World
     */
    @Test
    public void test_hello() {
        // 1 定义解析器
        SpelExpressionParser parser = new SpelExpressionParser();
        // 2 使用解析器解析表达式
        Expression exp = parser.parseExpression("'Hello World'");
        // 3 获取解析结果
        String value = (String) exp.getValue();
        System.out.println(value);
    }

    /**
     * 字符串方法的字面调用
     * 在表达式中调用字符串的普通方法和构造方法。
     */
    @Test
    public void test_String_method() {
        // 1 定义解析器
        SpelExpressionParser parser = new SpelExpressionParser();
        // 2 使用解析器解析表达式
        Expression exp = parser.parseExpression("'Hello World'.concat('!')");
        // 3 获取解析结果
        String value = (String) exp.getValue();
        System.out.println(value);
        exp = parser.parseExpression("'Hello World'.bytes");
        byte[] bytes = (byte[]) exp.getValue();
        System.out.println(Arrays.toString(bytes));
        exp = parser.parseExpression("'Hello World'.bytes.length");
        int length = (Integer) exp.getValue();
        System.out.println("length: " + length);

        //  调用
        exp = parser.parseExpression("new String('hello world').toUpperCase()");
        System.out.println("大写: " + exp.getValue());

    }


    /**
     * 案例3 针对特定对象解析表达式
     * <p>
     * SpEL 更常见的用法是提供针对特定对象实例（称为根对象）进行评估的表达式字符串。案例演示如何从 Inventor 类的实例中检索名称属性或创建布尔条件。
     */
    @Test
    public void test_over_root() {
        // 创建  Inventor 对象
        GregorianCalendar c = new GregorianCalendar();
        c.set(1856, 7, 9);
        Inventor tesla = new Inventor("Nikola Tesla", c.getTime(), "Serbian");
        // 1 定义解析器
        ExpressionParser parser = new SpelExpressionParser();
        // 指定表达式
        Expression exp = parser.parseExpression("name");
        // 在 tesla对象上解析
        String name = (String) exp.getValue(tesla);
        System.out.println(name); // Nikola Tesla

        exp = parser.parseExpression("name == 'Nikola Tesla'");
        // 在 tesla对象上解析并指定返回结果
        boolean result = exp.getValue(tesla, Boolean.class);
        System.out.println(result); // true

        exp = parser.parseExpression("birthdate");
        System.out.println(exp.getValue(tesla, Date.class));
    }


    /**
     * 执行过程分析
     * <p>
     * 上面的案例中SpEL表达式的使用步骤中涉及了几个概念和接口：
     * <p>
     * 用户表达式：我们定义的表达式，如1+1!=2
     * 解析器：ExpressionParser 接口，负责将用户表达式解析成SpEL认识的表达式对象
     * 表达式对象：Expression接口，SpEL的核心，表达式语言都是围绕表达式进行的
     * 评估上下文：EvaluationContext 接口，表示当前表达式对象操作的对象，表达式的评估计算是在上下文上进行的。
     */
    @Test
    public void test_debug() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        Boolean value = parser.parseExpression("1+1!=2").getValue(context, Boolean.class);
        System.out.println(value);
    }

    @Test
    public void test_literal() {
        ExpressionParser parser = new SpelExpressionParser();

        // 字符串 "Hello World"
        String helloWorld = (String) parser.parseExpression("'Hello World'").getValue();
        System.out.println(helloWorld);

        double num = (Double) parser.parseExpression("6.0221415E+23").getValue();
        System.out.println(num);

        // int  2147483647
        int maxValue = (Integer) parser.parseExpression("0x7FFFFFFF").getValue();
        System.out.println(maxValue);

        // 负数
        System.out.println((Integer) parser.parseExpression("-100").getValue());

        // boolean
        boolean trueValue = (Boolean) parser.parseExpression("true").getValue();
        System.out.println(trueValue);

        // null
        Object nullValue = parser.parseExpression("null").getValue();
        System.out.println(nullValue);
    }

    /**
     * 属性 数组 列表 map 索引
     * 属性、数组、列表、Map
     * 属性： 指定属性名，通过"."支持多级嵌套。
     * 数组：[index] 方式
     * 列表：[index] 方式
     * Map：['key'] 方式
     */
    @Test
    public void test2() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        Inventor inventor = new Inventor("发明家1", "中国");
        // 发明作品数组
        inventor.setInventions(new String[]{"发明1", "发明2", "发明3", "发明4"});

        // 1 属性
        String name = parser.parseExpression("name").getValue(context, inventor, String.class);
        System.out.println("属性： " + name);
        // 属性： 发明家1

        // 2 数组表达式
        String invention = parser.parseExpression("inventions[3]").getValue(context, inventor, String.class);
        System.out.println("数组表达式: " + invention);
        // 数组表达式: 发明4

        // 3 List
        List strList = Arrays.asList("str1", "str2", "str3");
        String str = parser.parseExpression("[0]").getValue(context, strList, String.class);
        System.out.println(str);
        // str1

        // 4 map
        Map map = new HashMap<String, String>();
        map.put("xxx", "ooo");
        map.put("xoo", "oxx");
        String value = parser.parseExpression("['xoo']").getValue(context, map, String.class);
        System.out.println(value);
        // oxx
    }

    // 内联List
    @Test
    public void test3() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        List numbers = (List) parser.parseExpression("{1,3,5,7}").getValue(context);
        System.out.println(numbers);
        //[1, 3, 5, 7]
        List listOfList = (List) parser.parseExpression("{{1,3,5,7},{0,2,4,6}}").getValue(context);
        System.out.println(listOfList);
        // [[1, 3, 5, 7], [0, 2, 4, 6]]
    }

    /**
     * 4 内联Map
     */
    @Test
    public void test4() {
        SpelExpressionParser parser = new SpelExpressionParser();
        SimpleEvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();

        Map<String, Object> infoMap =
                (Map<String, Object>) parser.parseExpression("{'name':'name', password:'111'}").getValue();
        System.out.println(infoMap);
        //{name=name, password=111}

        Map mapOfMap =
                (Map) parser.parseExpression("{name:{first:'xxx', last:'ooo'}, password:'111'}").getValue(context);
        System.out.println(mapOfMap);
        // {name={first=xxx, last=ooo}, password=111}
    }


    /**
     * 集合选择
     * 选择是一种强大的表达式语言功能，通过从其元素中进行选择将源集合转换为另一个集合。
     * <p>
     * Map 筛选的元素是 Map.Entry，可以使用 key 和 value 来筛选。
     * <p>
     * 3种用法：
     * 1、从集合按条件筛选生成新集合：.?[selectionExpression]
     * 2、从集合按条件筛选后取第一个元素：.?[selectionExpression]
     * 3、从集合按条件筛选后取最后一个元素：.?[selectionExpression]
     */
    @Test
    public void test15() {
        SpelExpressionParser parser = new SpelExpressionParser();

        Society society = new Society();
        // 发明者列表
        for (int i = 0; i < 5; i++) {
            Inventor inventor = new Inventor("发明家" + i, i % 2 == 0 ? "中国" : "外国");
            society.getMembers().add(inventor);
        }
        // 1、 List 筛选 .?[selectionExpression]
        List<Inventor> list = (List<Inventor>) parser.parseExpression("members.?[nationality == '中国']").getValue(society);
        list.forEach(item -> {
            System.out.println(item.getName() + " : " + item.getNationality());
        });
        // 发明家0 : 中国
        // 发明家2 : 中国
        // 发明家4 : 中国

        // 2、 List  取第一个.^[selectionExpression]  取最后一个.$[selectionExpression]
        Inventor first = parser.parseExpression("members.^[nationality == '中国']").getValue(society, Inventor.class);
        Inventor last = parser.parseExpression("members.$[nationality == '中国']").getValue(society, Inventor.class);
        System.out.println(first.getName() + " : " + first.getNationality());// 发明家0 : 中国
        System.out.println(last.getName() + " : " + last.getNationality()); // 发明家4 : 中国

        // 3 Map 筛选维度是 Map.Entry，其键和值可作为用于选择的属性访问
        society.getOfficers().put("1", 100);
        society.getOfficers().put("2", 200);
        society.getOfficers().put("3", 300);
        Map mapNew = (Map) parser.parseExpression("officers.?[value>100]").getValue(society);
        System.out.println(mapNew); // {2=200, 3=300}
    }

    /**
     * 集合映射
     * 一个集合通过映射的方式转换成新的集合，如从 Map 映射成 List，语法是： .![projectionExpression]
     */
    @Test
    public void test16() {
        Society society = new Society();
        // 发明者列表
        for (int i = 0; i < 5; i++) {
            Inventor inventor = new Inventor("发明家" + i, i % 2 == 0 ? "中国" : "外国");
            society.getMembers().add(inventor);
        }
        // 1、 List<Inventor> 映射到 List<String> 只要name
        List<String> nameList = (List<String>) parser.parseExpression("members.![name]").getValue(society);
        System.out.println(nameList); // [发明家0, 发明家1, 发明家2, 发明家3, 发明家4]

        // 2 Map映射到List
        society.getOfficers().put("1", 100);
        society.getOfficers().put("2", 200);
        society.getOfficers().put("3", 300);
        List<String> kvList = (List<String>) parser.parseExpression("officers.![key + '-' + value]").getValue(society);
        System.out.println(kvList); // [1-100, 2-200, 3-300]
    }

    /**
     * 数组生成
     * 直接使用 new 方式 ，注意： 多维数组不可以初始化。
     */
    @Test
    public void test5() {
        int[] numbers1 = (int[]) parser.parseExpression("new int[4]").getValue(context);
        List<int[]> ints = Arrays.asList(numbers1);
        System.out.println(ints);

        // 一维数组可以初始化
        int[] numbers2 = (int[]) parser.parseExpression("new int[]{1,2,3}").getValue(context);
        System.out.println(numbers2);

        // 多维数组不可以初始化
        int[][] numbers3 = (int[][]) parser.parseExpression("new int[4][5]").getValue(context);
        System.out.println(numbers3);
    }

    /**
     * 关系运算符
     * <p>
     * 使用标准运算符表示法支持关系运算符（等于、不等于、小于、小于或等于、大于和大于或等于）和等价的英文字符缩写表示。
     * 标准符号 	等价英文缩写
     * < 	lt
     * > 	gt
     * <= 	le
     * >= 	ge
     * == 	eq
     * != 	ne
     * / 	div
     * % 	mod
     * ! 	not
     * <p>
     * 注意特殊的 null比任何比较都小，所以 -1 < null 为 false，0 > null 为 false，如果数字比较使用0代替null更好。
     * <p>
     * 支持 instanceof
     * <p>
     * 小心原始类型，因为它们会立即装箱到包装器类型，因此 1 instanceof T(int) 的计算结果为 false，而 1 instanceof T(Integer) 的计算结果为 true。
     * <p>
     * 通过 matches 支持正则表达式
     */
    @Test
    public void test() {
        // true
        boolean trueValue = parser.parseExpression("2 == 2").getValue(Boolean.class);
        // false
        boolean falseValue = parser.parseExpression("2 < -5.0").getValue(Boolean.class);
        // false
        boolean falseValue2 = parser.parseExpression("2 gt -5.0").getValue(Boolean.class);
        // true
        boolean trueValue2 = parser.parseExpression("'black' < 'block'").getValue(Boolean.class);

        // null 比任何比较数小
        // true
        Boolean value = parser.parseExpression("100 > null").getValue(boolean.class);
        // false
        Boolean value2 = parser.parseExpression("-1 < null").getValue(boolean.class);
        System.out.println(value);
        System.out.println(value2);


        // instanceof 支持
        // false
        Boolean aBoolean = parser.parseExpression("'xxx' instanceof T(Integer)").getValue(Boolean.class);
        System.out.println(aBoolean);

        // 支持正则表达式 matches
        // true
        Boolean match = parser.parseExpression(
                "'5.00' matches '^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);
        // false
        Boolean notMatch = parser.parseExpression(
                "'5.0067' matches '^-?\\d+(\\.\\d{2})?$'").getValue(Boolean.class);
        System.out.println(match);
        System.out.println(notMatch);
    }


    /**
     * 逻辑运算符
     * 支持标准符号和英文字符缩写：
     * and (&&)
     * or (||)
     * not (!)
     */
    @Test
    public void test8() {
        Society societyContext = new Society();

        // -- AND --
        // false
        boolean falseValue = parser.parseExpression("true and false").getValue(Boolean.class);
        // true
        String expression = "isMember('Nikola Tesla') and isMember('Mihajlo Pupin')";
        boolean trueValue = parser.parseExpression(expression).getValue(societyContext, Boolean.class);

        // -- OR --

        // true
        boolean trueValue2 = parser.parseExpression("true or false").getValue(Boolean.class);
        // true
        expression = "isMember('Nikola Tesla') or isMember('Albert Einstein')";
        boolean trueValue3 = parser.parseExpression(expression).getValue(societyContext, Boolean.class);

        // -- NOT --

        // false
        boolean falseValue2 = parser.parseExpression("!true").getValue(Boolean.class);

        // -- AND and NOT --
        expression = "isMember('Nikola Tesla') and !isMember('Mihajlo Pupin')";
        boolean falseValue3 = parser.parseExpression(expression).getValue(societyContext, Boolean.class);

        System.out.println(falseValue3);

    }

    /**
     * 数学运算符
     * 可以对数字和字符串使用加法运算符，字符串只支持"+"。
     */
    @Test
    public void test9() {
        // Addition
        int two = parser.parseExpression("1 + 1").getValue(Integer.class);  // 2

        String testString = parser.parseExpression(
                "'test' + ' ' + 'string'").getValue(String.class);  // 'test string'

        // Subtraction
        int four = parser.parseExpression("1 - -3").getValue(Integer.class);  // 4

        double d = parser.parseExpression("1000.00 - 1e4").getValue(Double.class);  // -9000

        // Multiplication
        int six = parser.parseExpression("-2 * -3").getValue(Integer.class);  // 6

        double twentyFour = parser.parseExpression("2.0 * 3e0 * 4").getValue(Double.class);  // 24.0

        // Division
        int minusTwo = parser.parseExpression("6 / -3").getValue(Integer.class);  // -2

        double one = parser.parseExpression("8.0 / 4e0 / 2").getValue(Double.class);  // 1.0

        // Modulus
        int three = parser.parseExpression("7 % 4").getValue(Integer.class);  // 3

        int value = parser.parseExpression("8 / 5 % 2").getValue(Integer.class);  // 1

        int minusTwentyOne = parser.parseExpression("1+2-3*8").getValue(Integer.class);  // -21

    }

    /**
     * 赋值操作
     * 赋值运算符 =用于设置属性。通常在对 setValue 的调用中完成，但也可以在对 getValue 的调用中完成
     */
    @Test
    public void test10() {
        Inventor inventor = new Inventor();
        EvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();
        // setValue 中
        parser.parseExpression("Name").setValue(context, inventor, "xxx");

        //  等价于在 getValue 赋值
        String name = parser.parseExpression(
                "Name = 'xxx'").getValue(context, inventor, String.class);

        System.out.println(name); // xxx
    }

    /**
     * 多级属性安全访问?.
     * 多级属性访问如国家城市城镇nation.city.town三级访问，如果中间的 city是null则会抛出 NullPointerException 异常。
     * 为了避免这种情况的异常，SpEL借鉴了Groovy的语法?.，如果中间属性为null不会抛出异常而是返回null。
     */
    @Test
    public void test18() {
        Inventor inventor = new Inventor("xx", "oo");
        inventor.setPlaceOfBirth(new PlaceOfBirth("北京", "中国"));

        // 正常访问
        String city = parser.parseExpression("PlaceOfBirth?.city").getValue(context, inventor, String.class);
        System.out.println(city); // 北京

        // placeOfBirth为null
        inventor.setPlaceOfBirth(null);
        String city1 = parser.parseExpression("PlaceOfBirth?.city").getValue(context, inventor, String.class);
        System.out.println(city1); // null

        // 非安全访问 异常
        String city3 = parser.parseExpression("PlaceOfBirth.city").getValue(context, inventor, String.class);
        System.out.println(city3); // 抛出异常
    }

    /**
     * 方法调用
     * 使用典型的 Java 编程语法来调用方法。可以在字面上调用方法。还支持可变参数。
     */
    @Test
    public void test6() {
        String bc = parser.parseExpression("'abc'.substring(1, 3)").getValue(String.class);
        System.out.println(bc);
        // bc

        Society societyContext = new Society();
        // 传递参数
        boolean isMember = parser.parseExpression("isMember('Mihajlo Pupin')").getValue(
                societyContext, Boolean.class);
        System.out.println(isMember);
        // false
    }

    /**
     * new 调用构造方法
     * 使用 new 运算符调用构造函数。对除原始类型（int、float 等）和 String 之外的所有类型使用完全限定的类名。
     */
    @Test
    public void test12() {
        Inventor value =
                parser.parseExpression("new com.jingdyang.spel.Inventor('ooo','xxx')").getValue(Inventor.class);
        System.out.println(value.getName() + " " + value.getNationality()); // ooo xxx

        String value1 = parser.parseExpression("new String('xxxxoo')").getValue(String.class);
        System.out.println(value1); // xxxxoo
    }


    /**
     * 类类型T
     * 使用特殊的 T 运算符指定 java.lang.Class 的实例（类型）。
     * 类中的静态变量、静态方法属于Class， 可以通过T(xxx).xxx调用。
     */
    @Test
    public void test11() throws InstantiationException, IllegalAccessException {
        // 1 获取类的Class java.lang包下的类可以不指定全路径
        Class value = parser.parseExpression("T(String)").getValue(Class.class);
        System.out.println(value);

        // 2 获取类的Class 非java.lang包下的类必须指定全路径
        Class dateValue = parser.parseExpression("T(java.util.Date)").getValue(Class.class);
        System.out.println(dateValue);

        // 3 类中的静态变量 静态方法属于Class 通过T(xxx)调用
        boolean trueValue = parser.parseExpression(
                "T(java.math.RoundingMode).CEILING < T(java.math.RoundingMode).FLOOR")
                .getValue(Boolean.class); // true
        System.out.println(trueValue);
        Long longValue = parser.parseExpression("T(Long).parseLong('9999')").getValue(Long.class);
        System.out.println(longValue);// 9999

        Class clazz = parser.parseExpression("T(com.jingdyang.spel.TestService)").getValue(Class.class);
        System.out.println(clazz);
        TestService testService = (TestService) clazz.newInstance();
        String res = testService.getString("666");
        System.out.println(res);
    }

    /**
     * 表达式模板 #{}
     * 表达式模板允许将文字文本与一个或多个评估块混合。每个评估块都由前缀和后缀字符分隔，默认是#{ }。支持实现接口ParserContext自定义前后缀。
     * 调用parseExpression()时指定 ParserContext参数如new TemplateParserContext()
     */
    @Test
    public void test19() {
        String randomStr = parser.parseExpression("随机数字是： #{T(java.lang.Math).random()}", new TemplateParserContext())
                .getValue(String.class);
        System.out.println(randomStr);
    }

    /**
     * 变量 #
     * 定义和使用变量
     * 可以使用#variableName 语法来引用表达式中的变量。通过在 EvaluationContext 实现上使用 setVariable 方法设置变量。
     */
    @Test
    public void test13() {
        Inventor inventor = new Inventor("xxx", "xxx");
        SimpleEvaluationContext context = SimpleEvaluationContext.forReadWriteDataBinding().build();
        context.setVariable("newName", "new ooo");
        // 使用预先的变量赋值 Name 属性
        parser.parseExpression("Name = #newName").getValue(context, inventor);
        System.out.println(inventor.getName()); // new ooo
    }

    /**
     * 方法注册和使用注册和使用自定义方法
     * <p>
     * 函数可以当做一种变量来注册和使用的。2种方式注册：
     * 按变量设置方式 EvaluationContext#setVariable(String name, @Nullable Object value)
     * 按明确的方法设置方式 StandardEvaluationContext#public void registerFunction(String name, Method method) ，其实底下也是按照变量处理。
     */
    @Test
    public void test20() throws NoSuchMethodException {
        // 注册 org.springframework.util.StringUtils.startsWithIgnoreCase(String str,String prefix)
        Method method = StringUtils.class.getDeclaredMethod("startsWithIgnoreCase", String.class, String.class);

        // 方式1 变量方式
        SimpleEvaluationContext simpleEvaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();
        simpleEvaluationContext.setVariable("startsWithIgnoreCase", method);
        Boolean startWith = parser.parseExpression("#startsWithIgnoreCase('123', '111')").getValue(simpleEvaluationContext,
                Boolean.class);
        System.out.println("方式1: " + startWith);

        // 方式2 明确方法方式
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        standardEvaluationContext.registerFunction("startsWithIgnoreCase", method);
        Boolean startWit2 =
                parser.parseExpression("#startsWithIgnoreCase('123', '111')").getValue(simpleEvaluationContext,
                        Boolean.class);
        System.out.println("方式2: " + startWit2);
    }

    /**
     * bean引用
     * 如果评估上下文已经配置了 bean 解析器，可以使用 @ 符号从表达式中查找 bean。直接看案例。
     */
    @Test
    public void test21() {
        AnnotationConfigApplicationContext applicationContext =
                new AnnotationConfigApplicationContext(TestService.class);
        SpelExpressionParser parser = new SpelExpressionParser();
        // 使用 StandardEvaluationContext
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        // 需要注入一个BeanResolver来解析bean引用，此处注入 BeanFactoryResolver
        standardEvaluationContext.setBeanResolver(new BeanFactoryResolver(applicationContext));
        // 使用 @ 来引用bean
        String testServiceResult = parser.parseExpression("@testService.getString('隐约雷鸣，阴霾天空')")
                .getValue(standardEvaluationContext, String.class);
        System.out.println(testServiceResult);
    }


}
