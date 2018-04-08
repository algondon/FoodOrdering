package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Status;
import com.example.foodordering.bean.User;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.util.GetUserData;
import com.example.foodordering.service.PostUtility;
import com.example.foodordering.util.Util;
import com.google.gson.Gson;

public class Activity_ChangeMyInfo extends BaseActivity implements View.OnClickListener {
    private EditText etMail, etUserName, etNickName, etAddress;
    private SharedPreferences sharedPreferences;
    private String userName, mail, NickName, Address, Sex;
    private RadioGroup radioGroupSex;
    private TextView tvPhoneNumber;
    private RadioButton rb_male, rb_female;//男 女
    private Button btnChangeInfo;
    private String id;
    private String phoneNumber;
    private String address;
    private String name;
    private String email;
    private String nickname;//昵称
    private String sex;
    private String avatar;//头像
    private User user;
    private int statusCode = 0;
    private String postForm;
    private Handler handler = new Handler();
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_my_info);
        pg=new ProgressDialog(Activity_ChangeMyInfo.this);
        getUserData();
        initView();
    }

    /**
     * 获取用户数据（从本地）
     */
    private void getUserData() {
        GetUserData data = new GetUserData();
        user = data.getUser(this);
        id = user.getId();
        address = user.getAddress();
        name = user.getName();
        email = user.getEmail();
        nickname = user.getNickname();
        sex = user.getSex();
        avatar = user.getAvatar();
        phoneNumber = user.getPhoneNumber();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("修改资料");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        btnChangeInfo = (Button) findViewById(R.id.btnChangeInfo);
        btnChangeInfo.setOnClickListener(this);
        radioGroupSex = (RadioGroup) findViewById(R.id.info_sex);
        rb_female = (RadioButton) findViewById(R.id.rb_female);
        rb_male = (RadioButton) findViewById(R.id.rb_male);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etNickName = (EditText) findViewById(R.id.etNickName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etMail = (EditText) findViewById(R.id.etMail);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);

        etNickName.setText(nickname);
        etUserName.setText(name);
        tvPhoneNumber.setText(phoneNumber);
        etMail.setText(email);
        etAddress.setText(address);
        if (sex.equals("男")) {
            radioGroupSex.check(R.id.rb_male);
        } else {
            radioGroupSex.check(R.id.rb_female);
        }
        radioGroupSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int id = group.getCheckedRadioButtonId();//获取选中按钮的id
                switch (id) {
                    case R.id.rb_male://男
                        Sex = "男";
                        break;
                    case R.id.rb_female://女
                        Sex = "女";
                        break;
                    default:
                        break;
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ResultForIntent();
                break;
            default:
                break;
        }
        return true;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Activity_ChangeMyInfo.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangeInfo:
                pg.setMessage("提交中...");
                pg.show();
                NickName = etNickName.getText().toString();
                userName = etUserName.getText().toString();
                mail = etMail.getText().toString();
                Address = etAddress.getText().toString();
                if (userName.isEmpty() || NickName.isEmpty() || mail.isEmpty() || Address.isEmpty()) {
                    Util.showToast(this, "请将信息填完整！");
                    pg.dismiss();
                    return;
                }
                if (Sex == null || Sex.isEmpty()) {
                    if (sex == null || sex.isEmpty()) {
                        return;
                    }
                    Sex = sex;
                }
                if (!Util.checkNetwork(Activity_ChangeMyInfo.this)) {
                    pg.dismiss();
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            postForm = PostUtility.postChangeInfo(id, userName, NickName, Sex, Address, mail);
                            handler.post(runnableModfInfo);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                break;
            default:
                break;
        }
    }

    Runnable runnableModfInfo = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            if (postForm == null || postForm.isEmpty()) {
                Util.showToast(Activity_ChangeMyInfo.this, "服务器出错，请重试！");
                return;
            }
            if (postForm.length() > 100) {
                Util.showToast(Activity_ChangeMyInfo.this, "内部服务器发生错误");
                return;
            } else {
                Gson gson = new Gson();
                Status status = gson.fromJson(postForm, Status.class);
                statusCode = status.getStatusCode();
                Util.showToast(Activity_ChangeMyInfo.this, status.getStatusDescription());
                if (statusCode == 200) {
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Activity_ChangeMyInfo.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", userName); //姓名
                    editor.putString("email", mail);  //电子邮件
                    editor.putString("address", Address);   //地址
                    editor.putString("nickname", NickName);
                    editor.putString("sex", Sex);
                    editor.apply();//提交数据

                    ResultForIntent();
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        ResultForIntent();
    }

    private void ResultForIntent() {
        if (statusCode == 200) {
            Intent intent = new Intent();
            intent.putExtra("name", userName);
            intent.putExtra("email", mail);
            intent.putExtra("address", Address);
            intent.putExtra("nickname", NickName);
            intent.putExtra("sex", Sex);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //重写返回键
            ResultForIntent();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
