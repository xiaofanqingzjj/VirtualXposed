package com.fortune.va

import android.os.Bundle
import com.fortune.va.lib.MenuActivity
import com.fortune.va.proxy.MyInterface
import com.fortune.va.proxy.MyInterfaceStub

class MyTestMenu : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("Dynamic Proxy") {
            MyInterface.test()
        }

        addMenu("MethodInvocationStub") {
            MyInterfaceStub.test();
        }
    }
}