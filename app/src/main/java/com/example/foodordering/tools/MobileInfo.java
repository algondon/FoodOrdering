package com.example.foodordering.tools;

import android.annotation.SuppressLint;
import android.content.*;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class MobileInfo {
    @SuppressLint("MissingPermission")
    public static String GetIMSI(Context context) {
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String IMSI = mTelephonyMgr.getSubscriberId() + "";
            if (IMSI.equals("") || IMSI.equals("null")) {// 获取不到IMSI返回串码
                IMSI = GetIMEI(context);
            }
            return IMSI;
        } catch (Exception e) {
            // TODO: handle exception

            return "";

        }

    }

    @SuppressLint("MissingPermission")
    public static String GetIMEI(Context context) {
        String IMEI;
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = mTelephonyMgr.getDeviceId() + "";
            if (IMEI != null && IMEI.equals("null")) {

                IMEI = Secure.getString(context.getContentResolver(),
                        Secure.ANDROID_ID);
            }
            return IMEI;
        } catch (Exception e) {
            // TODO: handle exception

            return "";
        }

    }

    public static String GetMacAddress(Context context) {
        try {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch (Exception e) {
            return "";
        }

    }
}
