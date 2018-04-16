package com.example.foodordering;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.foodordering.application.FoodOrderingApplication;
import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.tools.MobileInfo;
import com.example.foodordering.tools.NetWorkTool;

public class Activity_Welcome extends BaseActivity {
    private FoodOrderingApplication application;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String IMSI, IMEI;//国际移动用户识别码 国际移动设备身份码
    private AlertDialog.Builder builder, net_builder;
    private AlertDialog dialog, net_dialog;
    private View view;
    private ImageView image_wifi, image_mobile;
    private ToggleButton toggle_wifi, toggle_mobile;
    private Button button_confirm;
    private NetWorkTool checker;
    private Handler updatehandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        application = (FoodOrderingApplication) getApplication();
        TextView versionNumber = (TextView) findViewById(R.id.versionNumber);
        versionNumber.setText("懒人外卖  " + getLocalVersionName(Activity_Welcome.this));
        application.versionName = getLocalVersionName(Activity_Welcome.this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取本地软件版本号
     */
    public static int getLocalVersion(Context context) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context context) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    private void init() {
        sp = getSharedPreferences("user", MODE_PRIVATE);
        editor = sp.edit();
        IMSI = MobileInfo.GetIMSI(Activity_Welcome.this);
        IMEI = MobileInfo.GetIMEI(Activity_Welcome.this);

        LayoutInflater inflater = (LayoutInflater) Activity_Welcome.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.network_state_changer, null);
        image_wifi = (ImageView) view.findViewById(R.id.image_wifi);
        image_mobile = (ImageView) view.findViewById(R.id.image_mobile);
        toggle_wifi = (ToggleButton) view.findViewById(R.id.toggle_wifi);
        toggle_mobile = (ToggleButton) view.findViewById(R.id.toggle_mobile);
        button_confirm = (Button) view.findViewById(R.id.button_confirm);
        builder = new AlertDialog.Builder(Activity_Welcome.this)
                .setIcon(R.drawable.logo).setTitle("网络连接").setView(view);
        dialog = builder.create();//创建（打开网络或wifi）这个AlertDialog对象
        dialog.setCanceledOnTouchOutside(false);

        checker = new NetWorkTool(
                (ConnectivityManager) Activity_Welcome.this
                        .getSystemService(Context.CONNECTIVITY_SERVICE),
                Activity_Welcome.this);

        toggle_wifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean state) {
                if (state) {
                    image_wifi.setImageResource(R.drawable.wifi_connected);
                } else {
                    image_wifi.setImageResource(R.drawable.wifi_unconnected);
                }
                checker.changeWIFIState(state);
            }
        });

        toggle_mobile.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean state) {
                if (state) {
                    image_mobile
                            .setImageResource(R.drawable.mobile_data_connected);
                } else {
                    image_mobile
                            .setImageResource(R.drawable.mobile_data_unconnected);
                }
                checker.changeMobileDataState(state);
            }
        });

        button_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (dialog != null) {
                    net_dialog.dismiss();
                    dialog.dismiss();
                    NetworkConnectionWarning();
                }
            }
        });

        initNetDialog();
        NetworkConnectionWarning();
    }

    private void initNetDialog() {
        net_builder = new AlertDialog.Builder(Activity_Welcome.this)
                .setIcon(R.drawable.logo).setTitle("网络提示:")
                .setMessage("您的网络连接未打开，请打开网络，否则将导致您无法正常使用！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        dialog.show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Activity_Welcome.this.finish();
                        System.exit(0);
                    }
                });
        net_dialog = net_builder.create();//创建网络警告这个AlertDialog对象
        net_dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 若没有连接网络则弹出提示框，若有网络则不弹出
     */
    private void NetworkConnectionWarning() {
        if (checker.isNewworkConnected()) {
            checkUpdate();
        } else {
            net_dialog.show();
        }
    }

    private void checkUpdate() {
        new Thread() {
            public void run() {
                try {
                    sleep(1000);
                    updatehandler.post(runnableupdate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Runnable runnableupdate = new Runnable() {
        public void run() {
            Intent mainIntent = new Intent(Activity_Welcome.this, Activity_Main.class);
            startActivity(mainIntent);
            finish();
        }
    };

}
