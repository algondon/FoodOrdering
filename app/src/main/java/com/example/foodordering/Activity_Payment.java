package com.example.foodordering;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Business;
import com.example.foodordering.bean.Foods;
import com.example.foodordering.bean.PaymentWay;
import com.example.foodordering.bean.Status;
import com.example.foodordering.adapter.Adapter_PaymentWay;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.PostUtility;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.util.Util;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Activity_Payment extends BaseActivity implements View.OnClickListener {
    private Handler handler = new Handler();
    private ProgressDialog pg;
    private RecyclerView recyclerView_payment;
    private Button confirm_pay;
    private TextView tv_remaining_time, tv_cost, business_nameAndId;
    private NumberFormat nf;
    double cost;
    public static int selected_position = 0;
    private double longitude, latitude;
    private String userId, userName, email, sex, phoneNumber, nickname, address, avatar, remarkContent;
    private int statusCode = 0;
    private List<Business> businessList = new ArrayList<>();
    private List<Foods> foodList = new ArrayList<>();
    private String postFormSubmitOrder, postFormBusns, TipText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        pg = new ProgressDialog(Activity_Payment.this);
        getOrderIntentData();
        getBusinessData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("支付订单");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        recyclerView_payment = (RecyclerView) findViewById(R.id.recycler_payment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_payment.setLayoutManager(layoutManager);
        Adapter_PaymentWay adapter = new Adapter_PaymentWay(getPayWayData());
        recyclerView_payment.setAdapter(adapter);

        confirm_pay = (Button) findViewById(R.id.confirm_pay);
        tv_remaining_time = (TextView) findViewById(R.id.tv_remaining_time);
        tv_cost = (TextView) findViewById(R.id.tv_cost);
        business_nameAndId = (TextView) findViewById(R.id.business_nameAndId);

        confirm_pay.setOnClickListener(this);
        tv_cost.setText(nf.format(cost));
        if (businessList != null) {
            business_nameAndId.setText(businessList.get(0).getName() + "-" + businessList.get(0).getId());
        }
        confirm_pay.setText("确认支付" + nf.format(cost));
    }


    /**
     * 得到支付方式数据，用于布局
     *
     * @return
     */
    private List<PaymentWay> getPayWayData() {
        List<PaymentWay> paymentWayList = new ArrayList<>();
        PaymentWay paymentWay1 = new PaymentWay();
        paymentWay1.setId(1);
        paymentWay1.setPayImg(R.drawable.alipay_logo);
        paymentWay1.setPayName("支付宝支付");
        paymentWay1.setPayHint("推荐有支付宝账号的用户使用");
        paymentWay1.setSelected(true);
        paymentWayList.add(paymentWay1);

        PaymentWay paymentWay2 = new PaymentWay();
        paymentWay2.setId(2);
        paymentWay2.setPayImg(R.drawable.wx_pay_logo);
        paymentWay2.setPayName("微信支付");
        paymentWay2.setPayHint("推荐微信版本5.0及以上的用户使用");
        paymentWay2.setSelected(false);
        paymentWayList.add(paymentWay2);

        return paymentWayList;
    }

    /**
     * 获得订单数据
     */
    private void getOrderIntentData() {
        Intent intent = getIntent();
        cost = intent.getDoubleExtra("cost", -1);
        statusCode = intent.getIntExtra("statusCode", -1);
        userId = intent.getStringExtra("userId");
        userName = intent.getStringExtra("userName");
        email = intent.getStringExtra("email");
        sex = intent.getStringExtra("sex");
        phoneNumber = intent.getStringExtra("phoneNumber");
        nickname = intent.getStringExtra("nickname");
        address = intent.getStringExtra("address");
        avatar = intent.getStringExtra("avatar");
        foodList = (ArrayList<Foods>) intent.getSerializableExtra("foods");
        remarkContent = intent.getStringExtra("remarkContent");
        longitude = intent.getDoubleExtra("longitude", -1);
        latitude = intent.getDoubleExtra("latitude", -1);
    }

    /**
     * 获取商家数据
     */
    private void getBusinessData() {
        pg.setMessage("加载中...");
        pg.show();
        if (!Util.checkNetwork(this)) {
            pg.dismiss();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    businessList = RequestUtility.getBusinessList();
                    handler.post(runnableBusiness);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Runnable runnableBusiness = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            initView();
            timerStart();//启动倒计时
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_pay:
                if (Activity_Main.networkState == 0) {
                    Toast.makeText(this, "网络连接失败,请检查网络设置！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selected_position == 0) {
                    TipText = "支付宝支付成功！";
                    SubmitOrder();
                } else if (selected_position == 1) {
                    TipText = "微信支付成功！";
                    SubmitOrder();
                } else {
                    Toast.makeText(Activity_Payment.this, "系统出错!position(" + selected_position + ")", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    /**
     * @param context
     * @param userId
     * @param cost
     * @param statusCode
     * @param userName
     * @param email
     * @param sex
     * @param phoneNumber
     * @param nickname
     * @param address
     * @param avatar
     * @param foodsList
     * @param remarkContent 备注
     */
    public static void actionStart(Context context, String userId, double cost, int statusCode, String userName, String email, String sex, String phoneNumber, String nickname, String address, String avatar, ArrayList<Foods> foodsList, String remarkContent, double longitude, double latitude) {
        Intent intent = new Intent(context, Activity_Payment.class);
        intent.putExtra("userId", userId);
        intent.putExtra("cost", cost);
        intent.putExtra("statusCode", statusCode);
        intent.putExtra("userName", userName);
        intent.putExtra("email", email);
        intent.putExtra("sex", sex);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("nickname", nickname);
        intent.putExtra("address", address);
        intent.putExtra("avatar", avatar);
        intent.putExtra("foods", foodsList);
        intent.putExtra("remarkContent", remarkContent);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        context.startActivity(intent);
    }

    /**
     * 向服务器提交订单
     */
    private void SubmitOrder() {
        pg.setMessage("提交订单中...");
        pg.show();
        if (remarkContent == null) {
            remarkContent = "";
        }
        if (foodList.size() == 0) {
            Util.showToast(this, "购物车中没有商品，请返回主页选择！");
            pg.dismiss();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < foodList.size(); i++) {
                        postFormSubmitOrder = PostUtility.postSubmitOrder(userId,
                                business_nameAndId.getText().toString(),
                                address,
                                phoneNumber,
                                foodList.get(i).getPrice() + "",
                                remarkContent,
                                address,
                                foodList.get(i).getId() + "",
                                foodList.get(i).count,
                                longitude + "",
                                latitude + ""
                        );
                    }
                    handler.post(runnablePayment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    private Runnable runnablePayment = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();

            if (postFormSubmitOrder == null || postFormSubmitOrder.length() > 100) {
                Util.showToast(Activity_Payment.this, "下单失败，请重试！");
                return;
            }
            Gson gson = new Gson();
            final Status status = gson.fromJson(postFormSubmitOrder, Status.class);
            final int statusCode = status.getStatusCode();

            final AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Payment.this);
            builder.setTitle("提示");
            builder.setCancelable(false);
            builder.setMessage(status.getStatusDescription() + TipText);
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            if (statusCode == 200) {
                                Activity_OrderComplete.actionStart(Activity_Payment.this, userId, cost, statusCode, userName, email, sex, phoneNumber, nickname, address, avatar, (ArrayList<Foods>) foodList, remarkContent);
                            } else {
                                timerCancel();//取消倒计时
                            }
                        }
                    });
            builder.create().show();
        }
    };


    /**
     * 倒数计时器
     */
    private CountDownTimer timer = new CountDownTimer(15 * 60 * 1000, 1000) {
        /**
         * 固定间隔被调用,就是每隔countDownInterval会回调一次方法onTick
         * @param millisUntilFinished
         */
        @Override
        public void onTick(long millisUntilFinished) {
            tv_remaining_time.setText(formatTime(millisUntilFinished));
        }

        /**
         * 倒计时完成时被调用
         */
        @Override
        public void onFinish() {
            tv_remaining_time.setText("00:00");
        }
    };

    /**
     * 将毫秒转化为 分钟：秒 的格式
     *
     * @param millisecond 毫秒
     * @return
     */
    public String formatTime(long millisecond) {
        int minute;//分钟
        int second;//秒数
        minute = (int) ((millisecond / 1000) / 60);
        second = (int) ((millisecond / 1000) % 60);
        if (minute < 10) {
            if (second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        } else {
            if (second < 10) {
                return minute + ":" + "0" + second;
            } else {
                return minute + ":" + second;
            }
        }
    }

    /**
     * 取消倒计时
     */
    public void timerCancel() {
        timer.cancel();
    }

    /**
     * 开始倒计时
     */
    public void timerStart() {
        timer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
