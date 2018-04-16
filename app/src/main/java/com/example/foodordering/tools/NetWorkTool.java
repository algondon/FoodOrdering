package com.example.foodordering.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import com.example.foodordering.R;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


@SuppressWarnings("deprecation")
public class NetWorkTool {

    private ConnectivityManager connectivityManager;
    private Context context;

    public NetWorkTool(ConnectivityManager connectivityManager, Context context) {
        this.connectivityManager = connectivityManager;
        this.context = context;
    }

    public boolean isNewworkConnected() {
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
//		if (networkInfo[0].isConnected()) {
//			return true;
//		}
//		if (networkInfo[1].isConnected()) {
//			return true;
//		}
//		if (networkInfo[0].toString().contains("UNKNO")) {
//			return true;
//		}
        for (int i = 0; i < networkInfo.length; i++) {
            if (networkInfo[i].isConnected()) {
                return true;
            }
        }
        return false;
    }

    public int getNetworkConnectionType() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return 0;
        }
        int type = networkInfo.getType();
        switch (type) {
            case ConnectivityManager.TYPE_MOBILE: {
                if (networkInfo.getExtraInfo().contains("cmnet")) {
                    // 3G网络连接
                    return 1;
                }
                if (networkInfo.getExtraInfo().contains("cmwap")) {
                    // 2G网络连接
                    return 2;
                }
            }
            case ConnectivityManager.TYPE_WIFI: {
                // WIFI连接
                return 3;
            }
            default: {
                // 未知网络
                return -1;
            }
        }
    }

    public static String getContent(String url) throws Exception {
        StringBuilder sb = new StringBuilder();

        HttpClient client = new DefaultHttpClient();
        HttpParams httpParams = (HttpParams) client.getParams();
        // 设置网络超时参数
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        HttpResponse response = client.execute(new HttpGet(url));
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    entity.getContent(), "GBK"), 8192);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
        }
        return sb.toString();
    }

    public void NetworkClosedWarning(String message) {
        if (!isNewworkConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.logo);
            builder.setTitle("网络提示:");
            builder.setMessage(message);
            builder.setPositiveButton("确定", new OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 更改网络连接设置，默认为打开设置界面
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    // changeWIFIState();
                    // changeMobileDataState();
                }
            });
            builder.setNegativeButton("取消", new OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    System.exit(0);
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }
    }

    public static boolean isNetwork(Context context) {
        boolean isnetwork = false;
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            isnetwork = false;
        } else {
            int type = networkinfo.getType();
            if (type == ConnectivityManager.TYPE_MOBILE
                    || type == ConnectivityManager.TYPE_WIFI) {
                isnetwork = true;

            }
        }
        return isnetwork;
    }

    public void changeWIFIState(boolean state) {
        WifiManager wifi_manager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wifi_manager.setWifiEnabled(state);
    }

    public void changeMobileDataState(boolean state) {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Class<?> ownerClass = mConnectivityManager.getClass();
            Class<?>[] argsClass = new Class[1];
            argsClass[0] = boolean.class;
            Method method = ownerClass.getMethod("setMobileDataEnabled",
                    argsClass);
            method.invoke(mConnectivityManager, state);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("移动数据设置错误: " + e.toString());
        }
    }
}
