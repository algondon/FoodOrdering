package com.example.foodordering.weather.util;

/**
 * Created by xch on 2018/3/10.
 */
public class TextUtil {
    public static String getFormatArea(String areaName) {
        if (areaName.length() > 2 && areaName.contains("县")) {
            areaName = areaName.replace("县", "");
        } else if (areaName.contains("市")) {
            areaName = areaName.replace("市", "");
        }
        return areaName;
    }
}
