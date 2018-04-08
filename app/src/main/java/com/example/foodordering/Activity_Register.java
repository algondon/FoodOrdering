package com.example.foodordering;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Status;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.PostUtility;
import com.example.foodordering.util.Util;
import com.google.gson.Gson;

public class Activity_Register extends BaseActivity implements View.OnClickListener {

    private Button btnRegister;
    private RadioGroup radioGroupSex;
    private RadioButton radioButtonSex;
    private EditText etPhoneNumber, etPwd, etRePwd, etUserName, etNickName, etAddress;
    private String userName, pwd, rePwd, PhoneNumber, NickName, Address, Sex;
    private String postForm;
    private Handler handler=new Handler();
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pg=new ProgressDialog(Activity_Register.this);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("注册会员");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        btnRegister = (Button) findViewById(R.id.register);
        btnRegister.setOnClickListener(this);
        radioGroupSex = (RadioGroup) findViewById(R.id.rg_sex);
        radioButtonSex = (RadioButton) findViewById(radioGroupSex.getCheckedRadioButtonId());
        etUserName = (EditText) findViewById(R.id.etUserName);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etNickName = (EditText) findViewById(R.id.etNickName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPwd = (EditText) findViewById(R.id.etPassword);
        etRePwd = (EditText) findViewById(R.id.etRePassword);
        radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                Sex = radioButton.getText().toString();
            }
        });
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                NickName = etNickName.getText().toString();
                PhoneNumber = etPhoneNumber.getText().toString();
                userName = etUserName.getText().toString();
                pwd = etPwd.getText().toString();
                rePwd = etRePwd.getText().toString();
                Address = etAddress.getText().toString();
                if (PhoneNumber.isEmpty() || pwd.isEmpty() || rePwd.isEmpty() || NickName.isEmpty() || Address.isEmpty() || userName.isEmpty()) {
                    Toast.makeText(this, "请将信息填完整！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwd.equals(rePwd)) {
                    Toast.makeText(this, "密码和确认密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Util.checkNetwork(this)) {
                    return;
                }
                if (Sex == null || Sex.isEmpty()) {
                    Sex = "男";
                }

                new Thread(){
                    @Override
                    public void run() {
                        pg.setMessage("正在注册账号信息，请稍后...");
                        pg.show();
                        try {
                            postForm = PostUtility.postRegister(PhoneNumber.trim(), userName, pwd, NickName, Sex, Address);
                            handler.post(runnableRegister);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };

                break;
            default:
                break;
        }
    }
    Runnable runnableRegister=new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            Gson gson = new Gson();
            Status status = gson.fromJson(postForm, Status.class);
            int statusCode = status.getStatusCode();
            if (statusCode == 200) {
                Intent intent = new Intent(Activity_Register.this, Activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            Toast.makeText(Activity_Register.this, status.getStatusDescription(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
