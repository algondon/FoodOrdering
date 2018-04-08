package com.example.foodordering;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.foodordering.adapter.Adapter_FoodDelivery;
import com.example.foodordering.bean.Business;
import com.example.foodordering.bean.Order;
import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.util.Util;
import com.example.foodordering.widget.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 食品配送
 */
public class Activity_FoodDelivery extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ProgressDialog pg;
    private View mEmptyView;//无数据视图
    private EmptyRecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Order> orderDeliveryList = new ArrayList<>();//配送订单列表
    private List<Business> businessList = new ArrayList<>();//商家列表

    private MyLocationListener myListener = new MyLocationListener();
    public LocationClient mLocationClient = null;
    private LocationClientOption option = null;
    public static double longitude, latitude;
    public static String address;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pg.dismiss();
            switch (msg.what) {
                case 1:
                    Adapter_FoodDelivery adapter = new Adapter_FoodDelivery(orderDeliveryList, businessList, longitude,latitude,address,Activity_FoodDelivery.this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_FoodDelivery.this);
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setEmptyView(mEmptyView);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_delivery);
        pg = new ProgressDialog(Activity_FoodDelivery.this);
        initView();
        location();
        getBusinessData();
        getFoodDeliveryData();
    }

    /**
     * 定位，获取当前位置信息
     */
    private void location() {
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        initLocation();
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();
    }

    private void initLocation() {
        // 利用LocationClientOption类配置定位SDK参数
        option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        // 可选，设置定位模式，默认高精度
        // LocationMode.Hight_Accuracy：高精度；
        // LocationMode. Battery_Saving：低功耗；
        // LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        // 可选，设置返回经纬度坐标类型，默认gcj02
        // gcj02：国测局坐标；
        // bd09ll：百度经纬度坐标；
        // bd09：百度墨卡托坐标；
        // 海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setOpenGps(true);
        // 可选，设置是否使用gps，默认false
        // 使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        // 可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(true);
        // 可选，定位SDK内部是一个service，并放到了独立进程。
        // 设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        // 可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setEnableSimulateGps(false);
        // 可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedLocationDescribe(true);
        // 可选，是否需要位置描述信息，默认为不需要，即参数为false
        // 如果开发者需要获得当前点的位置信息，此处必须为true

        option.setIsNeedLocationPoiList(true);
        // 可选，是否需要周边POI信息，默认为不需要，即参数为false
        // 如果开发者需要获得周边POI信息，此处必须为true

        option.setIsNeedAddress(true);// 获取详细信息

        mLocationClient.setLocOption(option);
        // mLocationClient为第二步初始化过的LocationClient对象
        // 需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        // 更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation arg0) {
            if (arg0.getLocType() == BDLocation.TypeGpsLocation
                    || arg0.getLocType() == BDLocation.TypeNetWorkLocation) {
                longitude = arg0.getLongitude();
                latitude = arg0.getLatitude();
                address = arg0.getAddrStr();
//                Util.showToast(Activity_FoodDelivery.this,longitude+"/"+latitude+" "+address);
            }

        }
    }


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Activity_FoodDelivery.class);
        context.startActivity(intent);
    }

    /**
     * 获取商家信息（从MainActivity）
     */
    private void getBusinessData() {
        businessList = Activity_Main.businessList;
    }

    /**
     * 获取需要送餐的外卖数据
     */
    private void getFoodDeliveryData() {
        pg.setMessage("正在获取数据和定位信息...");
        pg.show();
        if (!Util.checkNetwork(this)) {
            pg.dismiss();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    // 获取订单
                    orderDeliveryList = RequestUtility.getOrdersList("201712dd70");
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("送餐");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mEmptyView = findViewById(R.id.id_empty_view);
        recyclerView = (EmptyRecyclerView) findViewById(R.id.recyclerView);
        //刷新控件
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutOrderAll);
        swipeRefreshLayout.setOnRefreshListener(this);
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
    public void onRefresh() {
        getFoodDeliveryData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //刷新控件停止两秒后消失
                    Thread.sleep(1000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
