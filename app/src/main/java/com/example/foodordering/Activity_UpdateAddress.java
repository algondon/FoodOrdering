package com.example.foodordering;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.bean.Status;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.PostUtility;
import com.example.foodordering.util.Util;
import com.google.gson.Gson;

public class Activity_UpdateAddress extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout ly_location;
    private Button btn_submitAddress;
    private TextView tv_location;
    private EditText et_address, et_detail_address, et_phone, et_receiveName;//地址 详细地址 电话 收件人 经纬度
    private RadioGroup radioGroup;
    private RadioButton rb_male, rb_female;//男 女
    private String UserId, Address, ReceiveName, Sex, Contact;
    private RadioGroup.OnCheckedChangeListener changeListener;//RadioGroup监听器
    private Handler handler = new Handler();
    private ProgressDialog pg;
    private String postForm;
    private final int requestCode_location = 333;
    private String longitude, latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_address);
        initView();
    }

    public static void actionStart(Context context, String UserId) {
        Intent intent = new Intent(context, Activity_UpdateAddress.class);
        intent.putExtra("userId", UserId);
        context.startActivity(intent);
    }

    private void getData() {
        Intent intent = getIntent();
        UserId = intent.getStringExtra("userId");

        Address = et_address.getText().toString() + " " + et_detail_address.getText().toString();
        ReceiveName = et_receiveName.getText().toString();
        Contact = et_phone.getText().toString();
    }

    private void findViewById() {
        ly_location = (LinearLayout) findViewById(R.id.ly_location);
        et_address = (EditText) findViewById(R.id.et_address);
        et_detail_address = (EditText) findViewById(R.id.et_detail_address);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_receiveName = (EditText) findViewById(R.id.et_receiveName);
        tv_location = (TextView) findViewById(R.id.tv_location);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup_sex);
        rb_male = (RadioButton) findViewById(R.id.rb_male);
        rb_female = (RadioButton) findViewById(R.id.rb_female);
        btn_submitAddress = (Button) findViewById(R.id.btn_submitAddress);
        ly_location.setOnClickListener(this);
        tv_location.setOnClickListener(this);
    }

    private void initView() {
        pg = new ProgressDialog(Activity_UpdateAddress.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加地址");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewById();
        btn_submitAddress.setOnClickListener(this);
        //为radioGroup绑定监听器
        radioGroup.setOnCheckedChangeListener(changeListener);
        changeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();//获取选中按钮的id
                switch (id) {
                    case R.id.rb_male://男
                        Toast.makeText(Activity_UpdateAddress.this, rb_male.getText(), Toast.LENGTH_SHORT).show();
                        Sex = "男";
                        break;
                    case R.id.rb_female://女
                        Sex = "女";
                        break;
                    default:
                        break;
                }
            }
        };

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
            case R.id.btn_submitAddress:
                pg.setMessage("正在提交...");
                pg.show();
                getData();
                if (!Util.checkNetwork(this)) {
                    pg.dismiss();
                    return;
                }
                if (Sex == null || Sex.isEmpty()) {
                    Sex = "男";
                }
                if (Address.isEmpty() || ReceiveName.isEmpty() || Sex.isEmpty() || Contact.isEmpty() || Address.isEmpty()) {
                    pg.dismiss();
                    Toast.makeText(this, "请将信息填完整！", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            postForm = PostUtility.postInsertAddress(UserId, Address, ReceiveName, Sex, Contact,longitude,latitude);
                            handler.post(runnableUpdateAddress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case R.id.ly_location:
                Intent intent = new Intent(Activity_UpdateAddress.this,
                        Activity_Location.class);
                startActivityForResult(intent, requestCode_location);
                break;
            case R.id.tv_location:
                Intent intent2 = new Intent(Activity_UpdateAddress.this,
                        Activity_Location.class);
                startActivityForResult(intent2, requestCode_location);
                break;
            default:
                break;
        }
    }

    Runnable runnableUpdateAddress = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            String msg;
            if (postForm == null || postForm.length() > 70) {
                Toast.makeText(Activity_UpdateAddress.this, "登录失败,服务器错误！", Toast.LENGTH_SHORT).show();
                return;
            }
            Gson gson = new Gson();
            final Status status = gson.fromJson(postForm, Status.class);
            msg = status.getStatusDescription();

            final AlertDialog.Builder builder = new AlertDialog.Builder(Activity_UpdateAddress.this);
            builder.setTitle("提示");
            builder.setCancelable(false);
            builder.setMessage(msg);
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            int statusCode = status.getStatusCode();
                            if (statusCode == 200) {
                                Activity_Address.actionStart(Activity_UpdateAddress.this, "更新地址页面");
                                finish();
                            }
                        }
                    });
            builder.create().show();
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case requestCode_location:
                if (resultCode == RESULT_OK) {
                    longitude = data.getDoubleExtra("longitude", -1) + "";
                    latitude = data.getDoubleExtra("latitude", -1) + "";
                    String address = data.getStringExtra("address");
                    // 小数位数太长，只显示一部分（上传的位置还是真实经纬度）
                    String str_longitude = subStringForLocation(longitude);
                    String str_latitude = subStringForLocation(latitude);

                    tv_location.setText(str_longitude + "," + str_latitude);
                    et_address.setText(address);
                }
                break;

            default:
                break;
        }
    }


    /**
     * 截取字符串，长度大于10则截取10位
     *
     * @param location
     * @return
     */
    private String subStringForLocation(String location) {
        String rt_location = "";
        if (location != null) {
            if (location.length() > 10) {
                rt_location = location.substring(0, 10);
            } else {
                rt_location = location;
            }
        }
        return rt_location;
    }
}
