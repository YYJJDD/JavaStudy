package com.jingdyang.singleton.lanhan;

/**
 * 懒汉式-线程安全（Double-Check双检查锁）
 * instance 只需要被实例化一次，之后就可以直接使用了。加锁操作只需要对实例化那部分的代码进行，只有当 instance 没有被实例化时，
 * 才需要进行加锁。双重校验锁先判断 instance 是否已经被实例化，如果没有被实例化，那么才对实例化语句进行加锁。
 * 原文链接：https://pdai.tech/md/dev-spec/pattern/2_singleton.html
 */
public class singletonLanHanSafeDoubleCheck {

    /**
     * instance 采用 volatile 关键字修饰也是很有必要的。instance = new Singleton();
     * 这段代码其实是分为三步执行。
     * 1、分配内存空间；
     * 2、初始化对象；
     * 3、将instance 指向分配的内存地址；
     * 但是由于 JVM 具有指令重排的特性，有可能执行顺序变为了 1>3>2，这在单线程情况下自然是没有问题。
     * 但如果是多线程下，有可能获得是一个还没有被初始化的实例，以致于程序出错：
     * 假假设有两个线程a，b，都去请求我们单例模式下类的实例，a线程在设置了instance指针指向了我们的对象所在的内存空间以后就归还了锁，
     * 线程b这个时候拿到锁以后，检查到对象不为空，直接返回了线程a创建的对象，但是这个时候线程a还没有完成对象的初始化，
     * 所以就导致了线程b拿到的对象是一个空对象，就会出现空指针的问题。
     * <p>
     * 使用 volatile 可以禁止 JVM 的指令重排，保证在多线程环境下也能正常运行。
     */
    private volatile static singletonLanHanSafeDoubleCheck instance;

    // 私有构造方法
    private singletonLanHanSafeDoubleCheck() {
    }

    /**
     * 考虑下面的实现，也就是只使用了一个 if 语句。在 instance == null 的情况下，如果两个线程同时执行 if 语句，
     * 那么两个线程就会同时进入 if 语句块内。虽然在 if 语句块内有加锁操作，但是两个线程都会执行 instance = new Singleton();
     * 这条语句，只是先后的问题，那么就会进行两次实例化，从而产生了两个实例。因此必须使用双重校验锁，也就是需要使用两个 if 语句。
     * if (instance == null) {
     * synchronized (Singleton.class) {
     * instance = new Singleton();
     * }
     * }
     */
    public static singletonLanHanSafeDoubleCheck getInstance() {
        if (instance == null) {
            synchronized (singletonLanHanSafeDoubleCheck.class) {
                if (instance == null) {
                    instance = new singletonLanHanSafeDoubleCheck();
                }
            }
        }
        return instance;
    }

}
