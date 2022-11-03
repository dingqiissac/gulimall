package com.atguigu.gulimall.commons.bean;

public class Constant {

    public static String ES_SPU_INDEX = "productionindex";

    public static String ES_SPU_TYPE = "_doc";

    public static String CACHE_CATELOG = "cache:cateLog";

    public static String CACHE_CATELOG1 = "cache:cateLog1";

    public static String CACHE_CATELOG2 = "cache:cateLog2";

    public static String CACHE_CATELOG3 = "cache:cateLog3";

    public static String LOCK = "lock";

    public static String LOGIN_USER_PREFIX = "login:user:";

    public static Long LOGIN_TIME_OFF = 30L;

    public static  final String CART_PREFIX = "cart:user:";

    public static Long CART_TIMEOUT = 60*24*30L;

    public static String TOKENS = "Order:token:";

    public static Long TOKENS_TIME_OUT = 60*30L;

    public static String STOCK_LOCK = "Stock:Lock:";//+skuId
}
