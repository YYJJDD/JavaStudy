package com.jingdyang.singleton.lanhan;

/**
 * 懒汉式-线程安全（在getInstance方法上加锁）
 * 只需要对 getInstance() 方法加锁，那么在一个时间点只能有一个线程能够进入该方法，从而避免了多次实例化 instance 的问题。
 * 但是当一个线程进入该方法之后，其它试图进入该方法的线程都必须等待，因此性能上有一定的损耗。
 * 原文链接：https://pdai.tech/md/dev-spec/pattern/2_singleton.html
 */
public class singletonLanHanSafe {
    private static singletonLanHanSafe instance;

    // 私有构造方法
    private singletonLanHanSafe() {
    }

    public static synchronized singletonLanHanSafe getInstance() {
        if (instance == null) {
            instance = new singletonLanHanSafe();
        }
        return instance;
    }

}
