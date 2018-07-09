package com.example.foodordering;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.application.FoodOrderingApplication;
import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.tools.dialog.ProgressDialog;

public class Activity_Setting extends BaseActivity implements View.OnClickListener {
    private ProgressDialog pg;
    FoodOrderingApplication application;
    private LinearLayout ly_version_update, ly_feedback;
    private TextView btn_exitLogin, tv_versionName;
    public int statusCode = 0;//登录状态

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 取消对话框
            pg.dismiss();
            switch (msg.what) {
                case 1://版本检查
                    Toast.makeText(Activity_Setting.this, "当前是最新版本了哟", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        application = (FoodOrderingApplication) getApplication();
        pg = new ProgressDialog(Activity_Setting.this);
        initView();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Activity_Setting.class);
        context.startActivity(intent);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("设置");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ly_feedback = (LinearLayout) findViewById(R.id.ly_feedback);
        ly_version_update = (LinearLayout) findViewById(R.id.ly_version_update);
        btn_exitLogin = (TextView) findViewById(R.id.btn_exitLogin);
        tv_versionName = (TextView) findViewById(R.id.tv_versionName);
        btn_exitLogin.setOnClickListener(this);
        ly_feedback.setOnClickListener(this);
        ly_version_update.setOnClickListener(this);
        tv_versionName.setText("当前版本为" + application.versionName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ly_feedback://意见反馈
                Activity_Feedback.actionStart(Activity_Setting.this);
                break;
            case R.id.ly_version_update:
                pg.setMessage("正在检查版本信息...");
                pg.show();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //并没有检查，只是沉睡一秒
                            sleep(10000);
                            Message message = handler.obtainMessage();
                            message.what =1;
                            message.arg1=123;
                            handler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.btn_exitLogin:
                SharedPreferences preferences = getSharedPreferences("LoginInfo", Activity.MODE_PRIVATE);
                statusCode = preferences.getInt("statusCode", -1);
                if (statusCode == 200) {
                    new AlertDialog.Builder(Activity_Setting.this).setTitle("退出")
                            .setMessage("确定要退出登录？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor editors = sharedPreferences.edit();
                                    editors.putInt("statusCode", 0);
                                    editors.commit();//提交修改

                                    Intent intent = new Intent(Activity_Setting.this, Activity_Login.class);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();

                } else {
                    Toast.makeText(Activity_Setting.this, "你还未登录，请先登录!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
