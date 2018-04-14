package com.example.foodordering;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.example.foodordering.control.ActivityCollector;
import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Business;
import com.example.foodordering.bean.User;
import com.example.foodordering.fragment.Fragment_find;
import com.example.foodordering.fragment.Fragment_home;
import com.example.foodordering.fragment.Fragment_order;
import com.example.foodordering.share.ShareCallBack;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.util.GetUserData;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.util.Util;
import com.example.foodordering.zxing.android.CaptureActivity;
import com.facebook.imageutils.BitmapUtil;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ShareUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Activity_Main extends BaseActivity {
    private TextView login_register, user_nickname;
    private CircleImageView icon_image;
    private Context context;
    private BottomNavigationBar bottomNavigationBar;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navView;
    private TextView toolbarText;
    public String userId, address, name, email, nickname, sex, avatar, phoneNumber;
    public static List<Business> businessList = new ArrayList<>();
    public User user = new User();
    public int statusCode = 0;//登录状态
    private SharedPreferences sharedPreferences;
    private List<User> userList = new ArrayList<>();
    private Handler handler = new Handler();
    private ProgressDialog pg;
    private boolean isRefresh = true;//控制底部导航栏加载次数，否则再次刷新会重复

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    public static int networkState = -1;//0：网络不可用  1：网络可用

    private long firstTime = 0;//记录用户首次点击返回键的时间

    //扫一扫
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;

    private final int requestCode_MYINFO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = Activity_Main.this;
        pg = new ProgressDialog(context);
        getNetworkState();
        getIntentData();
        initStaticView();
        getData();
    }


    /**
     * 先加载不需要动态获取数据的静态布局，有利于用户体验
     */
    private void initStaticView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navView = (NavigationView) findViewById(R.id.nav_view);
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        toolbar.setTitle("");
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("首页");

        setSupportActionBar(toolbar);//使用toolbar,外观功能和ActionBar一致
        final ActionBar actionBar = getSupportActionBar();//得到ActionBar实例
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);//让导航按钮显示出来
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);//设置导航按钮图标
        }
    }

    private void initView() {

        //加载侧滑栏菜单项点击事件
        loadNavMenuView();

        //动态加载侧滑栏头部及点击事件
        loadHeaderView();

        if (isRefresh) {
            //加载底部导航栏及选中事件
            loadBottomNavigationBar();
        }
    }

    /**
     * 动态添加fragment（碎片）
     *
     * @param fragment
     */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, fragment);
        transaction.commit();
    }

    /**
     * 加载侧滑栏菜单项点击事件
     */
    private void loadNavMenuView() {
        navView.setCheckedItem(R.id.nav_comment);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {//事件监听器
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_comment://我的评价
                        if (statusCode == 200) {
                            Activity_MyComment.actionStart(Activity_Main.this, user.getId());
                        } else {
                            Toast.makeText(context, "请先登录!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_monitor://视频监控
                        Intent intentVideo = new Intent(context, Activity_VideoSurveillance.class);
                        startActivity(intentVideo);
                        break;
                    case R.id.nav_business://商家信息
                        Activity_BusinessInfo.actionStart(context);
                        break;
                    case R.id.nav_location://我的地址
                        if (statusCode == 200) {
                            Activity_Address.actionStart(context, "主页面");
                        } else {
                            Toast.makeText(context, "请先登录!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_aboutUs://关于我们
                        Activity_AboutUs.actionStart(context);
                        break;
                    case R.id.nav_customer_service://客服中心
                        if (!Util.checkNetwork(context)) {
                            break;
                        }
                        Activity_Customer_Service.actionStart(context);
                        break;
                    case R.id.nav_share://分享我们
                        if (!Util.checkNetwork(context)) {
                            break;
                        }
                        Activity_ShareUs.actionStart(context);
                        break;
                    case R.id.nav_exit://退出登录
                        if (statusCode == 200) {
                            new AlertDialog.Builder(Activity_Main.this).setTitle("退出")
                                    .setMessage("确定要退出登录？")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", Activity.MODE_PRIVATE);
                                            SharedPreferences.Editor editors = sharedPreferences.edit();
                                            editors.putInt("statusCode", 0);
                                            editors.commit();//提交修改

                                            Intent intent = new Intent(context, Activity_Login.class);
                                            startActivity(intent);
                                        }
                                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();

                        } else {
                            Toast.makeText(context, "你还未登录，请先登录!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.nav_food_delivery://食品配送
                       if (statusCode == 200) {
                           Activity_FoodDelivery.actionStart(context);
                        } else {
                            Toast.makeText(context, "请先登录!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 加载底部导航栏及选中事件
     */
    private void loadBottomNavigationBar() {
        toolbarText.setText("首页");
        replaceFragment(new Fragment_home());
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.icon_home, "首页"))
                .addItem(new BottomNavigationItem(R.mipmap.nav_location, "发现"))
                .addItem(new BottomNavigationItem(R.mipmap.nav_task, "订单"))
                .setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .setActiveColor(R.color.colorPrimary)
                .setFirstSelectedPosition(0)
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {//未选中 -> 选中
                switch (position) {
                    case 0:
                        toolbarText.setText("首页");
                        replaceFragment(new Fragment_home());
                        break;
                    case 1:
                        toolbarText.setText("发现");
                        replaceFragment(new Fragment_find());
                        break;
                    case 2:
                        if (statusCode == 200) {
                            replaceFragment(new Fragment_order());
                            toolbarText.setText("我的订单");
                        } else {
                            Toast.makeText(context, "请先登录!", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {//选中 -> 未选中

            }

            @Override
            public void onTabReselected(int position) {//选中 -> 选中

            }
        });
    }

    /**
     * 动态加载侧滑栏头部及点击事件
     */
    private void loadHeaderView() {
//        View headerView = navView.inflateHeaderView(R.layout.nav_header);
        //利用此方法获取头部视图，避免再次刷新重复显示
        View headerView = navView.getHeaderView(0);
        Button btn_setting = (Button) headerView.findViewById(R.id.btn_setting);
        login_register = (TextView) headerView.findViewById(R.id.login_regist);
        user_nickname = (TextView) headerView.findViewById(R.id.user_nickname);
        icon_image = (CircleImageView) headerView.findViewById(R.id.icon_image);

        if (statusCode == 200) {
            if (!nickname.isEmpty()) {
                user_nickname.setText(nickname);
            }
            login_register.setText(phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            login_register.setEnabled(false);
        }
        //设置
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Setting.actionStart(context);
            }
        });
        icon_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusCode == 200) {
                    Intent intent = new Intent(Activity_Main.this, Activity_MyInformation.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("address", address);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("sex", sex);
                    intent.putExtra("avatar", avatar);
                    startActivityForResult(intent, requestCode_MYINFO);
                } else {
                    Toast.makeText(context, "请先登录!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        user_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusCode == 200) {
                    Intent intent = new Intent(Activity_Main.this, Activity_MyInformation.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("name", name);
                    intent.putExtra("email", email);
                    intent.putExtra("phoneNumber", phoneNumber);
                    intent.putExtra("address", address);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("sex", sex);
                    intent.putExtra("avatar", avatar);
                    startActivityForResult(intent, requestCode_MYINFO);
                } else {
                    Toast.makeText(context, "请先登录!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Activity_Login.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 加载toolbar.xml这个菜单文件
     *
     * @param menu
     * @return
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    /**
     * 处理toolbar中各个按钮的点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://HomeAsUp按钮id永远是home
                drawerLayout.openDrawer(GravityCompat.START);//将滑动菜单显示，GravityCompat.START和xml定义的一致
                break;
            case R.id.scanner://扫一扫
                if (!Util.checkNetwork(this)) {
                    break;
                }
                Intent intent = new Intent(Activity_Main.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * @param context
     * @param phoneNumber 电话号码
     * @param statusCode  登录状态码
     */
    public static void actionStart(Context context, String phoneNumber, int statusCode) {
        Intent intent = new Intent(context, Activity_Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("statusCode", statusCode);
        context.startActivity(intent);
    }

    /**
     * 双击返回键退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(Activity_Main.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                ActivityCollector.finishAll();
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void getIntentData() {
//        Intent intent = getIntent();
//        phoneNumber = intent.getStringExtra("phoneNumber");
//        statusCode = intent.getIntExtra("statusCode", -1);

        SharedPreferences preferences = getSharedPreferences("LoginInfo", Activity.MODE_PRIVATE);
        statusCode = preferences.getInt("statusCode", -1);
        phoneNumber = preferences.getString("phoneNumber", "");

        GetUserData data = new GetUserData();
        user = data.getUser(Activity_Main.this);
        nickname = user.getNickname();
    }

    /**
     * 获取用户信息和商家信息
     */
    private void getData() {
        pg.setMessage("数据加载中...");
        pg.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    if (statusCode == 200) {
                        userList = RequestUtility.getUserListForPhoneNumber(phoneNumber);//用户信息
                    }
                    businessList = RequestUtility.getBusinessList();//商家列表
                    handler.post(runnableUserBusns);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Runnable runnableUserBusns = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            if (statusCode == 200) {
                if (userList != null && userList.size() > 0) {
                    userId = userList.get(0).getId();
                    name = userList.get(0).getName();
                    email = userList.get(0).getEmail();
                    address = userList.get(0).getAddress();
                    nickname = userList.get(0).getNickname();
                    sex = userList.get(0).getSex();
                    avatar = userList.get(0).getAvatar();
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Activity_Main.this);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("statusCode", statusCode);//状态码
                    editor.putString("userId", userId);//用户编号
                    editor.putString("name", name); //姓名
                    editor.putString("email", email);  //电子邮件
                    editor.putString("address", address);   //地址
                    editor.putString("nickname", nickname);
                    editor.putString("sex", sex);
                    editor.putString("phoneNumber", phoneNumber);
                    editor.putString("avatar", avatar);
                    editor.apply();//提交数据
                }
            }
            initView();
        }

    };

    /**
     * 活动销毁
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);//关闭广播
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SCAN:
                // 扫描二维码/条码回传
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String content = data.getStringExtra(DECODED_CONTENT_KEY);
                        Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
                        if (content.indexOf("-") != -1) {
                            String foodId = interceptString(content);//获得食物id
                            Activity_FoodsDetails.actionStart(context, Integer.parseInt(foodId.trim()));
                        } else {
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Activity_Main.this);
                            builder.setTitle("扫描懒人外卖专用二维码有惊喜哟,不过也可以告诉你,你扫描到的内容是：");
                            builder.setCancelable(false);
                            builder.setMessage(content);
                            builder.setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {

                                        }
                                    });
                            builder.create().show();
                        }
                    }
                }
                break;
            case requestCode_MYINFO:
                if (resultCode == RESULT_OK) {
                    statusCode = data.getIntExtra("statusCode", -1);
                    if (statusCode != -1) {
                        //不刷新底部导航
                        isRefresh = false;
                        getData();
                    }
                }
                break;

            case ShareConstant.REQUEST_CODE:
                if (data != null) {
                    int channel = data.getIntExtra(ShareConstant.EXTRA_SHARE_CHANNEL, -1);
                    int status = data.getIntExtra(ShareConstant.EXTRA_SHARE_STATUS, -1);
                    onShareCallback(channel, status);
                }
                break;
            default:
                break;
        }
    }
    /**
     * 分享回调处理
     * @param channel
     * @param status
     */
    private void onShareCallback(int channel, int status) {
        new ShareCallBack().onShareCallback(channel, status);
    }

    /**
     * 截取"-"前面的字符串
     *
     * @param str 字符串内容
     * @return
     */
    private String interceptString(String str) {
        return str.substring(0, str.indexOf("-"));
    }

    /**
     * 获取网络连接状态
     */
    private void getNetworkState() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//网络状态发生改变时发出这一条广播
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //getSystemService()方法得到ConnectivityManager实例，这是一个系统服务类，用于管理网络连接
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            //得到NetworkInfo实例
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {//网络可用
                networkState = 1;
            } else {//网络不可用
                networkState = 0;
            }
        }
    }

}
