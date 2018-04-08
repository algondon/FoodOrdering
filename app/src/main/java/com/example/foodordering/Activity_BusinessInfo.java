package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Business;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.util.DateTimeTransfer;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * 商家信息页面
 */
public class Activity_BusinessInfo extends BaseActivity {
    private List<Business> businessList = new ArrayList<>();//商家列表
    private String Id;//编号
    private String Name;//店名
    private String Contact;//联系方式
    private String Address;//地址
    private String Manager;//店主
    private String OpenTime;//开门时间
    private String ClosedTime;//关门时间
    private TextView tvId, tvName, tvContact, tvAddress, tvManager, tvOpenTime, tvClosedTime;
    private Handler handler=new Handler();
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_info);
        pg=new ProgressDialog(Activity_BusinessInfo.this);
        getBusinessData();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Activity_BusinessInfo.class);
        context.startActivity(intent);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("商家信息");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (Activity_Main.networkState == 0) {
            Toast.makeText(Activity_BusinessInfo.this, "网络连接失败，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
            return;
        }

        tvId = (TextView) findViewById(R.id.business_id);
        tvName = (TextView) findViewById(R.id.business_name);
        tvContact = (TextView) findViewById(R.id.business_contact);
        tvAddress = (TextView) findViewById(R.id.business_address);
        tvManager = (TextView) findViewById(R.id.business_manager);
        tvOpenTime = (TextView) findViewById(R.id.business_open_time);
        tvClosedTime = (TextView) findViewById(R.id.business_close_time);
        tvId.setText(Id);
        tvName.setText(Name);
        tvContact.setText(Contact);
        tvAddress.setText(Address);
        tvManager.setText(Manager);
        if (OpenTime != null) {
            String timeOpen = DateTimeTransfer.GetTimeFromLong(OpenTime.substring(OpenTime.lastIndexOf("(") + 1, OpenTime.lastIndexOf(")") - 3));
            tvOpenTime.setText(timeOpen);
        }
        if (ClosedTime != null) {
            String timeClosed = DateTimeTransfer.GetTimeFromLong(ClosedTime.substring(ClosedTime.lastIndexOf("(") + 1, ClosedTime.lastIndexOf(")") - 3));
            tvClosedTime.setText(timeClosed);
        }
    }

    /**
     * 获取商家信息
     */
    private void getBusinessData() {
        pg.setMessage("数据加载中...");
        pg.show();
        if (!Util.checkNetwork(Activity_BusinessInfo.this)) {
            pg.dismiss();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    businessList = RequestUtility.getBusinessList();//商家列表
                    handler.post(runnableBusns);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }
    Runnable runnableBusns=new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            if (businessList != null && businessList.size() > 0) {
                Id = businessList.get(0).getId();
                Name = businessList.get(0).getName();
                Contact = businessList.get(0).getContact();
                Address = businessList.get(0).getAddress();
                Manager = businessList.get(0).getManager();
                OpenTime = businessList.get(0).getOpenTime();
                ClosedTime = businessList.get(0).getClosedTime();
            }
            initView();
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
}
