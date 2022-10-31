package com.atguigu.gulimall.order.proxy;

public class Man implements People{
    @Override
    public String work(Long step) {
//
//        for (int i = 0; i < step; i++) {
//            System.out.println(i);
//        }
        return step+"";
    }


    public static void main(String[] args) {
        Man man = new Man();


        ProxyIns proxy = new ProxyIns();
        People bind = (People)proxy.bind(man);

        String work = bind.work(8l);
        System.out.println(work);
    }


}
