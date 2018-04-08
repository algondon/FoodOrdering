package com.example.foodordering;

import com.baidu.location.BDLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.foodordering.control.BaseActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Activity_Location extends BaseActivity implements View.OnClickListener {
    private LinearLayout back_lay;
    private TextView title,custom_made;
    private TextView tv_longitude, tv_latitude, tv_address;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    // 判断是否是第一次进入应用
    private boolean isFirstLocation = true;
    private LatLng latLng;
    private MyLocationListener myListener = new MyLocationListener();
    public LocationClient mLocationClient = null;
    private LocationClientOption option = null;

    private double longitude, latitude;
    private String address;

    private void findViewById() {
        back_lay = (LinearLayout) findViewById(R.id.back_lay);
        title= (TextView) findViewById(R.id.title);
        custom_made= (TextView) findViewById(R.id.custom_made);
        title.setText("定位");
        custom_made.setText("确定");
        mMapView = (MapView) findViewById(R.id.bmapView);
        tv_longitude = (TextView) findViewById(R.id.tv_longitude);
        tv_latitude = (TextView) findViewById(R.id.tv_latitude);
        tv_address = (TextView) findViewById(R.id.tv_address);
        back_lay.setOnClickListener(this);
        custom_made.setOnClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_location);
        findViewById();

        initMap();
        // 声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());

        initLocation();
        // 注册监听函数
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();

        mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                // TODO Auto-generated method stub
                LatLng latLng = arg0.getPosition();
                longitude = latLng.longitude;
                latitude = latLng.latitude;
                address = arg0.getName() + "";

                // 小数位数太长，只显示一部分（上传的位置还是真实经纬度）
                String str_longitude = subStringForLocation(String
                        .valueOf(longitude));
                String str_latitude = subStringForLocation(String
                        .valueOf(latitude));

                tv_longitude.setText(str_longitude);
                tv_latitude.setText(str_latitude);
                tv_address.setText(address);

                MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
                locationBuilder.latitude(latitude);
                locationBuilder.longitude(longitude);
                MyLocationData locationData = locationBuilder.build();
                mBaiduMap.setMyLocationData(locationData);

                return false;
            }

            @SuppressLint("WrongConstant")
            @Override
            public void onMapClick(LatLng arg0) {
                // TODO Auto-generated method stub
                longitude = arg0.longitude;
                latitude = arg0.latitude;

                // 小数位数太长，只显示一部分（上传的位置还是真实经纬度）
                String str_longitude = subStringForLocation(String
                        .valueOf(longitude));
                String str_latitude = subStringForLocation(String
                        .valueOf(latitude));

                tv_longitude.setText(str_longitude);
                tv_latitude.setText(str_latitude);


                MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
                locationBuilder.latitude(latitude);
                locationBuilder.longitude(longitude);
                MyLocationData locationData = locationBuilder.build();
                mBaiduMap.setMyLocationData(locationData);

                GeoCoder geoCoder = GeoCoder.newInstance();
                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
                            return;
                        }
                        address = reverseGeoCodeResult.getAddress();
                        tv_address.setText(address);
                    }
                });
                //反向地理解析
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(arg0));
            }
        });
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

    private void initMap() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
    }

    private void initLocation() {
        // 利用LocationClientOption类配置定位SDK参数
        option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);
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

    private void navigateTo(BDLocation location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        tv_longitude.setText(longitude + "");
        tv_latitude.setText(latitude + "");
        address = location.getAddrStr();
        tv_address.setText(address);
        if (isFirstLocation) {
            latLng = new LatLng(latitude, longitude);
            MapStatus.Builder builder = new MapStatus.Builder();
            MapStatus mapStatus = builder.target(latLng).zoom(15.0f).build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory
                    .newMapStatus(mapStatus));
            isFirstLocation = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        mBaiduMap.setMyLocationData(locationData);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_lay:
                Intent intent = new Intent();
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("address", address);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.custom_made:
                Intent intent2 = new Intent();
                intent2.putExtra("longitude", longitude);
                intent2.putExtra("latitude", latitude);
                intent2.putExtra("address", address);
                setResult(RESULT_OK, intent2);
                finish();
                break;
            default:
                break;
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation arg0) {
            if (arg0.getLocType() == BDLocation.TypeGpsLocation
                    || arg0.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(arg0);
            }

        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapView.onDestroy();
        // 当不需要定位图层时关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        // 取消监听函数
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(myListener);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            intent.putExtra("longitude", longitude);
            intent.putExtra("latitude", latitude);
            intent.putExtra("address", address);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
