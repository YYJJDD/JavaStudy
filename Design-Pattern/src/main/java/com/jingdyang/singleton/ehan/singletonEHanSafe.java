package com.jingdyang.singleton.ehan;

/**
 * 饿汉式-线程安全
 * 只需要对 getInstance() 方法加锁，那么在一个时间点只能有一个线程能够进入该方法，从而避免了多次实例化的问题。
 * 但是当一个线程进入该方法之后，其它试图进入该方法的线程都必须等待，因此性能上有一定的损耗。
 * https://pdai.tech/md/dev-spec/pattern/2_singleton.html
 */
public class singletonEHanSafe {

    private static singletonEHanSafe instance = new singletonEHanSafe();

    public static synchronized singletonEHanSafe getInstance() {
        if (instance == null) {
            instance = new singletonEHanSafe();
        }
        return instance;
    }
}
