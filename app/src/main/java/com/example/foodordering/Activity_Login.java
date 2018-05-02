package com.example.foodordering;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.control.ActivityCollector;
import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Status;
import com.example.foodordering.tools.VerificationCode;
import com.example.foodordering.util.BottomMenu;
import com.example.foodordering.service.PostUtility;
import com.example.foodordering.util.Util;
import com.google.gson.Gson;

public class Activity_Login extends BaseActivity implements View.OnClickListener {
    private Handler loginHandler = new Handler();
    private Button btn_Exchange;
    private ImageView Image_Code;
    private ProgressDialog progress;
    private BottomMenu menuWindow;
    private TextView other_login, tv_register, tv_forget_password;
    private Button login;
    private EditText etUserName, etPwd, VF_Code;
    private String userName, pwd;

    private int statusCode = 0;
    String postForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        other_login.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        login.setOnClickListener(this);
        tv_forget_password.setOnClickListener(this);
    }

    private void initView() {
        etPwd = (EditText) findViewById(R.id.etPassword);
        etUserName = (EditText) findViewById(R.id.etUserName);
        other_login = (TextView) findViewById(R.id.other_login);
        tv_register = (TextView) findViewById(R.id.register);
        tv_forget_password = (TextView) findViewById(R.id.tv_forget_password);
        login = (Button) findViewById(R.id.login);
        VF_Code = (EditText) findViewById(R.id.Code);
        Image_Code = (ImageView) findViewById(R.id.Image_Code);
        btn_Exchange = (Button) findViewById(R.id.Exchange);
        btn_Exchange.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        Image_Code.setImageBitmap(VerificationCode.getVerificationCode());

        btn_Exchange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Image_Code.setImageBitmap(VerificationCode
                        .getVerificationCode());
            }
        });

        Image_Code.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Image_Code.setImageBitmap(VerificationCode
                        .getVerificationCode());
            }
        });


        SharedPreferences preferences = getSharedPreferences("LoginInfo", Activity.MODE_PRIVATE);
        String phone = preferences.getString("phoneNumber", "");
        String pwd = preferences.getString("password", "");
        if (phone != null && !phone.isEmpty()) {
            etUserName.setText(phone);
        }
        if (pwd != null && !pwd.isEmpty()) {
            etPwd.setText(pwd);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_login:
                menuWindow = new BottomMenu(Activity_Login.this, clickListener);
                menuWindow.show();
                break;
            case R.id.register:
                Intent intent = new Intent(Activity_Login.this, Activity_Register.class);
                startActivity(intent);
                break;
            case R.id.tv_forget_password:
                Util.showToast(getApplication(), "敬请期待...");
                break;
            case R.id.login:
                progress = ProgressDialog.show(this, "请稍候", "正在登录...", true);
                progress.setCancelable(false);
                if (VerificationCode.checkVerificationCode(VF_Code.getText()
                        .toString())) {
                    userName = etUserName.getText().toString();
                    pwd = etPwd.getText().toString();
                    if (!Util.checkNetwork(this)) {
                        progress.dismiss();
                        return;
                    }
                    if (!"".equals(userName) && !"".equals(pwd)) {
                        login();
                    } else if (userName.toString().length() == 0) {
                        progress.dismiss();
                        Util.showToast(Activity_Login.this, "您的手机号码不能为空，请注意输入!");
                        return;
                    } else if (pwd.toString().length() == 0) {
                        progress.dismiss();
                        Util.showToast(Activity_Login.this, "您的密码不能为空，请注意输入!");
                        return;
                    } else if (pwd.toString().length() < 6) {
                        progress.dismiss();
                        Util.showToast(Activity_Login.this, "您的密码位数不能少于6位");
                        return;
                    } else if (pwd.toString().length() > 16) {
                        progress.dismiss();
                        Util.showToast(Activity_Login.this, "您的密码位数不能多于16位");
                        return;
                    } else {
                        progress.dismiss();
                        Util.showToast(Activity_Login.this, "您的账户和密码不能为空，请注意输入!");
                        return;
                    }
                } else {
                    progress.dismiss();
                    Toast.makeText(Activity_Login.this, "验证码错误，请重新输入！",
                            Toast.LENGTH_LONG).show();
                    Image_Code.setImageBitmap(VerificationCode
                            .getVerificationCode());
                    VF_Code.setText("");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        new Thread() {
            public void run() {
                try {
                    postForm = PostUtility.postLogin(userName, pwd);
                    loginHandler.post(runnableLogin);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    private Runnable runnableLogin = new Runnable() {

        @Override
        public void run() {
            Gson gson = new Gson();
            if (postForm == null || postForm.isEmpty() || postForm.length() > 100) {
                Util.showToast(Activity_Login.this, "服务器出错，正在收集错误信息！");
                progress.dismiss();
                return;
            } else {
                Status status = gson.fromJson(postForm, Status.class);
                statusCode = status.getStatusCode();
                if (statusCode == 200) {
                    //保存用户登录信息到本地
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editors = sharedPreferences.edit();//获取编辑器
                    editors.putString("phoneNumber", userName);
                    editors.putString("password", pwd);
                    editors.putInt("statusCode", statusCode);
                    editors.commit();//提交修改
                    Activity_Main.actionStart(Activity_Login.this, userName, statusCode);
                }
                Util.showToast(Activity_Login.this, status.getStatusDescription());
            }
            progress.dismiss();
        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_wxLogin://微信登录
                    Util.showToast(getApplication(), "敬请期待...");
                    break;
                case R.id.btn_msgCheckLogin://短信验证码登录
                    Util.showToast(getApplication(), "敬请期待...");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            ActivityCollector.finishAll();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
