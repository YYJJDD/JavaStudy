

[TOC]

# 类与类加载

前面我们讲解了JVM的内存结构，包括JVM如何对内存进行划分，如何对内存区域进行垃圾回收。接下来，我们来研究一下类文件结构以及类的加载机制。

## 类文件结构

在我们学习C语言的时候，我们的编程过程会经历如下几个阶段：写代码、保存、编译、运行。实际上，最关键的一步是编译，因为只有经历了编译之后，我们所编写的代码才能够翻译为机器可以直接运行的二进制代码，并且在不同的操作系统下，我们的代码都需要进行一次编译之后才能运行。

> 如果全世界所有的计算机指令集只有x86一种，操作系统只有Windows一种，那也许就不会有Java语言的出现。

随着时代的发展，人们迫切希望能够在不同的操作系统、不同的计算机架构中运行同一套编译之后的代码。本地代码不应该是我们编程的唯一选择，所以，**越来越多的语言选择了与操作系统和机器指令集无关的中立格式作为编译后的存储格式**。

“**一次编写，到处运行**”，Java最引以为傲的口号，标志着平台不再是限制编程语言的阻碍。

实际上，Java正式利用了这样的解决方案，**将源代码编译为平台无关的中间格式**，并通过对应的Java虚拟机读取和运行这些中间格式的编译文件，这样，我们**只需要考虑不同平台的虚拟机如何编写，而Java语言本身很轻松地实现了跨平台**。

现在，越来越多的开发语言都支持**将源代码编译为`.class`字节码文件格式，以便能够直接交给JVM运行，包括Kotlin（安卓开发官方指定语言）、Groovy、Scala等**。

![image-20230306165746693](https://s2.loli.net/2023/03/06/u2K8Y5yU1zf9LQ4.png)

***

## 类加载机制

现在，我们已经了解了字节码文件的结构，以及JVM如何对内存进行管理，现在只剩下最后一个谜团等待解开了，也就是我们的类字节码文件到底是如何加载到内存中的，加载之后又会做什么事情。

### 类加载过程

首先，要加载一个类，一定是出于某种目的的，比如我们要运行我们的Java程序，那么就必须要加载主类才能运行主类中的主方法，又或是我们需要加载数据库驱动，那么可以通过反射来将对应的数据库驱动类进行加载。

所以，一般在这些情况下，如果类没有被加载，那么会被自动加载：

* 使用new关键字创建对象时
* 使用某个类的静态成员（包括方法和字段）的时候（当然，final类型的静态字段有可能在编译的时候被放到了当前类的常量池中，这种情况下是不会触发自动加载的）
* 使用反射对类信息进行获取的时候（之前的数据库驱动就是这样的）
* 加载一个类的子类时
* 加载接口的实现类，且接口带有`default`的方法默认实现时

比如这种情况，那么需要用到另一个类中的成员字段，所以就必须将另一个类加载之后才能访问：

```java
public class Main {
    public static void main(String[] args) {
        System.out.println(Test.str);
    }

    public static class Test{
        static {
            System.out.println("我被初始化了！");
        }

        public static String str = "都看到这里了，不给个三连+关注吗？";
    }
}
```

这里我们就演示一个不太好理解的情况，我们现在将静态成员变量修改为final类型的：

```java
public class Main {
    public static void main(String[] args) {
        System.out.println(Test.str);
    }

    public static class Test{
        static {
            System.out.println("我被初始化了！");
        }

        public final static String str = "都看到这里了，不给个三连+关注吗？";
    }
}
```

可以看到，在主方法中，我们使用了Test类的静态成员变量，并且此静态成员变量是一个final类型的，也就是说不可能再发生改变。那么各位觉得，Test类会像上面一样被初始化吗？

按照正常逻辑来说，既然要用到其他类中的字段，那么肯定需要加载其他类，但是这里我们结果发现，并没有对Test类进行加载，那么这是为什么呢？我们来看看Main类编译之后的字节码指令就知道了：

![image-20230306170622970](https://s2.loli.net/2023/03/06/JyFWfPbBvIK5zMe.png)

很明显，这里使用的是`ldc`指令从常量池中将字符串取出并推向操作数栈顶，也就是说，在编译阶段，整个`Test.str`直接被替换为了对应的字符串（因为final不可能发生改变的，编译就会进行优化，直接来个字符串比你去加载类在获取快得多不是吗，反正结果都一样），所以说编译之后，实际上跟Test类半毛钱关系都没有了。

所以说，当你在某些情况下疑惑为什么类加载了或是没有加载时，可以从字节码指令的角度去进行分析，一般情况下，只要遇到`new`、`getstatic`、`putstatic`、`invokestatic`这些指令时，都会进行类加载，比如：

![image-20230306170635704](https://s2.loli.net/2023/03/06/IRo9i6hntA2jQ3X.png)

这里很明显，是一定会将Test类进行加载的。除此之外，各位也可以试试看数组的定义会不会导致类被加载。

好了，聊完了类的加载触发条件，我们接着来看一下类的详细加载流程。

![image-20230306170654350](https://s2.loli.net/2023/03/06/UIV6fJknmM4bojP.png)

首先类的生命周期一共有7个阶段，而首当其冲的就是加载，加载阶段需要获取此类的二进制数据流，比如我们要从硬盘中读取一个class文件，那么就可以通过文件输入流来获取类文件的`byte[]`，也可以是其他各种途径获取类文件的输入流，甚至网络传输并加载一个类也不是不可以。然后交给类加载器进行加载（类加载器可以是JDK内置的，也可以是开发者自己撸的，后面会详细介绍）类的所有信息会被加载到方法区中，并且在堆内存中会生成一个代表当前类的Class类对象（那么思考一下，同一个Class文件加载的类，是唯一存在的吗？），我们可以通过此对象以及反射机制来访问这个类的各种信息。

数组类要稍微特殊一点，通过前面的检验，我没发现数组在创建后是不会导致类加载的，数组类型本身不会通过类加载器进行加载的，不过你既然要往里面丢对象进去，那最终依然是要加载类的。

接着我们来看验证阶段，验证阶段相当于是对加载的类进行一次规范校验（因为一个类并不一定是由我们使用IDEA编译出来的，有可能是像我们之前那样直接用ASM框架写的一个），如果说类的任何地方不符合虚拟机规范，那么这个类是不会验证通过的，如果没有验证机制，那么一旦出现危害虚拟机的操作，整个程序会出现无法预料的后果。

验证阶段，首先是文件格式的验证：

* 是否魔数为CAFEBABE开头。
* 主、次版本号是否可以由当前Java虚拟机运行
* Class文件各个部分的完整性如何。
* ...

有关类验证的详细过程，可以参考《深入理解Java虚拟机 第三版》268页。

接下来就是准备阶段了，这个阶段会为类变量分配内存，并为一些字段设定初始值，注意是系统规定的初始值，不是我们手动指定的初始值。

再往下就是解析阶段，此阶段是将常量池内的符号引用替换为直接引用的过程，也就是说，到这个时候，所有引用变量的指向都是已经切切实实地指向了内存中的对象了。

到这里，链接过程就结束了，也就是说这个时候类基本上已经完成大部分内容的初始化了。

最后就是真正的初始化阶段了，从这里开始，类中的Java代码部分，才会开始执行，还记得我们之前介绍的`<clinit>`方法吗，它就是在这个时候执行的，比如我们的类中存在一个静态成员变量，并且赋值为10，或是存在一个静态代码块，那么就会自动生成一个`<clinit>`方法来进行赋值操作，但是这个方法是自动生成的。

全部完成之后，我们的类就算是加载完成了。

***

### 类加载器

Java提供了类加载器，以便我们自己可以更好地控制类加载，我们可以自定义类加载器，也可以使用官方自带的类加载器去加载类。对于任意一个类，都必须由加载它的类加载器和这个类本身一起共同确立其在Java虚拟机中的唯一性。

也就是说，一个类可以由不同的类加载器加载，并且，不同的类加载器加载的出来的类，即使来自同一个Class文件，也是不同的，只有两个类来自同一个Class文件并且是由同一个类加载器加载的，才能判断为是同一个。默认情况下，所有的类都是由JDK自带的类加载器进行加载。

比如，我们先创建一个Test类用于测试：

```java
package com.test;

public class Test {
    
}
```

接着我们自己实现一个ClassLoader来加载我们的Test类，同时使用官方默认的类加载器来加载：

```java
public class Main {
    public static void main(String[] args) throws ReflectiveOperationException {
        Class<?> testClass1 = Main.class.getClassLoader().loadClass("com.test.Test");
        CustomClassLoader customClassLoader = new CustomClassLoader();
        Class<?> testClass2 = customClassLoader.loadClass("com.test.Test");

     	  //看看两个类的类加载器是不是同一个
        System.out.println(testClass1.getClassLoader());
        System.out.println(testClass2.getClassLoader());
				
      	//看看两个类是不是长得一模一样
        System.out.println(testClass1);
        System.out.println(testClass2);

      	//两个类是同一个吗？
        System.out.println(testClass1 == testClass2);
      
      	//能成功实现类型转换吗？
        Test test = (Test) testClass2.newInstance();
    }

    static class CustomClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try (FileInputStream stream = new FileInputStream("./target/classes/"+name.replace(".", "/")+".class")){
                byte[] data = new byte[stream.available()];
                stream.read(data);
                if(data.length == 0) return super.loadClass(name);
                return defineClass(name, data, 0, data.length);
            } catch (IOException e) {
                return super.loadClass(name);
            }
        }
    }
}
```

通过结果我们发现，即使两个类是同一个Class文件加载的，只要类加载器不同，那么这两个类就是不同的两个类。

所以说，我们当时在JavaSE阶段讲解的每个类都在堆中有一个唯一的Class对象放在这里来看，并不完全正确，只是当前为了防止各位初学者搞混。

实际上，JDK内部提供的类加载器一共有三个，比如上面我们的Main类，其实是被AppClassLoader加载的，而JDK内部的类，都是由BootstrapClassLoader加载的，这其实就是为了实现**双亲委派机制**而做的。

![image-20230306170728531](https://s2.loli.net/2023/03/06/RFaE7s5CnmylgkT.png)

有关双亲委派机制，我们在JavaSE阶段反射板块已经讲解过了，所以说这就不多做介绍了。

————————————————
版权声明：本文为柏码知识库版权所有，禁止一切未经授权的转载、发布、出售等行为，违者将被追究法律责任。
原文链接：https://www.itbaima.cn/document/r9dq37de0kaeauoi

# JMM内存模型

注意这里提到的内存模型和我们在JVM中介绍的内存模型不在同一个层次，JVM中的内存模型是虚拟机规范对整个内存区域的规划，而Java内存模型，是在JVM内存模型之上的抽象模型，具体实现依然是基于JVM内存模型实现的，我们会在后面介绍。

### Java内存模型

我们在`计算机组成原理`中学习过，在我们的CPU中，一般都会有高速缓存，而它的出现，是为了解决内存的速度跟不上处理器的处理速度的问题，所以CPU内部会添加一级或多级高速缓存来提高处理器的数据获取效率，但是这样也会导致一个很明显的问题，因为现在基本都是多核心处理器，每个处理器都有一个自己的高速缓存，那么又该怎么去保证每个处理器的高速缓存内容一致呢？

![image-20230306171105367](https://s2.loli.net/2023/03/06/Kj2CiIVNOkpG3FS.png)

为了解决缓存一致性的问题，需要各个处理器访问缓存时都遵循一些协议，在读写时要根据协议来进行操作，这类协议有MSI、MESI（Illinois Protocol）、MOSI、Synapse、Firefly及Dragon Protocol等。

而Java也采用了类似的模型来实现支持多线程的内存模型：

![image-20230306171115671](https://s2.loli.net/2023/03/06/UMkWgFatBoLsfr5.png)

JMM（Java Memory Model）内存模型规定如下：

* 所有的变量全部存储在主内存（注意这里包括下面提到的变量，指的都是会出现竞争的变量，包括成员变量、静态变量等，而局部变量这种属于线程私有，不包括在内）
* 每条线程有着自己的工作内存（可以类比CPU的高速缓存）线程对变量的所有操作，必须在工作内存中进行，不能直接操作主内存中的数据。
* 不同线程之间的工作内存相互隔离，如果需要在线程之间传递内容，只能通过主内存完成，无法直接访问对方的工作内存。

也就是说，每一条线程如果要操作主内存中的数据，那么得先拷贝到自己的工作内存中，并对工作内存中数据的副本进行操作，操作完成之后，也需要从工作副本中将结果拷贝回主内存中，具体的操作就是`Save`（保存）和`Load`（加载）操作。

那么各位肯定会好奇，这个内存模型，结合之前JVM所讲的内容，具体是怎么实现的呢？

* 主内存：对应堆中存放对象的实例的部分。
* 工作内存：对应线程的虚拟机栈的部分区域，虚拟机可能会对这部分内存进行优化，将其放在CPU的寄存器或是高速缓存中。比如在访问数组时，由于数组是一段连续的内存空间，所以可以将一部分连续空间放入到CPU高速缓存中，那么之后如果我们顺序读取这个数组，那么大概率会直接缓存命中。

前面我们提到，在CPU中可能会遇到缓存不一致的问题，而Java中，也会遇到，比如下面这种情况：

```java
public class Main {
    private static int i = 0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            for (int j = 0; j < 100000; j++) i++;
            System.out.println("线程1结束");
        }).start();
        new Thread(() -> {
            for (int j = 0; j < 100000; j++) i++;
            System.out.println("线程2结束");
        }).start();
        //等上面两个线程结束
        Thread.sleep(1000);
        System.out.println(i);
    }
}
```

可以看到这里是两个线程同时对变量`i`各自进行100000次自增操作，但是实际得到的结果并不是我们所期望的那样。

那么为什么会这样呢？在之前学习了JVM之后，相信各位应该已经知道，自增操作实际上并不是由一条指令完成的（注意一定不要理解为一行代码就是一个指令完成的）：

![image-20230306171129517](https://s2.loli.net/2023/03/06/2kHoMV1bLOGRe8y.png)

包括变量`i`的获取、修改、保存，都是被拆分为一个一个的操作完成的，那么这个时候就有可能出现在修改完保存之前，另一条线程也保存了，但是当前线程是毫不知情的。

![image-20230306171140325](https://s2.loli.net/2023/03/06/x1O9jinomM3K2dF.png)

所以说，我们当时在JavaSE阶段讲解这个问题的时候，是通过`synchronized`关键字添加同步代码块解决的，当然，我们后面还会讲解另外的解决方案（原子类）

### 重排序

在编译或执行时，为了优化程序的执行效率，编译器或处理器常常会对指令进行重排序，有以下情况：

1. 编译器重排序：Java编译器通过对Java代码语义的理解，根据优化规则对代码指令进行重排序。
2. 机器指令级别的重排序：现代处理器很高级，能够自主判断和变更机器指令的执行顺序。

指令重排序能够在不改变结果（单线程）的情况下，优化程序的运行效率，比如：

```java
public static void main(String[] args) {
    int a = 10;
    int b = 20;
    System.out.println(a + b);
}
```

我们其实可以交换第一行和第二行：

```java
public static void main(String[] args) {
    int b = 10;
    int a = 20;
    System.out.println(a + b);
}
```

即使发生交换，但是我们程序最后的运行结果是不会变的，当然这里只通过代码的形式演示，实际上JVM在执行字节码指令时也会进行优化，可能两个指令并不会按照原有的顺序进行。

虽然单线程下指令重排确实可以起到一定程度的优化作用，但是在多线程下，似乎会导致一些问题：

```java
public class Main {
    private static int a = 0;
    private static int b = 0;
    public static void main(String[] args) {
        new Thread(() -> {
            if(b == 1) {
                if(a == 0) {
                    System.out.println("A");
                }else {
                    System.out.println("B");
                }   
            }
        }).start();
        new Thread(() -> {
            a = 1;
            b = 1;
        }).start();
    }
}
```

上面这段代码，在正常情况下，按照我们的正常思维，是不可能输出`A`的，因为只要b等于1，那么a肯定也是1才对，因为a是在b之前完成的赋值。但是，如果进行了重排序，那么就有可能，a和b的赋值发生交换，b先被赋值为1，而恰巧这个时候，线程1开始判定b是不是1了，这时a还没来得及被赋值为1，可能线程1就已经走到打印那里去了，所以，是有可能输出`A`的。

### volatile关键字

好久好久都没有认识新的关键字了，今天我们来看一个新的关键字`volatile`，开始之前我们先介绍三个词语：

* 原子性：其实之前讲过很多次了，就是要做什么事情要么做完，要么就不做，不存在做一半的情况。
* 可见性：指当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值。
* 有序性：即程序执行的顺序按照代码的先后顺序执行。

我们之前说了，如果多线程访问同一个变量，那么这个变量会被线程拷贝到自己的工作内存中进行操作，而不是直接对主内存中的变量本体进行操作，下面这个操作看起来是一个有限循环，但是是无限的：

```java
public class Main {
    private static int a = 0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (a == 0);
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        a = 1;   //很明显，按照我们的逻辑来说，a的值被修改那么另一个线程将不再循环
    }
}
```

实际上这就是我们之前说的，虽然我们主线程中修改了a的值，但是另一个线程并不知道a的值发生了改变，所以循环中依然是使用旧值在进行判断，因此，普通变量是不具有可见性的。

要解决这种问题，我们第一个想到的肯定是加锁，同一时间只能有一个线程使用，这样总行了吧，确实，这样的话肯定是可以解决问题的：

```java
public class Main {
    private static int a = 0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (a == 0) {
                synchronized (Main.class){}
            }
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        synchronized (Main.class){
            a = 1;
        }
    }
}
```

但是，除了硬加一把锁的方案，我们也可以使用`volatile`关键字来解决，此关键字的第一个作用，就是保证变量的可见性。当写一个`volatile`变量时，JMM会把该线程本地内存中的变量强制刷新到主内存中去，并且这个写会操作会导致其他线程中的`volatile`变量缓存无效，这样，另一个线程修改了这个变时，当前线程会立即得知，并将工作内存中的变量更新为最新的版本。

那么我们就来试试看：

```java
public class Main {
    //添加volatile关键字
    private static volatile int a = 0;
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (a == 0);
            System.out.println("线程结束！");
        }).start();

        Thread.sleep(1000);
        System.out.println("正在修改a的值...");
        a = 1;
    }
}
```

结果还真的如我们所说的那样，当a发生改变时，循环立即结束。

当然，虽然说`volatile`能够保证可见性，但是不能保证原子性，要解决我们上面的`i++`的问题，以我们目前所学的知识，还是只能使用加锁来完成：

```java
public class Main {
    private static volatile int a = 0;
    public static void main(String[] args) throws InterruptedException {
        Runnable r = () -> {
            for (int i = 0; i < 10000; i++) a++;
            System.out.println("任务完成！");
        };
        new Thread(r).start();
        new Thread(r).start();

        //等待线程执行完成
        Thread.sleep(1000);
        System.out.println(a);
    }
}
```

不对啊，`volatile`不是能在改变变量的时候其他线程可见吗，那为什么还是不能保证原子性呢？还是那句话，自增操作是被瓜分为了多个步骤完成的，虽然保证了可见性，但是只要手速够快，依然会出现两个线程同时写同一个值的问题（比如线程1刚刚将a的值更新为100，这时线程2可能也已经执行到更新a的值这条指令了，已经刹不住车了，所以依然会将a的值再更新为一次100）

那要是真的遇到这种情况，那么我们不可能都去写个锁吧？后面，我们会介绍原子类来专门解决这种问题。

最后一个功能就是`volatile`会禁止指令重排，也就是说，如果我们操作的是一个`volatile`变量，它将不会出现重排序的情况，也就解决了我们最上面的问题。那么它是怎么解决的重排序问题呢？若用volatile修饰共享变量，在编译时，会在指令序列中插入`内存屏障`来禁止特定类型的处理器重排序

>  内存屏障（Memory Barrier）又称内存栅栏，是一个CPU指令，它的作用有两个：
>
>  1. 保证特定操作的顺序
>  2. 保证某些变量的内存可见性（volatile的内存可见性，其实就是依靠这个实现的）
>
>  由于编译器和处理器都能执行指令重排的优化，如果在指令间插入一条Memory Barrier则会告诉编译器和CPU，不管什么指令都不能和这条Memory Barrier指令重排序。
>
>  ![image-20230306171216983](https://s2.loli.net/2023/03/06/upc28A41DwCBNO9.png)
>
>  | 屏障类型   | 指令示例                 | 说明                                                         |
>  | ---------- | ------------------------ | ------------------------------------------------------------ |
>  | LoadLoad   | Load1;LoadLoad;Load2     | 保证Load1的读取操作在Load2及后续读取操作之前执行             |
>  | StoreStore | Store1;StoreStore;Store2 | 在Store2及其后的写操作执行前，保证Store1的写操作已刷新到主内存 |
>  | LoadStore  | Load1;LoadStore;Store2   | 在Store2及其后的写操作执行前，保证Load1的读操作已读取结束    |
>  | StoreLoad  | Store1;StoreLoad;Load2   | 保证load1的写操作已刷新到主内存之后，load2及其后的读操作才能执行 |

所以`volatile`能够保证，之前的指令一定全部执行，之后的指令一定都没有执行，并且前面语句的结果对后面的语句可见。

最后我们来总结一下`volatile`关键字的三个特性：

* 保证可见性
* 不保证原子性
* 防止指令重排

在之后我们的设计模式系列视频中，还会讲解单例模式下`volatile`的运用。

### happens-before原则

经过我们前面的讲解，相信各位已经了解了JMM内存模型以及重排序等机制带来的优点和缺点，综上，JMM提出了`happens-before`（先行发生）原则，定义一些禁止编译优化的场景，来向各位程序员做一些保证，只要我们是按照原则进行编程，那么就能够保持并发编程的正确性。具体如下：

* **程序次序规则：** 同一个线程中，按照程序的顺序，前面的操作happens-before后续的任何操作。
  * 同一个线程内，代码的执行结果是有序的。其实就是，可能会发生指令重排，但是保证代码的执行结果一定是和按照顺序执行得到的一致，程序前面对某一个变量的修改一定对后续操作可见的，不可能会出现前面才把a修改为1，接着读a居然是修改前的结果，这也是程序运行最基本的要求。
* **监视器锁规则：** 对一个锁的解锁操作，happens-before后续对这个锁的加锁操作。
  * 就是无论是在单线程环境还是多线程环境，对于同一个锁来说，一个线程对这个锁解锁之后，另一个线程获取了这个锁都能看到前一个线程的操作结果。比如前一个线程将变量`x`的值修改为了`12`并解锁，之后另一个线程拿到了这把锁，对之前线程的操作是可见的，可以得到`x`是前一个线程修改后的结果`12`（所以synchronized是有happens-before规则的）
* **volatile变量规则：** 对一个volatile变量的写操作happens-before后续对这个变量的读操作。
  * 就是如果一个线程先去写一个`volatile`变量，紧接着另一个线程去读这个变量，那么这个写操作的结果一定对读的这个变量的线程可见。
* **线程启动规则：** 主线程A启动线程B，线程B中可以看到主线程启动B之前的操作。
  * 在主线程A执行过程中，启动子线程B，那么线程A在启动子线程B之前对共享变量的修改结果对线程B可见。
* **线程加入规则：** 如果线程A执行操作`join()`线程B并成功返回，那么线程B中的任意操作happens-before线程A`join()`操作成功返回。
* **传递性规则：** 如果A happens-before B，B happens-before C，那么A happens-before C。

那么我们来从happens-before原则的角度，来解释一下下面的程序结果：

```java
public class Main {
    private static int a = 0;
  	private static int b = 0;
    public static void main(String[] args) {
        a = 10;
        b = a + 1;
        new Thread(() -> {
          if(b > 10) System.out.println(a); 
        }).start();
    }
}
```

首先我们定义以上出现的操作：

* **A：** 将变量`a`的值修改为`10`
* **B：** 将变量`b`的值修改为`a + 1`
* **C：** 主线程启动了一个新的线程，并在新的线程中获取`b`，进行判断，如果为`true`那么就打印`a`

首先我们来分析，由于是同一个线程，并且**B**是一个赋值操作且读取了**A**，那么按照**程序次序规则**，A happens-before B，接着在B之后，马上执行了C，按照**线程启动规则**，在新的线程启动之前，当前线程之前的所有操作对新的线程是可见的，所以 B happens-before C，最后根据**传递性规则**，由于A happens-before B，B happens-before C，所以A happens-before C，因此在新的线程中会输出`a`修改后的结果`10`。

————————————————
版权声明：本文为柏码知识库版权所有，禁止一切未经授权的转载、发布、出售等行为，违者将被追究法律责任。
原文链接：https://www.itbaima.cn/document/asncyye9ya18gfar#doc11-JMM%E5%86%85%E5%AD%98%E6%A8%A1%E5%9E%8B

# JVM内存管理

在之前，我们了解了JVM的大致运作原理以及相关特性，这一章，我们首先会从内存管理说起。

在传统的C/C++开发中，我们经常通过使用申请内存的方式来创建对象或是存放某些数据，但是这样也带来了一些额外的问题，我们要在何时释放这些内存，怎么才能使得内存的使用最高效，因此，内存管理是一个非常严肃的问题。

比如我们就可以通过C语言动态申请内存，并用于存放数据：

```c
#include <stdlib.h>
#include <stdio.h>

int main(){
    //动态申请4个int大小的内存空间
    int* memory = malloc(sizeof(int) * 4);
    //修改第一个int空间的值
    memory[0] = 10;
    //修改第二个int空间的值
    memory[1] = 2;
    //遍历内存区域中所有的值
    for (int i = 0;i < 4;i++){
        printf("%d ", memory[i]);
    }
    //释放指针所指向的内存区域
    free(memory);
    //最后将指针赋值为NULL
    memory = NULL;
}
```

而在Java中，这种操作实际上是不允许的，Java只支持直接使用基本数据类型和对象类型，至于内存到底如何分配，并不是由我们来处理，而是JVM帮助我们进行控制，这样就帮助我们节省很多内存上的工作，虽然带来了很大的便利，但是，一旦出现内存问题，我们就无法像C/C++那样对所管理的内存进行合理地处理，因为所有的内存操作都是由JVM在进行，只有了解了JVM的内存管理机制，我们才能够在出现内存相关问题时找到解决方案。

## 内存区域划分

既然要管理内存，那么肯定不会是杂乱无章的，JVM对内存的管理采用的是分区治理，不同的内存区域有着各自的职责所在，在虚拟机运行时，内存区域如下划分：

![点击查看图片来源](https://s2.loli.net/2023/03/06/CP4yv1iqrfjmXzW.jpg)

我们可以看到，内存区域一共分为5个区域，其中方法区和堆是所有线程共享的区域，随着虚拟机的创建而创建，虚拟机的结束而销毁，而虚拟机栈、本地方法栈、程序计数器都是线程之间相互隔离的，每个线程都有一个自己的区域，并且线程启动时会自动创建，结束之后会自动销毁。内存划分完成之后，我们的JVM执行引擎和本地库接口，也就是Java程序开始运行之后就会根据分区合理地使用对应区域的内存了。

### 大致划分

#### 程序计数器

首先我们来介绍一下程序计数器，它和我们的传统8086 CPU中PC寄存器的工作差不多，因为JVM虚拟机目的就是实现物理机那样的程序执行。在8086 CPU中，PC作为程序计数器，负责储存内存地址，该地址指向下一条即将执行的指令，每解释执行完一条指令，PC寄存器的值就会自动被更新为下一条指令的地址，进入下一个指令周期时，就会根据当前地址所指向的指令，进行执行。

而JVM中的程序计数器可以看做是当前线程所执行字节码的行号指示器，而行号正好就指的是某一条指令，字节码解释器在工作时也会改变这个值，来指定下一条即将执行的指令。

因为**Java的多线程也是依靠时间片轮转算法进行的，因此一个CPU同一时间也只会处理一个线程，当某个线程的时间片消耗完成后，会自动切换到下一个线程继续执行**，而**当前线程的执行位置会被保存到当前线程的程序计数器中**，当下次轮转到此线程时，又继续根据之前的执行位置继续向下执行。

程序计数器因为只需要记录很少的信息，所以只占用很少一部分内存。

#### 虚拟机栈

虚拟机栈就是一个非常关键的部分，看名字就知道它是一个栈结构，每个方法被执行的时候，Java虚拟机都会同步创建一个**栈帧**（其实就是栈里面的一个元素），栈帧中包括了当前方法的一些信息，比如局部变量表、操作数栈、动态链接、方法出口等。

![image-20230306164822720](https://s2.loli.net/2023/03/06/As1NGy6BwhKJ9kY.png)

其中局部变量表就是我们方法中的局部变量，之前我们也进行过演示，实际上局部变量表在class文件中就已经定义好了，操作数栈就是我们之前字节码执行时使用到的栈结构； 每个栈帧还保存了一个**可以指向当前方法所在类**的运行时常量池，目的是：当前方法中如果需要调用其他方法的时候，能够从运行时常量池中找到对应的符号引用，然后将符号引用转换为直接引用，然后就能直接调用对应方法，这就是动态链接（我们还没讲到常量池，暂时记住即可，建议之后再回顾一下），最后是方法出口，也就是方法该如何结束，是抛出异常还是正常返回。

可能听起来有点懵逼，这里我们来模拟一下整个虚拟机栈的运作流程，我们先编写一个测试类：

```java
public class Main {
    public static void main(String[] args) {
        int res = a();
        System.out.println(res);
    }

    public static int a(){
        return b();
    }

    public static int b(){
        return c();
    }

    public static int c(){
        int a = 10;
        int b = 20;
        return a + b;
    }
}
```

当我们的主方法执行后，会依次执行三个方法`a() -> b() -> c() -> 返回`，我们首先来观察一下反编译之后的结果：

```
{
  public com.test.Main();   #这个是构造方法
    descriptor: ()V
    flags: ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 3: 0
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0       5     0  this   Lcom/test/Main;

  public static void main(java.lang.String[]);    #主方法
    descriptor: ([Ljava/lang/String;)V
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=1
         0: invokestatic  #2                  // Method a:()I
         3: istore_1
         4: getstatic     #3                  // Field java/lang/System.out:Ljava/io/PrintStream;
         7: iload_1
         8: invokevirtual #4                  // Method java/io/PrintStream.println:(I)V
        11: return
      LineNumberTable:
        line 5: 0
        line 6: 4
        line 7: 11
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      12     0  args   [Ljava/lang/String;
            4       8     1   res   I

  public static int a();
    descriptor: ()I
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=1, locals=0, args_size=0
         0: invokestatic  #5                  // Method b:()I
         3: ireturn
      LineNumberTable:
        line 10: 0

  public static int b();
    descriptor: ()I
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=1, locals=0, args_size=0
         0: invokestatic  #6                  // Method c:()I
         3: ireturn
      LineNumberTable:
        line 14: 0

  public static int c();
    descriptor: ()I
    flags: ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=2, args_size=0
         0: bipush        10
         2: istore_0
         3: bipush        20
         5: istore_1
         6: iload_0
         7: iload_1
         8: iadd
         9: ireturn
      LineNumberTable:
        line 18: 0
        line 19: 3
        line 20: 6
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            3       7     0     a   I
            6       4     1     b   I
}

```

可以看到在编译之后，我们整个方法的最大操作数栈深度、局部变量表都是已经确定好的，当我们程序开始执行时，会根据这些信息封装为对应的栈帧，我们从`main`方法开始看起：

![image-20230306164838564](https://s2.loli.net/2023/03/06/rRtxbFZXDkmGci4.png)

接着我们继续往下，到了` 0: invokestatic  #2                  // Method a:()I`时，需要调用方法`a()`，这时当前方法就不会继续向下运行了，而是去执行方法`a()`，那么同样的，将此方法也入栈，注意是放入到栈顶位置，`main`方法的栈帧会被压下去：

![image-20230306164848411](https://s2.loli.net/2023/03/06/SZJWH8l5xByTjr7.png)

这时，进入方法a之后，又继而进入到方法b，最后在进入c，因此，到达方法c的时候，我们的虚拟机栈变成了：

![image-20230306164859169](https://s2.loli.net/2023/03/06/FdS8U4lHVjCLKwD.png)

现在我们依次执行方法c中的指令，最后返回a+b的结果，在方法c返回之后，也就代表方法c已经执行结束了，栈帧4会自动出栈，这时栈帧3就得到了上一栈帧返回的结果，并继续执行，但是由于紧接着马上就返回，所以继续重复栈帧4的操作，此时栈帧3也出栈并继续将结果交给下一个栈帧2，最后栈帧2再将结果返回给栈帧1，然后栈帧1就可以继续向下运行了，最后输出结果。

![image-20230306164908882](https://s2.loli.net/2023/03/06/FBpxKWIbuY7ftq4.png)

#### 本地方法栈

本地方法栈与虚拟机栈作用差不多，但是它 备的，这里不多做介绍。

#### 堆

堆是整个Java应用程序共享的区域，也是整个虚拟机最大的一块内存空间，而此区域的职责就是存放和管理对象和数组，而我们马上要提到的垃圾回收机制也是主要作用于这一部分内存区域。

#### 方法区

方法区也是整个Java应用程序共享的区域，它用于存储所有的类信息、常量、静态变量、动态编译缓存等数据，可以大致分为两个部分，一个是类信息表，一个是运行时常量池。方法区也是我们要重点介绍的部分。

![image-20230306164925187](https://s2.loli.net/2023/03/06/vYL53rtIDAWcQhb.png)

首先类信息表中存放的是当前应用程序加载的所有类信息，包括类的版本、字段、方法、接口等信息，同时会将编译时生成的常量池数据全部存放到运行时常量池中。当然，常量也并不是只能从类信息中获取，在程序运行时，也有可能会有新的常量进入到常量池。

其实我们的String类正是利用了常量池进行优化，这里我们编写一个测试用例：

```java
public static void main(String[] args) {
    String str1 = new String("abc");
    String str2 = new String("abc");

    System.out.println(str1 == str2);
    System.out.println(str1.equals(str2));
}
```

得到的结果也是显而易见的，由于`str1`和`str2`是单独创建的两个对象，那么这两个对象实际上会在堆中存放，保存在不同的地址：

![image-20230306164934743](https://s2.loli.net/2023/03/06/tnqFSxzEcB4U9WL.png)

所以当我们使用`==`判断时，得到的结果`false`，而使用`equals`时因为比较的是值，所以得到`true`。现在我们来稍微修改一下：

```java
public static void main(String[] args) {
    String str1 = "abc";
    String str2 = "abc";

    System.out.println(str1 == str2);
    System.out.println(str1.equals(str2));
}
```

现在我们没有使用new的形式，而是直接使用双引号创建，那么这时得到的结果就变成了两个`true`，这是为什么呢？这其实是因为我们直接使用双引号赋值，会先在常量池中查找是否存在相同的字符串，若存在，则将引用直接指向该字符串；若不存在，则在常量池中生成一个字符串，再将引用指向该字符串：

![image-20230306164942208](https://s2.loli.net/2023/03/06/GTgbpIYdCyK3RLj.png)

实际上两次调用String类的`intern()`方法，和上面的效果差不多，也是第一次调用会将堆中字符串复制并放入常量池中，第二次通过此方法获取字符串时，会查看常量池中是否包含，如果包含那么会直接返回常量池中字符串的地址：

```java
public static void main(String[] args) {
    //不能直接写"abc"，双引号的形式，写了就直接在常量池里面吧abc创好了
    String str1 = new String("ab")+new String("c");
    String str2 = new String("ab")+new String("c");

    System.out.println(str1.intern() == str2.intern());
    System.out.println(str1.equals(str2));
}
```

![image-20230306164954716](https://s2.loli.net/2023/03/06/EOC5ilkr396BHNy.png)

所以上述结果中得到的依然是两个`true`。在JDK1.7之后，稍微有一些区别，在调用`intern()`方法时，当常量池中没有对应的字符串时，不会再进行复制操作，而是将其直接修改为指向当前字符串堆中的的引用：

![image-20230306165005169](https://s2.loli.net/2023/03/06/2fXENphit4OZvVk.png)

```java
public static void main(String[] args) {
  	//不能直接写"abc"，双引号的形式，写了就直接在常量池里面吧abc创好了
    String str1 = new String("ab")+new String("c");
    System.out.println(str1.intern() == str1);
}
```

```java
public static void main(String[] args) {
    String str1 = new String("ab")+new String("c");
    String str2 = new String("ab")+new String("c");

    System.out.println(str1 == str1.intern());
    System.out.println(str2.intern() == str1);
}
```

所以最后我们会发现，`str1.intern()`和`str1`都是同一个对象，结果为`true`。

值得注意的是，在JDK7之后，字符串常量池从方法区移动到了堆中。

最后我们再来进行一个总结，各个内存区域的用途：

* （线程独有）程序计数器：保存当前程序的执行位置。
* （线程独有）虚拟机栈：通过栈帧来维持方法调用顺序，帮助控制程序有序运行。
* （线程独有）本地方法栈：同上，作用与本地方法。
* 堆：所有的对象和数组都在这里保存。
* 方法区：类信息、即时编译器的代码缓存、运行时常量池。

当然，这些内存区域划分仅仅是概念上的，具体的实现过程我们后面还会提到。

### 爆内存和爆栈

实际上，在Java程序运行时，内存容量不可能是无限制的，当我们的对象创建过多或是数组容量过大时，就会导致我们的堆内存不足以存放更多新的对象或是数组，这时就会出现错误，比如：

```java
public static void main(String[] args) {
    int[] a = new int[Integer.MAX_VALUE];
}
```

这里我们申请了一个容量为21亿多的int型数组，显然，如此之大的数组不可能放在我们的堆内存中，所以程序运行时就会这样：

```java
Exception in thread "main" java.lang.OutOfMemoryError: Requested array size exceeds VM limit
	at com.test.Main.main(Main.java:5)
```

这里得到了一个`OutOfMemoryError`错误，也就是我们常说的内存溢出错误。我们可以通过参数来控制堆内存的最大值和最小值：

```
-Xms最小值 -Xmx最大值
```

比如我们现在限制堆内存为固定值1M大小，并且在抛出内存溢出异常时保存当前的内存堆转储快照：

![image-20230306165041598](https://s2.loli.net/2023/03/06/r5IsmTk3DZfXA26.png)

注意堆内存不要设置太小，不然连虚拟机都不足以启动，接着我们编写一个一定会导致内存溢出的程序：

```java
public class Main {
    public static void main(String[] args) {
        List<Test> list = new ArrayList<>();
        while (true){
            list.add(new Test());    //无限创建Test对象并丢进List中
        }
    }

    static class Test{ }
}
```

在程序运行之后：

```
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid35172.hprof ...
Heap dump file created [12895344 bytes in 0.028 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at java.util.Arrays.copyOf(Arrays.java:3210)
	at java.util.Arrays.copyOf(Arrays.java:3181)
	at java.util.ArrayList.grow(ArrayList.java:267)
	at java.util.ArrayList.ensureExplicitCapacity(ArrayList.java:241)
	at java.util.ArrayList.ensureCapacityInternal(ArrayList.java:233)
	at java.util.ArrayList.add(ArrayList.java:464)
	at com.test.Main.main(Main.java:10)
```

可以看到错误出现原因正是`Java heap space`，也就是堆内存满了，并且根据我们设定的VM参数，堆内存保存了快照信息。我们可以在IDEA内置的Profiler中进行查看：

![image-20230306165105858](https://s2.loli.net/2023/03/06/C71bdyIY2JVpLKz.png)

可以很明显地看到，在创建了360146个Test对象之后，堆内存蚌埠住了，于是就抛出了内存溢出错误。

我们接着来看栈溢出，我们知道，虚拟机栈会在方法调用时插入栈帧，那么，设想如果出现无限递归的情况呢？

```java
public class Main {
    public static void main(String[] args) {
        test();
    }

    public static void test(){
        test();
    }
}
```

这很明显是一个永无休止的程序，并且会不断继续向下调用test方法本身，那么按照我们之前的逻辑推导，无限地插入栈帧那么一定会将虚拟机栈塞满，所以，当栈的深度已经不足以继续插入栈帧时，就会这样：

```
Exception in thread "main" java.lang.StackOverflowError
	at com.test.Main.test(Main.java:12)
	at com.test.Main.test(Main.java:12)
	at com.test.Main.test(Main.java:12)
	at com.test.Main.test(Main.java:12)
	at com.test.Main.test(Main.java:12)
	at com.test.Main.test(Main.java:12)
	....以下省略很多行
```

这也是我们常说的栈溢出，它和堆溢出比较类似，也是由于容纳不下才导致的，我们可以使用`-Xss`来设定栈容量。

### 申请堆外内存

除了堆内存可以存放对象数据以外，我们也可以申请堆外内存（直接内存），也就是不受JVM管控的内存区域，这部分区域的内存需要我们自行去申请和释放，实际上本质就是JVM通过C/C++调用`malloc`函数申请的内存，当然得我们自己去释放了。不过虽然是直接内存，不会受到堆内存容量限制，但是依然会受到本机最大内存的限制，所以还是有可能抛出`OutOfMemoryError`异常。

这里我们需要提到一个堆外内存操作类：`Unsafe`，就像它的名字一样，虽然Java提供堆外内存的操作类，但是实际上它是不安全的，只有你完全了解底层原理并且能够合理控制堆外内存，才能安全地使用堆外内存。

注意这个类不让我们new，也没有直接获取方式（压根就没想让我们用）：

```java
public final class Unsafe {

    private static native void registerNatives();
    static {
        registerNatives();
        sun.reflect.Reflection.registerMethodsToFilter(Unsafe.class, "getUnsafe");
    }

    private Unsafe() {}

    private static final Unsafe theUnsafe = new Unsafe();
  
    @CallerSensitive
    public static Unsafe getUnsafe() {
        Class<?> caller = Reflection.getCallerClass();
        if (!VM.isSystemDomainLoader(caller.getClassLoader()))
            throw new SecurityException("Unsafe");   //不是JDK的类，不让用。
        return theUnsafe;
    }
```

所以我们这里就通过反射给他giao出来：

```java
public static void main(String[] args) throws IllegalAccessException {
    Field unsafeField = Unsafe.class.getDeclaredFields()[0];
    unsafeField.setAccessible(true);
    Unsafe unsafe = (Unsafe) unsafeField.get(null);
    
}
```

成功拿到Unsafe类之后，我们就可以开始申请堆外内存了，比如我们现在想要申请一个int大小的内存空间，并在此空间中存放一个int类型的数据：

```java
public static void main(String[] args) throws IllegalAccessException {
    Field unsafeField = Unsafe.class.getDeclaredFields()[0];
    unsafeField.setAccessible(true);
    Unsafe unsafe = (Unsafe) unsafeField.get(null);

    //申请4字节大小的内存空间，并得到对应位置的地址
    long address = unsafe.allocateMemory(4);
    //在对应的地址上设定int的值
    unsafe.putInt(address, 6666666);
    //获取对应地址上的Int型数值
    System.out.println(unsafe.getInt(address));
    //释放申请到的内容
    unsafe.freeMemory(address);

    //由于内存已经释放，这时数据就没了
    System.out.println(unsafe.getInt(address));
}
```

我们可以来看一下`allocateMemory`底层是如何调用的，这是一个native方法，我们来看C++源码：

```cpp
UNSAFE_ENTRY(jlong, Unsafe_AllocateMemory0(JNIEnv *env, jobject unsafe, jlong size)) {
  size_t sz = (size_t)size;

  sz = align_up(sz, HeapWordSize);
  void* x = os::malloc(sz, mtOther);   //这里调用了os::malloc方法

  return addr_to_java(x);
} UNSAFE_END
```

接着来看：

```cpp
void* os::malloc(size_t size, MEMFLAGS flags) {
  return os::malloc(size, flags, CALLER_PC);
}

void* os::malloc(size_t size, MEMFLAGS memflags, const NativeCallStack& stack) {
	...
  u_char* ptr;
  ptr = (u_char*)::malloc(alloc_size);   //调用C++标准库函数 malloc(size)
	....
  // we do not track guard memory
  return MemTracker::record_malloc((address)ptr, size, memflags, stack, level);
}
```

所以，我们上面的Java代码转换为C代码，差不多就是这个意思：

```c
#include <stdlib.h>
#include <stdio.h>

int main(){
    int * a = malloc(sizeof(int));
    *a = 6666666;
    printf("%d\n", *a);
    free(a);
    printf("%d\n", *a);
}
```

所以说，直接内存实际上就是JVM申请的一块额外的内存空间，但是它并不在受管控的几种内存空间中，当然这些内存依然属于是JVM的，由于JVM提供的堆内存会进行垃圾回收等工作，效率不如直接申请和操作内存来得快，一些比较追求极致性能的框架会用到堆外内存来提升运行速度，如nio框架。

当然，Unsafe类不仅仅只是这些功能，在其他系列课程中，我们还会讲到它。

***

## 垃圾回收机制

**注意：** 此部分为重点内容。

我们前面提到，Java会自动管理和释放内存，它不像C/C++那样要求我们手动管理内存，JVM提供了一套全自动的内存管理机制，当一个Java对象不再用到时，JVM会自动将其进行回收并释放内存，那么对象所占内存在什么时候被回收，如何判定对象可以被回收，以及如何去进行回收工作也是JVM需要关注的问题。

### 对象存活判定算法

首先我们来套讨论第一个问题，也就是：对象在什么情况下可以被判定为不再使用已经可以回收了？这里就需要提到以下几种垃圾回收算法了。

![image-20230306165128192](https://s2.loli.net/2023/03/06/stqTu8gD3ykixva.png)

#### 引用计数法

我们知道，如果我们要经常操作一个对象，那么首先一定会创建一个引用变量：

```java
//str就是一个引用类型的变量，它持有对后面字符串对象的引用，可以代表后面这个字符串对象本身
String str = "lbwnb";

//str.xxxxx...
```

实际上，我们会发现，只要一个对象还有使用价值，我们就会通过它的引用变量来进行操作，那么可否这样判断一个对象是否还需要被使用：

* 每个对象都包含一个 **引用计数器**，用于存放引用计数（其实就是存放被引用的次数）
* 每当有一个地方引用此对象时，引用计数`+1`
* 当引用失效（ 比如离开了局部变量的作用域或是引用被设定为`null`）时，引用计数`-1`
* 当引用计数为`0`时，表示此对象不可能再被使用，因为这时我们已经没有任何方法可以得到此对象的引用了

但是这样存在一个问题，如果两个对象相互引用呢？

```java
public class Main {
    public static void main(String[] args) {
        Test a = new Test();
        Test b = new Test();

        a.another = b;
        b.another = a;

        //这里直接把a和b赋值为null，这样前面的两个对象我们不可能再得到了
        a = b = null;
    }

    private static class Test{
        Test another;
    }
}
```

按照引用计数算法，那么当出现以上情况时，虽然我们无法在得到此对象的引用了，并且此对象我们也无需再使用，但是由于这两个对象直接存在相互引用的情况，那么引用计数器的值将会永远是`1`，但是实际上此对象已经没有任何用途了。所以引用计数法并不是最好的解决方案。

#### 可达性分析算法

目前比较主流的编程语言（包括Java），一般都会使用可达性分析算法来判断对象是否存活，它采用了类似于树结构的搜索机制。

首先每个对象的引用都有机会成为树的根节点（GC Roots），可以被选定作为根节点条件如下：

* 位于虚拟机栈的栈帧中的本地变量表中所引用到的对象（其实就是我们方法中的局部变量）同样也包括本地方法栈中JNI引用的对象。
* 类的静态成员变量引用的对象。
* 方法区中，常量池里面引用的对象，比如我们之前提到的`String`类型对象。
* 被添加了锁的对象（比如synchronized关键字）
* 虚拟机内部需要用到的对象。

![image-20230306165140204](https://s2.loli.net/2023/03/06/4MIORcEDzquZFia.png)

一旦已经存在的根节点不满足存在的条件时，那么根节点与对象之间的连接将被断开。此时虽然对象1仍存在对其他对象的引用，但是由于其没有任何根节点引用，所以此对象即可被判定为不再使用。比如某个方法中的局部变量引用，在方法执行完成返回之后：

![image-20230306165153841](https://s2.loli.net/2023/03/06/WYSGI5xCh8mOBcK.png)

这样就能很好地解决我们刚刚提到的循环引用问题，我们再来重现一下出现循环引用的情况：

![image-20230306165211604](https://s2.loli.net/2023/03/06/48ZguJqIK6ojEba.png)

可以看到，对象1和对象2依然是存在循环引用的，但是只有他们各自的GC Roots断开，那么就会变成下面这样：

![image-20230306165225991](https://s2.loli.net/2023/03/06/KdZpLNTzEitCOB3.png)

所以，我们最后进行一下总结：如果某个对象无法到达任何GC Roots，则证明此对象是不可能再被使用的。

#### 最终判定

虽然在经历了可达性分析算法之后基本可能判定哪些对象能够被回收，但是并不代表此对象一定会被回收，我们依然可以在最终判定阶段对其进行挽留。

还记得我们之前在讲解`Object`类时提到的`finalize()`方法吗？

```java
/**
 * Called by the garbage collector on an object when garbage collection
 * determines that there are no more references to the object.
 * A subclass overrides the {@code finalize} method to dispose of
 * system resources or to perform other cleanup.
 * ...
 */
protected void finalize() throws Throwable { }
```

此方法正是最终判定方法，如果子类重写了此方法，那么子类对象在被判定为可回收时，会进行二次确认，也就是执行`finalize()`方法，而在此方法中，当前对象是完全有可能重新建立GC Roots的！所以，如果在二次确认后对象不满足可回收的条件，那么此对象不会被回收，巧妙地逃过了垃圾回收的命运。比如下面这个例子：

```java
public class Main {
    private static Test a;
    public static void main(String[] args) throws InterruptedException {
        a = new Test();

        //这里直接把a赋值为null，这样前面的对象我们不可能再得到了
        a  = null;

        //手动申请执行垃圾回收操作（注意只是申请，并不一定会执行，但是一般情况下都会执行）
        System.gc();

        //等垃圾回收一下()
        Thread.sleep(1000);

        //我们来看看a有没有被回收
        System.out.println(a);
    }

    private static class Test{
        @Override
        protected void finalize() throws Throwable {
            System.out.println(this+" 开始了它的救赎之路！");
            a = this;
        }
    }
}
```

注意`finalize()`方法并不是在主线程调用的，而是虚拟机自动建立的一个低优先级的`Finalizer`线程（正是因为优先级比较低，所以前面才需要等待1秒钟）进行处理，我们可以稍微修改一下看看：

```java
private static class Test{
    @Override
    protected void finalize() throws Throwable {
        System.out.println(Thread.currentThread());
        a = this;
    }
}
```

```
Thread[Finalizer,8,system]
com.test.Main$Test@232204a1
```

同时，同一个对象的`finalize()`方法只会有一次调用机会，也就是说，如果我们连续两次这样操作，那么第二次，对象必定被回收：

```java
public static void main(String[] args) throws InterruptedException {
    a = new Test();
    //这里直接把a赋值为null，这样前面的对象我们不可能再得到了
    a  = null;
    //手动申请执行垃圾回收操作（注意只是申请，并不一定会执行，但是一般情况下都会执行）
    System.gc();
    //等垃圾回收一下
    Thread.sleep(1000);
    
    System.out.println(a);
    //这里直接把a赋值为null，这样前面的对象我们不可能再得到了
    a  = null;
    //手动申请执行垃圾回收操作（注意只是申请，并不一定会执行，但是一般情况下都会执行）
    System.gc();
    //等垃圾回收一下
    Thread.sleep(1000);
    
    System.out.println(a);
}
```

当然，`finalize()`方法也并不是专门防止对象被回收的，我们可以使用它来释放一些程序使用中的资源等。

最后，总结成一张图：

![image-20230306165250173](https://s2.loli.net/2023/03/06/8sSvloxtKINFYz4.png)

当然，除了堆中的对象以外，方法区中的数据也是可以被垃圾回收的，但是回收条件比较严格，这里就暂时不谈了。

***

### 垃圾回收算法

前面我们介绍了对象存活判定算法，现在我们已经可以准确地知道堆中的哪些对象可以被回收了，那么，接下来就该考虑如何对对象进行回收了，垃圾收集器会不定期地检查堆中的对象，查看它们是否满足被回收的条件。我们该如何对这些对象进行回收，是一个一个判断是否需要回收吗？

#### 分代收集机制

实际上，如果我们对堆中的每一个对象都依次判断是否需要回收，这样的效率其实是很低的，那么有没有更好地回收机制呢？第一步，我们可以对堆中的对象进行分代管理。

比如某些对象，在多次垃圾回收时，都未被判定为可回收对象，我们完全可以将这一部分对象放在一起，并让垃圾收集器减少回收此区域对象的频率，这样就能很好地提高垃圾回收的效率了。

因此，Java虚拟机将堆内存划分为**新生代**、**老年代**和**永久代**（其中永久代是HotSpot虚拟机特有的概念，在JDK8之前方法区实际上就是采用的永久代作为实现，而在JDK8之后，方法区由元空间实现，并且使用的是本地内存，容量大小取决于物理机实际大小，之后会详细介绍）这里我们主要讨论的是**新生代**和**老年代**。

不同的分代内存回收机制也存在一些不同之处，在HotSpot虚拟机中，新生代被划分为三块，一块较大的Eden空间和两块较小的Survivor空间，默认比例为8：1：1，老年代的GC评率相对较低，永久代一般存放类信息等（其实就是方法区的实现）如图所示：

![image-20230306165311823](https://s2.loli.net/2023/03/06/OZrKbUm39lfaAgv.png)

那么它是如何运作的呢？

首先，所有新创建的对象，在一开始都会进入到新生代的Eden区（如果是大对象会被直接丢进老年代），在进行新生代区域的垃圾回收时，首先会对所有新生代区域的对象进行扫描，并回收那些不再使用对象：

![image-20230306165326642](https://s2.loli.net/2023/03/06/lENKm3CMuxk1JFI.png)

接着，在一次垃圾回收之后，Eden区域没有被回收的对象，会进入到Survivor区。在一开始From和To都是空的，而GC之后，所有Eden区域存活的对象都会直接被放入到From区，最后From和To会发生一次交换，也就是说目前存放我们对象的From区，变为To区，而To区变为From区：

![image-20230306165336145](https://s2.loli.net/2023/03/06/xU6LY3nDukNg2rv.png)

接着就是下一次垃圾回收了，操作与上面是一样的，不过这时由于我们From区域中已经存在对象了，所以，在Eden区的存活对象复制到From区之后，所有To区域中的对象会进行年龄判定（每经历一轮GC年龄`+1`，如果对象的年龄大于`默认值为15`，那么会直接进入到老年代，否则移动到From区）

![image-20230306165345563](https://s2.loli.net/2023/03/06/NeGcYPwDOkdjVfq.png)

最后像上面一样交换To区和From区，之后不断重复以上步骤。

而垃圾收集也分为：

* Minor GC   -   次要垃圾回收，主要进行新生代区域的垃圾收集。
  * 触发条件：新生代的Eden区容量已满时。
* Major GC   -   主要垃圾回收，主要进行老年代的垃圾收集。
* Full GC      -    完全垃圾回收，对整个Java堆内存和方法区进行垃圾回收。
  * 触发条件1：每次晋升到老年代的对象平均大小大于老年代剩余空间
  * 触发条件2：Minor GC后存活的对象超过了老年代剩余空间
  * 触发条件3：永久代内存不足（JDK8之前）
  * 触发条件4：手动调用`System.gc()`方法

我们可以添加启动参数来查看JVM的GC日志：

![image-20230306165355706](https://s2.loli.net/2023/03/06/iWqvCUySGk8Qlad.png)

```java
public class Main {
    public static void main(String[] args) {
        Object o = new Object();
        o = null;
        System.gc();
    }
}
```

```
[GC (System.gc()) [PSYoungGen: 2621K->528K(76288K)] 2621K->528K(251392K), 0.0006874 secs] [Times: user=0.01 sys=0.01, real=0.00 secs] 
[Full GC (System.gc()) [PSYoungGen: 528K->0K(76288K)] [ParOldGen: 0K->332K(175104K)] 528K->332K(251392K), [Metaspace: 3073K->3073K(1056768K)], 0.0022693 secs] [Times: user=0.00 sys=0.00, real=0.00 secs] 
Heap
 PSYoungGen      total 76288K, used 3277K [0x000000076ab00000, 0x0000000770000000, 0x00000007c0000000)
  eden space 65536K, 5% used [0x000000076ab00000,0x000000076ae334d8,0x000000076eb00000)
  from space 10752K, 0% used [0x000000076eb00000,0x000000076eb00000,0x000000076f580000)
  to   space 10752K, 0% used [0x000000076f580000,0x000000076f580000,0x0000000770000000)
 ParOldGen       total 175104K, used 332K [0x00000006c0000000, 0x00000006cab00000, 0x000000076ab00000)
  object space 175104K, 0% used [0x00000006c0000000,0x00000006c00532d8,0x00000006cab00000)
 Metaspace       used 3096K, capacity 4496K, committed 4864K, reserved 1056768K
  class space    used 333K, capacity 388K, committed 512K, reserved 1048576K

```

现在我们还只能大致看懂GC日志，不过在学习完成本章全部内容后，我们就可以轻松阅读了。

#### 空间分配担保

我们可以思考一下，有没有这样一种极端情况（正常情况下新生代的回收率是很高的，所以说不用太担心会经常出现这种问题），在一次GC后，新生代Eden区仍然存在大量的对象（因为GC之后存活对象会进入到一个Survivor区，但是很明显这时已经超出Survivor区的容量了，肯定是装不下的）那么现在该怎么办？

这时就需要用到空间分配担保机制了，可以把Survivor区无法容纳的对象直接送到老年代，让老年代进行分配担保（当然老年代也得装得下才行）在现实生活中，贷款会指定担保人，就是当借款人还不起钱的时候由担保人来还钱。

当新生代无法容纳更多的的对象时，可以把新生代中的对象移动到老年代中，这样新生代就腾出了空间来容纳更多的对象。

好，那既然新生代装不下就丢给老年代，那么要是老年代也装不下新生代的数据呢？这时，老年代肯定担保人是当不成了，那么这样的话，首先会判断一下之前的每次垃圾回收进入老年代的平均大小是否小于当前老年代的剩余空间，如果小于，那么说明也许可以放得下（不过也仅仅是也许，依然有可能放不下，因为判断的实际上只是平均值，万一这一次突然非常大呢），否则，会先来一次Full GC，进行一次大规模垃圾回收，来尝试腾出空间，再次判断老年代是否有空间存放，要是还是装不下，直接抛出OOM错误，摆烂。

最后，我们来总结一下一次Minor GC的整个过程：

![image-20230306165425918](https://s2.loli.net/2023/03/06/71PqlEfiGgBSLae.png)

***

#### 标记-清除算法

前面我们已经了解了整个堆内存实际上是以分代收集机制为主，但是依然没有讲到具体的收集过程，那么，具体的回收过程又是什么样的呢？首先我们来了解一下最古老的`标记-清除`算法。

首先标记出所有需要回收的对象，然后再依次回收掉被标记的对象，或是标记出所有不需要回收的对象，只回收未标记的对象。实际上这种算法是非常基础的，并且最易于理解的（这里对象我就以一个方框代替了，当然实际上存放是我们前说到的GC Roots形式）

![image-20230306165444019](https://s2.loli.net/2023/03/06/d8anmBcLrW3iyJl.png)

虽然此方法非常简单，但是缺点也是非常明显的 ，首先如果内存中存在大量的对象，那么可能就会存在大量的标记，并且大规模进行清除。并且一次标记清除之后，连续的内存空间可能会出现许许多多的空隙，碎片化会导致连续内存空间利用率降低。

#### 标记-复制算法

既然标记清除算法在面对大量对象时效率低，那么我们可以采用标记-复制算法。它将容量分为同样大小的两块区域，

标记复制算法，实际上就是将内存区域划分为大小相同的两块区域，每次只使用其中的一块区域，每次垃圾回收结束后，将所有存活的对象全部复制到另一块区域中，并一次性清空当前区域。虽然浪费了一些时间进行复制操作，但是这样能够很好地解决对象大面积回收后空间碎片化严重的问题。

![image-20230306165458671](https://s2.loli.net/2023/03/06/JeifGj6kmVAY2PR.png)

这种算法就非常适用于新生代（因为新生代的回收效率极高，一般不会留下太多的对象）的垃圾回收，而我们之前所说的新生代Survivor区其实就是这个思路，包括8:1:1的比例也正是为了对标记复制算法进行优化而采取的。

#### 标记-整理算法

虽然标记-复制算法能够很好地应对新生代高回收率的场景，但是放到老年代，它就显得很鸡肋了。我们知道，一般长期都回收不到的对象，才有机会进入到老年代，所以老年代一般都是些钉子户，可能一次GC后，仍然存留很多对象。而标记复制算法会在GC后完整复制整个区域内容，并且会折损50%的区域，显然这并不适用于老年代。

那么我们能否这样，在标记所有待回收对象之后，不急着去进行回收操作，而是将所有待回收的对象整齐排列在一段内存空间中，而需要回收的对象全部往后丢，这样，前半部分的所有对象都是无需进行回收的，而后半部分直接一次性清除即可。

![image-20230306165514101](https://s2.loli.net/2023/03/06/4UncSVvO1P2xWKL.png)

虽然这样能保证内存空间充分使用，并且也没有标记复制算法那么繁杂，但是缺点也是显而易见的，它的效率比前两者都低。甚至，由于需要修改对象在内存中的位置，此时程序必须要暂停才可以，在极端情况下，可能会导致整个程序发生停顿（被称为“Stop The World”）。

所以，我们可以将标记清除算法和标记整理算法混合使用，在内存空间还不是很凌乱的时候，采用标记清除算法其实是没有多大问题的，当内存空间凌乱到一定程度后，我们可以进行一次标记整理算法。

***

### 垃圾收集器实现

聊完了对象存活判定和垃圾回收算法，接着我们就要看看具体有哪些垃圾回收器的实现了。我们可以自由地为新生代和老年代选择更适合它们的收集器。

#### Serial收集器

这款垃圾收集器也是元老级别的收集器了，在JDK1.3.1之前，是虚拟机新生代区域收集器的唯一选择。这是一款单线程的垃圾收集器，也就是说，当开始进行垃圾回收时，需要暂停所有的线程，直到垃圾收集工作结束。它的新生代收集算法采用的是标记复制算法，老年代采用的是标记整理算法。

![image-20230306165527009](https://s2.loli.net/2023/03/06/v79TEdmyo1njKwl.png)

可以看到，当进入到垃圾回收阶段时，所有的用户线程必须等待GC线程完成工作，就相当于你打一把LOL 40分钟，中途每隔1分钟网络就卡5秒钟，可能这时你正在打团，结果你被物理控制直接在那里站了5秒钟，这确实让人难以接受。

虽然缺点很明显，但是优势也是显而易见的：

1. 设计简单而高效。
2. 在用户的桌面应用场景中，内存一般不大，可以在较短时间内完成垃圾收集，只要不频繁发生，使用串行回收器是可以接受的。

所以，在客户端模式（一般用于一些桌面级图形化界面应用程序）下的新生代中，默认垃圾收集器至今依然是Serial收集器。我们可以在`java -version`中查看默认的客户端模式：

```
openjdk version "1.8.0_322"
OpenJDK Runtime Environment (Zulu 8.60.0.21-CA-macos-aarch64) (build 1.8.0_322-b06)
OpenJDK 64-Bit Server VM (Zulu 8.60.0.21-CA-macos-aarch64) (build 25.322-b06, mixed mode)
```

我们可以在jvm.cfg文件中切换JRE为Server VM或是Client VM，默认路径为：

```
JDK安装目录/jre/lib/jvm.cfg
```

比如我们需要将当前模式切换为客户端模式，那么我们可以这样编辑：

```
-client KNOWN
-server IGNORE
```

#### ParNew收集器

这款垃圾收集器相当于是Serial收集器的多线程版本，它能够支持多线程垃圾收集：

![image-20230306165542516](https://s2.loli.net/2023/03/06/tQwzAK1XU5gdl8Y.png)

除了多线程支持以外，其他内容基本与Serial收集器一致，并且目前某些JVM默认的服务端模式新生代收集器就是使用的ParNew收集器。

#### Parallel Scavenge/Parallel Old收集器

Parallel Scavenge同样是一款面向新生代的垃圾收集器，同样采用标记复制算法实现，在JDK6时也推出了其老年代收集器Parallel Old，采用标记整理算法实现：

![image-20230306165555265](https://s2.loli.net/2023/03/06/npiZY1Q4e5EhGuB.png)

与ParNew收集器不同的是，它会自动衡量一个吞吐量，并根据吞吐量来决定每次垃圾回收的时间，这种自适应机制，能够很好地权衡当前机器的性能，根据性能选择最优方案。

目前JDK8采用的就是这种 Parallel Scavenge + Parallel Old 的垃圾回收方案。

#### CMS收集器

在JDK1.5，HotSpot推出了一款在强交互应用中几乎可认为有划时代意义的垃圾收集器：CMS（Concurrent-Mark-Sweep）收集器，这款收集器是HotSpot虚拟机中第一款真正意义上的并发（注意这里的并发和之前的并行是有区别的，并发可以理解为同时运行用户线程和GC线程，而并行可以理解为多条GC线程同时工作）收集器，它第一次实现了让垃圾收集线程与用户线程同时工作。

它主要采用标记清除算法：

![image-20230306165610810](https://s2.loli.net/2023/03/06/qhmndIQPKarvDso.png)

它的垃圾回收分为4个阶段：

* 初始标记（需要暂停用户线程）：这个阶段的主要任务仅仅只是标记出GC Roots能直接关联到的对象，速度比较快，不用担心会停顿太长时间。
* 并发标记：从GC Roots的直接关联对象开始遍历整个对象图的过程，这个过程耗时较长但是不需要停顿用户线程，可以与垃圾收集线程一起并发运行。
* 重新标记（需要暂停用户线程）：由于并发标记阶段可能某些用户线程会导致标记产生变得，因此这里需要再次暂停所有线程进行并行标记，这个时间会比初始标记时间长一丢丢。
* 并发清除：最后就可以直接将所有标记好的无用对象进行删除，因为这些对象程序中也用不到了，所以可以与用户线程并发运行。

虽然它的优点非常之大，但是缺点也是显而易见的，我们之前说过，标记清除算法会产生大量的内存碎片，导致可用连续空间逐渐变少，长期这样下来，会有更高的概率触发Full GC，并且在与用户线程并发执行的情况下，也会占用一部分的系统资源，导致用户线程的运行速度一定程度上减慢。

不过，如果你希望的是最低的GC停顿时间，这款垃圾收集器无疑是最佳选择，不过自从G1收集器问世之后，CMS收集器不再推荐使用了。

#### Garbage First (G1) 收集器

此垃圾收集器也是一款划时代的垃圾收集器，在JDK7的时候正式走上历史舞台，它是一款主要面向于服务端的垃圾收集器，并且在JDK9时，取代了JDK8默认的 Parallel Scavenge + Parallel Old 的回收方案。

我们知道，我们的垃圾回收分为`Minor GC`、`Major GC `和`Full GC`，它们分别对应的是新生代，老年代和整个堆内存的垃圾回收，而G1收集器巧妙地绕过了这些约定，它将整个Java堆划分成`2048`个大小相同的独立`Region`块，每个`Region块`的大小根据堆空间的实际大小而定，整体被控制在1MB到32MB之间，且都为2的N次幂。所有的`Region`大小相同，且在JVM的整个生命周期内不会发生改变。

那么分出这些`Region`有什么意义呢？每一个`Region`都可以根据需要，自由决定扮演哪个角色（Eden、Survivor和老年代），收集器会根据对应的角色采用不同的回收策略。此外，G1收集器还存在一个Humongous区域，它专门用于存放大对象（一般认为大小超过了Region容量一半的对象为大对象）这样，新生代、老年代在物理上，不再是一个连续的内存区域，而是到处分布的。

![image-20230306165629129](https://s2.loli.net/2023/03/06/yHxXZP92DKc5Qre.png)

它的回收过程与CMS大体类似：

![image-20230306165641872](https://s2.loli.net/2023/03/06/kXRMlt5iFDvjq8y.png)

分为以下四个步骤：

* 初始标记（暂停用户线程）：仅仅只是标记一下GC Roots能直接关联到的对象，并且修改TAMS指针的值，让下一阶段用户线程并发运行时，能正确地在可用的Region中分配新对象。这个阶段需要停顿线程，但耗时很短，而且是借用进行Minor GC的时候同步完成的，所以G1收集器在这个阶段实际并没有额外的停顿。
* 并发标记：从GC Root开始对堆中对象进行可达性分析，递归扫描整个堆里的对象图，找出要回收的对象，这阶段耗时较长，但可与用户程序并发执行。
* 最终标记（暂停用户线程）：对用户线程做一个短暂的暂停，用于处理并发标记阶段漏标的那部分对象。
* 筛选回收：负责更新Region的统计数据，对各个Region的回收价值和成本进行排序，根据用户所期望的停顿时间来制定回收计划，可以自由选择任意多个Region构成回收集，然后把决定回收的那一部分Region的存活对象复制到空的Region中，再清理掉整个旧Region的全部空间。这里的操作涉及存活对象的移动，是必须暂停用户线程，由多个收集器线程并行完成的。

***

### 元空间

JDK8之前，Hotspot虚拟机的方法区实际上是永久代实现的。在JDK8之后，Hotspot虚拟机不再使用永久代，而是采用了全新的元空间。类的元信息被存储在元空间中。元空间没有使用堆内存，而是与堆不相连的本地内存区域。所以，理论上系统可以使用的内存有多大，元空间就有多大，所以不会出现永久代存在时的内存溢出问题。这项改造也是有必要的，永久代的调优是很困难的，虽然可以设置永久代的大小，但是很难确定一个合适的大小，因为其中的影响因素很多，比如类数量的多少、常量数量的多少等。

![image-20230306165703340](https://s2.loli.net/2023/03/06/2RD3AnPvbh1lQ5N.png)

因此在JDK8时直接将本地内存作为元空间（**Metaspace**）的区域，物理内存有多大，元空间内存就可以有多大，这样永久代的空间分配问题就讲解了，所以最终它变成了这样：

![image-20230306165714662](https://s2.loli.net/2023/03/06/iVcYMU9jdn2NC6Z.png)

到此，我们对于JVM内存区域的讲解就基本完成了。

***

### 其他引用类型

最后，我们来介绍一下其他引用类型。

我们知道，在Java中，如果变量是一个对象类型的，那么它实际上存放的是对象的引用，但是如果是一个基本类型，那么存放的就是基本类型的值。实际上我们平时代码中类似于`Object o = new Object()`这样的的引用类型，细分之后可以称为`强引用`。

我们通过前面的学习可以明确，如果方法中存在这样的`强引用`类型，现在需要回收强引用所指向的对象，那么要么此方法运行结束，要么引用连接断开，否则被引用的对象是无法被判定为可回收的，因为我们说不定后面还要使用它。

所以，当JVM内存空间不足时，JVM宁愿抛出OutOfMemoryError使程序异常终止，也不会靠随意回收具有强引用的“存活”对象来解决内存不足的问题。

除了强引用之外，Java也为我们提供了三种额外的引用类型。

#### 软引用

软引用不像强引用那样不可回收，当 JVM 认为内存不足时，会去试图回收软引用指向的对象，即JVM 会确保在抛出 OutOfMemoryError 之前，清理软引用指向的对象。当然，如果内存充足，那么是不会轻易被回收的。

我们可以通过以下方式来创建一个软引用：

```java
public class Main {
    public static void main(String[] args) {
        //强引用写法：Object obj = new Object();
        //软引用写法：
        SoftReference<Object> reference = new SoftReference<>(new Object());
        //使用get方法就可以获取到软引用所指向的对象了
        System.out.println(reference.get());
    }
}
```

可以看到软引用还存在一个带队列的构造方法，软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被垃圾回收器回收，Java虚拟机就会把这个软引用加入到与之关联的引用队列中。

这里我们来进行一个测试，首先我们需要设定一下参数，来限制最大堆内存为10M，并且打印GC日志：

```
-XX:+PrintGCDetails -Xms10M -Xmx10M
```

接着运行以下代码：

```java
public class Main {
    public static void main(String[] args) {
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        SoftReference<Object> reference = new SoftReference<>(new Object(), queue);
        System.out.println(reference);

        try{
            List<String> list = new ArrayList<>();
            while (true) list.add(new String("lbwnb"));
        }catch (Throwable t){
            System.out.println("发生了内存溢出！"+t.getMessage());
            System.out.println("软引用对象："+reference.get());
            System.out.println(queue.poll());
        }
    }
}
```

运行结果如下：

```
java.lang.ref.SoftReference@232204a1
[GC (Allocation Failure) [PSYoungGen: 3943K->501K(4608K)] 3943K->2362K(15872K), 0.0050615 secs] [Times: user=0.01 sys=0.00, real=0.01 secs] 
[GC (Allocation Failure) [PSYoungGen: 3714K->496K(4608K)] 5574K->4829K(15872K), 0.0049642 secs] [Times: user=0.03 sys=0.00, real=0.01 secs] 
[GC (Allocation Failure) [PSYoungGen: 3318K->512K(4608K)] 7652K->7711K(15872K), 0.0059440 secs] [Times: user=0.03 sys=0.00, real=0.00 secs] 
[GC (Allocation Failure) --[PSYoungGen: 4608K->4608K(4608K)] 11807K->15870K(15872K), 0.0078912 secs] [Times: user=0.05 sys=0.00, real=0.01 secs] 
[Full GC (Ergonomics) [PSYoungGen: 4608K->0K(4608K)] [ParOldGen: 11262K->10104K(11264K)] 15870K->10104K(15872K), [Metaspace: 3207K->3207K(1056768K)], 0.0587856 secs] [Times: user=0.24 sys=0.00, real=0.06 secs] 
[Full GC (Ergonomics) [PSYoungGen: 4096K->1535K(4608K)] [ParOldGen: 10104K->11242K(11264K)] 14200K->12777K(15872K), [Metaspace: 3207K->3207K(1056768K)], 0.0608198 secs] [Times: user=0.25 sys=0.01, real=0.06 secs] 
[Full GC (Ergonomics) [PSYoungGen: 3965K->3896K(4608K)] [ParOldGen: 11242K->11242K(11264K)] 15207K->15138K(15872K), [Metaspace: 3207K->3207K(1056768K)], 0.0972088 secs] [Times: user=0.58 sys=0.00, real=0.10 secs] 
[Full GC (Allocation Failure) [PSYoungGen: 3896K->3896K(4608K)] [ParOldGen: 11242K->11225K(11264K)] 15138K->15121K(15872K), [Metaspace: 3207K->3207K(1056768K)], 0.1028222 secs] [Times: user=0.63 sys=0.01, real=0.10 secs] 
发生了内存溢出！Java heap space
软引用对象：null
java.lang.ref.SoftReference@232204a1
Heap
 PSYoungGen      total 4608K, used 4048K [0x00000007bfb00000, 0x00000007c0000000, 0x00000007c0000000)
  eden space 4096K, 98% used [0x00000007bfb00000,0x00000007bfef40a8,0x00000007bff00000)
  from space 512K, 0% used [0x00000007bff00000,0x00000007bff00000,0x00000007bff80000)
  to   space 512K, 0% used [0x00000007bff80000,0x00000007bff80000,0x00000007c0000000)
 ParOldGen       total 11264K, used 11225K [0x00000007bf000000, 0x00000007bfb00000, 0x00000007bfb00000)
  object space 11264K, 99% used [0x00000007bf000000,0x00000007bfaf64a8,0x00000007bfb00000)
 Metaspace       used 3216K, capacity 4500K, committed 4864K, reserved 1056768K
  class space    used 352K, capacity 388K, committed 512K, reserved 1048576K
```

可以看到，当内存不足时，软引用所指向的对象被回收了，所以`get()`方法得到的结果为null，并且软引用对象本身被丢进了队列中。

#### 弱引用

弱引用比软引用的生命周期还要短，在进行垃圾回收时，不管当前内存空间是否充足，都会回收它的内存。

我们可以像这样创建一个弱引用：

```java
public class Main {
    public static void main(String[] args) {
        WeakReference<Object> reference = new WeakReference<>(new Object());
        System.out.println(reference.get());
    }
}
```

使用方法和软引用是差不多的，但是如果我们在这之前手动进行一次GC：

```java
public class Main {
    public static void main(String[] args) {
        SoftReference<Object> softReference = new SoftReference<>(new Object());
        WeakReference<Object> weakReference = new WeakReference<>(new Object());

        //手动GC
        System.gc();

        System.out.println("软引用对象："+softReference.get());
        System.out.println("弱引用对象："+weakReference.get());
    }
}
```

可以看到，弱引用对象直接就被回收了，而软引用对象没有被回收。同样的，它也支持ReferenceQueue，和软引用用法一致，这里就不多做介绍了。

`WeakHashMap`正是一种类似于弱引用的HashMap类，如果Map中的Key没有其他引用那么此Map会自动丢弃此键值对。

```java
public class Main {
    public static void main(String[] args) {
        Integer a = new Integer(1);

        WeakHashMap<Integer, String> weakHashMap = new WeakHashMap<>();
        weakHashMap.put(a, "yyds");
        System.out.println(weakHashMap);

        a = null;
        System.gc();
        
        System.out.println(weakHashMap);
    }
}
```

可以看到，当变量a的引用断开后，这时只有WeakHashMap本身对此对象存在引用，所以在GC之后，这个键值对就自动被舍弃了。所以说这玩意，就挺适合拿去做缓存的。

#### 虚引用（鬼引用）

虚引用相当于没有引用，随时都有可能会被回收。

看看它的源码，非常简单：

```java
public class PhantomReference<T> extends Reference<T> {

    /**
     * Returns this reference object's referent.  Because the referent of a
     * phantom reference is always inaccessible, this method always returns
     * <code>null</code>.
     *
     * @return  <code>null</code>
     */
    public T get() {
        return null;
    }

    /**
     * Creates a new phantom reference that refers to the given object and
     * is registered with the given queue.
     *
     * <p> It is possible to create a phantom reference with a <tt>null</tt>
     * queue, but such a reference is completely useless: Its <tt>get</tt>
     * method will always return null and, since it does not have a queue, it
     * will never be enqueued.
     *
     * @param referent the object the new phantom reference will refer to
     * @param q the queue with which the reference is to be registered,
     *          or <tt>null</tt> if registration is not required
     */
    public PhantomReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

}
```

也就是说我们无论调用多少次`get()`方法得到的永远都是`null`，因为虚引用本身就不算是个引用，相当于这个对象不存在任何引用，并且只能使用带队列的构造方法，以便对象被回收时接到通知。

最后，Java中4种引用的级别由高到低依次为：  **强引用  >  软引用  >  弱引用  >  虚引用**

————————————————
版权声明：本文为柏码知识库版权所有，禁止一切未经授权的转载、发布、出售等行为，违者将被追究法律责任。
原文链接：https://www.itbaima.cn/document/ydd7n3jg8unc3clg