package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Foods;
import com.example.foodordering.adapter.Adapter_Balance;
import com.example.foodordering.util.Util;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * 结算页面
 */
public class Activity_Balance extends BaseActivity implements View.OnClickListener {
    private Context context;
    private LinearLayout ly_AddressInfo, ly_remarkInfo;//地址信息 备注信息
    private TextView tv_userName, tv_sex, tv_phoneNumber, tv_address, tv_remarkContent;
    private TextView tvBalance, tvCostBalance, tvCostDistribution, tvCost;//结算按钮，总价格，配送费,待支付
    private RecyclerView rvSelected;
    private Adapter_Balance adapter;
    private NumberFormat nf;
    private double longitude, latitude;
    private String userId, userName, email, sex, phoneNumber, nickname, address, avatar, remarkContent;
    private int statusCode = 0;
    double cost = 0, costDistribution = 0;//总价格,配送费
    private ArrayList<Foods> arrayList;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        getIntentData();
        initView();
        tvBalance.setOnClickListener(this);
    }

    private void findViewById() {
        tv_userName = (TextView) findViewById(R.id.user_name);
        tv_sex = (TextView) findViewById(R.id.sex);
        tv_phoneNumber = (TextView) findViewById(R.id.phone_number);
        tv_address = (TextView) findViewById(R.id.address);
        tvBalance = (TextView) findViewById(R.id.tvBalance);
        tvCostBalance = (TextView) findViewById(R.id.tvCostBalance);
        tvCostDistribution = (TextView) findViewById(R.id.tvCostDistribution);
        tvCost = (TextView) findViewById(R.id.tvCost);
        rvSelected = (RecyclerView) findViewById(R.id.selectRecyclerView);
        ly_remarkInfo = (LinearLayout) findViewById(R.id.ly_remarkInfo);
        ly_AddressInfo = (LinearLayout) findViewById(R.id.ly_AddressInfo);
        tv_remarkContent = (TextView) findViewById(R.id.tv_remarkContent);
    }

    private void initView() {
        context = Activity_Balance.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("提交订单");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        findViewById();
        ly_remarkInfo.setOnClickListener(this);
        ly_AddressInfo.setOnClickListener(this);

        adapter = new Adapter_Balance(Activity_Balance.this, arrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvSelected.setLayoutManager(layoutManager);
        rvSelected.setAdapter(adapter);

        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        for (int i = 0; i < arrayList.size(); i++) {
            Foods item = arrayList.get(i);
            cost += item.count * item.getPrice();
        }
        cost += costDistribution;
        tvCostBalance.setText(nf.format(cost));
        tvCost.setText(nf.format(cost));

        tv_userName.setText(userName);
        tv_phoneNumber.setText(phoneNumber);
        tv_address.setText(address);
        if (sex != null) {
            if (sex.equals("男")) {
                tv_sex.setText("先生");
            } else if (sex.equals("女")) {
                tv_sex.setText("女士");
            } else {
                tv_sex.setText(sex);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvBalance://结算
            if(longitude<=0||latitude<=0){
                Util.showToast(Activity_Balance.this,"此收货地址未包含经纬度信息，请重新选择或新建地址");
}else{
                Activity_Payment.actionStart(Activity_Balance.this, userId, cost, statusCode, userName, email, sex, phoneNumber, nickname, address, avatar, arrayList, remarkContent, longitude, latitude);
}
                break;
            case R.id.ly_remarkInfo://备注信息
                Intent intentRemark = new Intent(Activity_Balance.this, Activity_Remark.class);
                startActivityForResult(intentRemark, 2);
                break;
            case R.id.ly_AddressInfo://地址信息
                Intent intent = new Intent(context, Activity_Address.class);
                intent.putExtra("remark", "结算页面");
                startActivityForResult(intent, 1);
                break;
            default:
                break;
        }
    }

    /**
     * 获取从地址页面回传的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1://地址
                if (resultCode == RESULT_OK) {
                    userName = data.getStringExtra("name");
                    phoneNumber = data.getStringExtra("phoneNumber");
                    sex = data.getStringExtra("sex");
                    address = data.getStringExtra("address");
                    longitude = data.getDoubleExtra("longitude", -1);
                    latitude = data.getDoubleExtra("latitude", -1);

                    tv_userName.setText(userName);
                    tv_phoneNumber.setText(phoneNumber);
                    tv_address.setText(address);
                    if (sex.equals("男")) {
                        tv_sex.setText("先生");
                    } else if (sex.equals("女")) {
                        tv_sex.setText("女士");
                    } else {
                        tv_sex.setText(sex);
                    }
                }
                break;
            case 2://备注
                if (resultCode == RESULT_OK) {
                    remarkContent = data.getStringExtra("remarkContent");
                    tv_remarkContent.setText(remarkContent);
                }
                break;
            default:
                break;
        }
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

    private void getIntentData() {
        Intent intent = getIntent();
        arrayList = (ArrayList<Foods>) intent.getSerializableExtra("selectList");
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        email = intent.getStringExtra("email");
        sex = intent.getStringExtra("sex");
        phoneNumber = intent.getStringExtra("phoneNumber");
        address = intent.getStringExtra("address");
        avatar = intent.getStringExtra("avatar");
        nickname = intent.getStringExtra("nickname");
        statusCode = intent.getIntExtra("statusCode", -1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
