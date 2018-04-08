package com.example.foodordering.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodordering.Activity_FoodDelivery;
import com.example.foodordering.Activity_Main;
import com.example.foodordering.R;
import com.example.foodordering.bean.Business;
import com.example.foodordering.bean.Order;
import com.example.foodordering.bean.Status;
import com.example.foodordering.bean.User;
import com.example.foodordering.tools.CommProgressDialog;
import com.example.foodordering.util.DateTimeTransfer;
import com.example.foodordering.util.GetUserData;
import com.example.foodordering.util.IAlertDialogButtonListener;
import com.example.foodordering.util.Util;
import com.google.gson.Gson;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by xch on 2017/3/29.
 */

public class Adapter_FoodDelivery extends RecyclerView.Adapter<Adapter_FoodDelivery.ViewHolder> {
    private CommProgressDialog loadingDialog;
    private Context mContext;
    private List<Order> mOrderAllList;
    private List<Business> mBusinessList;
    private static User user;
    private static String postForm = "";
    private static Order order = null;
    private static GetUserData data = new GetUserData();
    private static Gson gson = new Gson();
    private static Status status = null;
    private static int statusCode = 0, position = 0;
    private static ViewHolder holder;

    //----------------以下都是百度坐标系的坐标------------------//
    private double LATITUDE_QIDIAN;  //起点纬度
    private double LONGTITUDE_QIDIAN;  //起点经度
    private String ADDRESS_QIDIAN = "昆明市盘龙区北京路492号";//起点名称

    private double LATITUDE_ZHONGDIAN = 25.044516;  //终点纬度
    private double LONGTITUDE_ZHONGDIAN = 102.726253;  //终点经度
    private String ADDRESS_ZHONGDIAN = "";//终点名称

    public Adapter_FoodDelivery(List<Order> orderAllList, List<Business> businessList, double longitude, double latitude, String address, Context context) {
        mOrderAllList = orderAllList;
        mBusinessList = businessList;
        this.mContext = context;
        this.LATITUDE_QIDIAN = latitude;
        this.LONGTITUDE_QIDIAN = longitude;
        this.ADDRESS_QIDIAN = address;

        loadingDialog = CommProgressDialog.createDialog(mContext, R.drawable.food_delivery_anim);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_delivery, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.btn_again_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//确认收货
                position = holder.getAdapterPosition();
                order = mOrderAllList.get(position);
                LATITUDE_ZHONGDIAN = order.getLatitude();//终点纬度
                LONGTITUDE_ZHONGDIAN = order.getLongitude();//终点经度
                ADDRESS_ZHONGDIAN = order.getOrderAddress();//终点名称
                data = new GetUserData();
                gson = new Gson();
                status = null;
                statusCode = 0;
                user = data.getUser(mContext);
                switch (holder.btn_again_buy.getText().toString()) {
                    case "配送此单":
                        if (Activity_Main.networkState == 0) {
                            Util.showToast(mContext, "请检查网络连接！");
                            return;
                        }
                        Adapter_FoodDelivery.holder = holder;
                        Util.showDialog(mContext, "配送此单", "是否确认配送此单？", DeliveryButtonListener);
                        break;
                    default:
                        break;
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mOrderAllList == null || mBusinessList == null) {
            return;
        }
        Order order = mOrderAllList.get(position);
        String orderTime = order.getOrderTime();
        Glide.with(mContext).load(order.getImageUrl()).into(holder.business_img);
        holder.foods_name.setText(order.getProductName());
        String time = DateTimeTransfer.GetDateFromLong(orderTime.substring(orderTime.lastIndexOf("(") + 1, orderTime.lastIndexOf(")") - 3));
        holder.order_time.setText(time);
        holder.foods_totalNum.setText("共" + order.getAmount() + "件商品，实付");
        holder.foods_cost.setText("￥" + order.getOrderAmount().toString());
        holder.delivery_address.setText("配送地址：" + order.getOrderAddress());
        holder.order_complete.setText(order.getOrderState());
        holder.btn_again_buy.setText("配送此单");
        if (order.getOrderState().equals("订单完成")) {
            holder.setVisibility(false);
        } else if (order.getOrderState().equals("未配送")) {
            holder.btn_again_buy.setText("配送此单");
        } else {
            holder.order_complete.setText("待配送");
        }
    }


    @Override
    public int getItemCount() {
        if (mOrderAllList == null) {
            return 0;
        } else {
            return mOrderAllList.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View item_view;
        ImageView business_img;
        TextView order_complete, foods_name, order_time, foods_totalNum, foods_cost;
        TextView delivery_address;
        Button btn_again_buy;

        public ViewHolder(View itemView) {
            super(itemView);
            item_view = itemView;
            business_img = (ImageView) itemView.findViewById(R.id.business_img);
            order_complete = (TextView) itemView.findViewById(R.id.order_complete);
            foods_name = (TextView) itemView.findViewById(R.id.foods_name);
            order_time = (TextView) itemView.findViewById(R.id.order_time);
            foods_totalNum = (TextView) itemView.findViewById(R.id.foods_totalNum);
            foods_cost = (TextView) itemView.findViewById(R.id.foods_cost);
            delivery_address = (TextView) itemView.findViewById(R.id.delivery_address);
            btn_again_buy = (Button) itemView.findViewById(R.id.btn_again_buy);
        }

        /**
         * 解决隐藏item时留空问题
         *
         * @param isVisible
         */
        public void setVisibility(boolean isVisible) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible) {
                param.height = RelativeLayout.LayoutParams.WRAP_CONTENT;// 布局的根布局类型
                param.width = RelativeLayout.LayoutParams.MATCH_PARENT;// 布局的根布局类型
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }
    }


    /**
     * 配送此单监听器
     */
    private IAlertDialogButtonListener DeliveryButtonListener = new IAlertDialogButtonListener() {
        @Override
        public void onDialogOkButtonClick() {
            foodDelivery();
        }

        @Override
        public void onDialogCancelButtonClick() {
        }

    };

    /**
     * 配送此单
     */
    private void foodDelivery() {
        loadingDialog.setMessage("正在为您规划路线...");
        loadingDialog.show();
        if (!Util.checkNetwork(mContext)) {
            loadingDialog.dismiss();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    Message message = handler.obtainMessage();
                    message.what = 1;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadingDialog.dismiss();
            switch (msg.what) {
                case 1:
                    if(LATITUDE_ZHONGDIAN<=0||LONGTITUDE_ZHONGDIAN<=0){
                         Util.showToast(mContext,"此订单未获取到经纬度信息，无法规划路线："+LATITUDE_QIDIAN+"/"+LONGTITUDE_QIDIAN+" "+ADDRESS_QIDIAN);
                    }else{
                         setUpBaiduAPPByLoca();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    /**
     * 注意下面的起终点坐标都是百度坐标，如果使用高德坐标系有很大的误差
     * mode=driving 指定导航方式，这里是驾车（riding是骑行）
     */
    void setUpBaiduAPPByLoca() {
        try {
            LATITUDE_QIDIAN=Activity_FoodDelivery.latitude;
            LONGTITUDE_QIDIAN=Activity_FoodDelivery.longitude;
            ADDRESS_QIDIAN=Activity_FoodDelivery.address;
            Util.showToast(mContext,"导航起点："+LATITUDE_QIDIAN+"/"+LONGTITUDE_QIDIAN+" "+ADDRESS_QIDIAN);
            Intent intent = Intent.getIntent("intent://map/direction?origin=latlng:" + LATITUDE_QIDIAN + "," + LONGTITUDE_QIDIAN + "|name:" + ADDRESS_QIDIAN + "&destination=latlng:" + LATITUDE_ZHONGDIAN + "," + LONGTITUDE_ZHONGDIAN + "|name:" + ADDRESS_ZHONGDIAN + "&mode=riding&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            if (isInstallByread("com.baidu.BaiduMap")) {
                mContext.startActivity(intent);

            } else {
                Util.showToast(mContext, "没有安装百度地图客户端");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
