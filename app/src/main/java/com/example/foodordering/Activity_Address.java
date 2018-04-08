package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Address;
import com.example.foodordering.bean.User;
import com.example.foodordering.adapter.Adapter_Address;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.util.GetUserData;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Activity_Address extends BaseActivity implements View.OnClickListener {
    private String userId, address, name, email, nickname, sex, avatar, phoneNumber;//头像
    private LinearLayout ly_AddAddress;
    private User user;
    private RecyclerView recyclerView_address;
    private List<Address> addressList = new ArrayList<>();
    public static String remark;//标记是从哪个页面跳转过来的
    private Handler handler = new Handler();
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        pg = new ProgressDialog(Activity_Address.this);
        getIntentData();
        getUserData();
        initView();
    }

    public static void actionStart(Context context, String remark) {
        Intent intent = new Intent(context, Activity_Address.class);
        intent.putExtra("remark", remark);
        context.startActivity(intent);
    }

    /**
     * 获取remark，标记是哪个页面跳转
     */
    private void getIntentData() {
        Intent intent = getIntent();
        remark = intent.getStringExtra("remark");
    }

    /**
     * 获取用户信息
     */
    private void getUserData() {
        GetUserData data = new GetUserData();
        user = data.getUser(this);
        userId = user.getId();
        address = user.getAddress();
        name = user.getName();
        email = user.getEmail();
        nickname = user.getNickname();
        sex = user.getSex();
        avatar = user.getAvatar();
        phoneNumber = user.getPhoneNumber();
    }

    private void findViewById() {
        ly_AddAddress = (LinearLayout) findViewById(R.id.ly_AddAddress);
        recyclerView_address = (RecyclerView) findViewById(R.id.recyclerView_address);
        ly_AddAddress.setOnClickListener(this);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("我的收货地址");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewById();

        getAddressData(userId);
    }

    /**
     * 通过用户id获取地址信息
     *
     * @param userId
     */
    private void getAddressData(final String userId) {
        pg.setMessage("获取地址中...");
        pg.show();
        if (!Util.checkNetwork(this)) {
            pg.dismiss();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    addressList = RequestUtility.getAddressListById(userId);
                    handler.post(runnableAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Runnable runnableAddress = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_Address.this);
            recyclerView_address.setLayoutManager(layoutManager);
            Adapter_Address adapter_address = new Adapter_Address(addressList);
            recyclerView_address.setAdapter(adapter_address);

            //为RecyclerView添加item点击事件
            adapter_address.setOnItemClickListener(new Adapter_Address.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (remark != null && remark.equals("结算页面")) {
                        //向结算页面返回结果
                        Address address = addressList.get(position);
                        Intent intent = new Intent();
                        intent.putExtra("name", address.getReceiveName());
                        intent.putExtra("sex", address.getSex());
                        intent.putExtra("phoneNumber", address.getContact());
                        intent.putExtra("address", address.getAddress());
                        intent.putExtra("longitude", address.getLongitude());
                        intent.putExtra("latitude", address.getLatitude());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
        }
    };

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
            case R.id.ly_AddAddress://添加地址
                Activity_UpdateAddress.actionStart(Activity_Address.this, userId);
                finish();
                break;
            default:
                break;
        }
    }
}
