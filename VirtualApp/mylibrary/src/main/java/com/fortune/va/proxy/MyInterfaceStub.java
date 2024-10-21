package com.fortune.va.proxy;

import android.util.Log;

import com.lody.virtual.client.hook.base.MethodInvocationStub;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.stub.StubActivity;

import java.lang.reflect.Method;


/**
 * 为某个对象创建一个代理对象
 *
 * 通过注册MethodProxy来
 */
public class MyInterfaceStub extends MethodInvocationStub<MyInterface> {

    static final String TAG = "MyInterfaceStub";

    public MyInterfaceStub(MyInterface baseInterface, Class<?>... proxyInterfaces) {
        super(baseInterface, proxyInterfaces);
        init();
    }

    public MyInterfaceStub(MyInterface baseInterface) {
        super(baseInterface);
        init();
    }

    void init() {
        // 注册对应接口的方法代理
        addMethodProxy(new DoSomethingProxy());
    }


    static class DoSomethingProxy extends MethodProxy {

        @Override
        public String getMethodName() {
            return "doSomething";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            Log.d(MyInterfaceStub.TAG, "call:" + method.getName());
            return super.call(who, method, args);
        }
    }


    /**
     * test
     */
    public static void test() {
        final MyInterface myInterfaceStub = new MyInterfaceImpl();

        MyInterfaceStub stub = new MyInterfaceStub(myInterfaceStub);


        stub.getProxyInterface().doSomething();

    }
}
