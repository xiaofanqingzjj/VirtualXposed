package com.fortune.va.proxy;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public interface MyInterface {

    static final String TAG = "MyInterface";

    void doSomething();
    String sayHello(String name);



    static void test() {
        // 创建目标对象
        final MyInterface target = new MyInterfaceImpl();



        // 创建代理实例
        MyInterface proxyInstance = (MyInterface) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), // 代理的接口的Class
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 方法调用
                        Log.d(TAG, "invoke proxy obj method proxy:" + proxy + ", method:" + method.getName() + ", args:" + args);
                        Object result = method.invoke(target, args);
                        return result;
                    }
                } // 方法调用处理器
        );

        // 调用代理方法
        proxyInstance.doSomething();
        String greeting = proxyInstance.sayHello("World");
        System.out.println(greeting);
    }
}

class MyInterfaceImpl implements MyInterface {
    @Override
    public void doSomething() {
        System.out.println("Doing something...");
    }

    @Override
    public String sayHello(String name) {
        return "Hello, " + name;
    }
}

