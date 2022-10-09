package com.atguigu.gulimall.commons.utils;

public class AppUtils {

    public static String splitStringWithSeparator(String [] arr, String sep){
        StringBuffer stringBuffer = new StringBuffer();

        if(arr!=null && arr.length>0){
            for (String val : arr) {
                stringBuffer.append(val);
                stringBuffer.append(sep);
            }
            String s = stringBuffer.toString();
            return stringBuffer.toString().substring(0,s.length()-1);
        }

        return null;
    }
}
