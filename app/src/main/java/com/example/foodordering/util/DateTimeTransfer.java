package com.example.foodordering.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xch on 2017/5/29.
 */

public class DateTimeTransfer {

    /**
     * 调用此方法输入所要转换的时间戳输入例如（1402733340）输出（"16:09:00"）
     *
     * @param time
     * @return
     */
    public static String GetTimeFromLong(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("HH:mm:ss");
        @SuppressWarnings("unused")
        long i = Long.parseLong(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    /**
     * long变成string型
     * @param mills
     * @return
     */
    public static String GetDateFromLong(String mills) {
        long mile=Long.parseLong(mills);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(mile* 1000L);
        String stringDate = simpleDateFormat.format(date);
        return stringDate;
    }
}
