[TOC]

## 1. 代理模式

​	代理模式是一种比较好理解的设计模式。简单来说就是 **我们使用代理对象来代替对真实对象(real object)的访问，这样就可以在不修改原目标对象的前提下，提供额外的功能操作，扩展目标对象的功能。**

**代理模式的主要作用是扩展目标对象的功能，比如说在目标对象的某个方法执行前后你可以增加一些自定义的操作。**

## 2. 静态代理
​	静态代理中，我们**对目标对象的每个方法的增强都是手动完成的**，**非常不灵活（比如接口一旦新增加方法，目标对象和代理对象都要进行修改）且麻烦(需要对每个目标类都单独写一个代理类）**。 实际应用场景非常非常少，日常开发几乎看不到使用静态代理的场景。

​	上面我们是从实现和应用角度来说的静态代理，从 JVM 层面来说， **静态代理在编译时就将接口、实现类、代理类这些都变成了一个个实际的 class 文件**。

静态代理实现步骤:

1. 定义一个接口及其实现类；
2. 创建一个代理类同样实现这个接口；
3. 将目标对象注入进代理类，然后在代理类的对应方法调用目标类中的对应方法。这样的话，我们就可以通过代理类屏蔽对目标对象的访问，并且可以在目标方法执行前后做一些自己想做的事情。

## 3.动态代理

相比于静态代理来说，动态代理更加灵活。我们不需要针对每个目标类都单独创建一个代理类，并且也不需要我们必须实现接口，我们可以直接代理实现类( *CGLIB 动态代理机制*)。

**从 JVM 角度来说，动态代理是在运行时动态生成类字节码，并加载到 JVM 中的。**

说到动态代理，Spring AOP、RPC 框架应该是两个不得不提的，它们的实现都依赖了动态代理。

**动态代理在我们日常开发中使用的相对较少，但是在框架中的几乎是必用的一门技术。学会了动态代理之后，对于我们理解和学习各种框架的原理也非常有帮助。**

就 Java 来说，动态代理的实现方式有很多种，比如 **JDK 动态代理**、**CGLIB 动态代理**等等。

```
JDK 动态代理有一个最致命的问题是其只能代理实现了接口的类。

为了解决这个问题，我们可以用 CGLIB 动态代理机制来避免。

CGLIB
(Code Generation Library)是一个基于ASM的字节码生成库，它允许我们在运行时对字节码进行修改和动态生成。CGLIB 通过继承方式实现代理。很多知名的开源框架都使用到了CGLIB， 例如 Spring 中的 AOP 模块中：如果目标对象实现了接口，则默认采用 JDK 动态代理，否则采用 CGLIB 动态代理。
```

### 3.1 JDK 动态代理类使用步骤：

1. 定义一个接口及其实现类；
2. 自定义 `InvocationHandler` 并重写`invoke`方法，在 `invoke` 方法中我们会调用原生方法（被代理类的方法）并自定义一些处理逻辑；
3. 通过 `Proxy.newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)` 方法创建代理对象；



### 3.2 CGLIB 动态代理类使用步骤

1. 定义一个类；
2. 自定义 `MethodInterceptor` 并重写 `intercept` 方法，`intercept` 用于拦截增强被代理类的方法，和 JDK 动态代理中的 `invoke` 方法类似；
3. 通过 `Enhancer` 类的 `create()`创建代理类；

### 3.3  JDK 动态代理和 CGLIB 动态代理对比

1. **JDK 动态代理只能代理实现了接口的类或者直接代理接口，而 CGLIB 可以代理未实现任何接口的类。** 另外， CGLIB 动态代理是通过生成一个被代理类的子类来拦截被代理类的方法调用，因此不能代理声明为 final 类型的类和方法。
2. 就二者的效率来说，大部分情况都是 JDK 动态代理更优秀，随着 JDK 版本的升级，这个优势更加明显。

## 4.静态代理和动态代理的对比

1. **灵活性**：动态代理更加灵活，不需要必须实现接口，可以直接代理实现类，并且可以不需要针对每个目标类都创建一个代理类。另外，静态代理中，接口一旦新增加方法，目标对象和代理对象都要进行修改，这是非常麻烦的！
2. **JVM 层面**：静态代理在编译时就将接口、实现类、代理类这些都变成了一个个实际的 class 文件。而动态代理是在运行时动态生成类字节码，并加载到 JVM 中的。



### 动态代理要解决什么问题？

####  什么是代理？

**代理模式**(Proxy pattern): 为另一个对象提供一个替身或占位符以控制对这个对象的访问

![img](https://pdai.tech/images/pics/a6c20f60-5eba-427d-9413-352ada4b40fe.png)

举个简单的例子：

我(client)如果要买(doOperation)房，可以找中介(proxy)买房，中介直接和卖方(target)买房。中介和卖方都实现买卖(doOperation)的操作。中介就是代理(proxy)。

####  什么是动态代理？

> 动态代理就是，在程序运行期，创建目标对象的代理对象，并对目标对象中的方法进行功能性增强的一种技术。

在生成代理对象的过程中，目标对象不变，代理对象中的方法是目标对象方法的增强方法。可以理解为运行期间，对象中方法的动态拦截，在拦截方法的前后执行功能操作。

![img](https://pdai.tech/images/spring/springframework/spring-springframework-aop-61.png)

### 

###  什么是Cglib? SpringAOP和Cglib是什么关系？

> Cglib是一个强大的、高性能的代码生成包，它广泛被许多AOP框架使用，为他们提供方法的拦截。

![img](https://pdai.tech/images/spring/springframework/spring-springframework-aop-62.png)

- 最底层是字节码，字节码相关的知识请参考 [JVM基础 - 类字节码详解](https://pdai.tech/md/java/jvm/java-jvm-class.html)
- ASM是操作字节码的工具
- cglib基于ASM字节码工具操作字节码（即动态生成代理，对方法进行增强）
- SpringAOP基于cglib进行封装，实现cglib方式的动态代理



## Cglib代理的流程

我们把上述Demo的主要流程画出来，你便能很快理解

![img](https://pdai.tech/images/spring/springframework/spring-springframework-aop-63.png)

更多细节：

- 在上图中，我们可以通过在Enhancer中配置更多的参数来控制代理的行为，比如如果只希望增强这个类中的一个方法（而不是所有方法），那就增加callbackFilter来对目标类中方法进行过滤；Enhancer可以有更多的参数类配置其行为，不过我们在学习上述主要的流程就够了。
- final方法为什么不能被代理？很显然final方法没法被子类覆盖，当然不能代理了。
- Mockito为什么不能mock静态方法？因为mockito也是基于cglib动态代理来实现的，static方法也不能被子类覆盖，所以显然不能mock。但PowerMock可以mock静态方法，因为它直接在bytecode上工作，更多可以看[Mockito单元测试]()。（pdai: 通了没？是不是so easy...）



参考：

[Java代理模式详解](https://javaguide.cn/java/basis/proxy.html)

https://pdai.tech/md/spring/spring-x-framework-aop-source-3.html



----------

# JDK动态代理原理：为什么目标对象一定要实现接口？

> ![img](https://i-blog.csdnimg.cn/blog_migrate/e25caf740868bfd779bac6f00cf469bf.png)
>
> https://blog.csdn.net/weixin_45967584/article/details/136848759
>
> https://blog.csdn.net/zwwhnly/article/details/130192925