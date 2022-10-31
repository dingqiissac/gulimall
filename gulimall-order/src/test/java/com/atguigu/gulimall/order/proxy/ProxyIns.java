package com.atguigu.gulimall.order.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyIns implements InvocationHandler {

    People people;


    public Object bind(People people) {
        this.people = people;
        return Proxy.newProxyInstance(
                this.people.getClass().getClassLoader(), this.people
                        .getClass().getInterfaces(), this);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        args[0]=100l;

        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        String name = method.getName();
        System.out.println(name);

        Object res = method.invoke(people,args);

        return res;
    }
}
