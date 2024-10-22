package com.lody.virtual.helper.utils;

public class DataUtil {

    public static int safeToInt(Object data) {
        if (data instanceof Long) {
            return ((Long) data).intValue();
        } else if (data instanceof Integer) {
            return (int) data;
        }
        return 0;
    }
}
