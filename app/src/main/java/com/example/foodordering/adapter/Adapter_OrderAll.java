package com.example.foodordering.adapter;

import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodordering.Activity_Main;
import com.example.foodordering.R;
import com.example.foodordering.bean.Business;
import com.example.foodordering.bean.Order;
import com.example.foodordering.bean.Status;
import com.example.foodordering.bean.User;
import com.example.foodordering.fragment.Fragment_Order_All;
import com.example.foodordering.util.DateTimeTransfer;
import com.example.foodordering.util.GetUserData;
import com.example.foodordering.util.IAlertDialogButtonListener;
import com.example.foodordering.service.PostUtility;
import com.example.foodordering.util.Util;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by xch on 2017/3/29.
 */

public class Adapter_OrderAll extends RecyclerView.Adapter<Adapter_OrderAll.ViewHolder> {
    private Fragment_Order_All mContext;
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
    private static Dialog loadDialog; //显示正在提交数据Dialog

    public Adapter_OrderAll(List<Order> orderAllList, List<Business> businessList, Fragment_Order_All context) {
        mOrderAllList = orderAllList;
        mBusinessList = businessList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_all, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.btn_again_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//确认收货
                position = holder.getAdapterPosition();
                order = mOrderAllList.get(position);
                data = new GetUserData();
                gson = new Gson();
                status = null;
                statusCode = 0;
                user = data.getUser(mContext.getContext());
                switch (holder.btn_again_buy.getText().toString()) {
                    case "删除订单":
                        if (Activity_Main.networkState == 0) {
                            Util.showToast(mContext.getContext(), "请检查网络连接！");
                            return;
                        }
                        if (order.getCommentState().equals("未评论")) {
                            Util.showDialog(mContext.getContext(), "删除订单", "您的订单还未评价确定删除？", DeleteButtonListener);

                        } else if (order.getCommentState().equals("已评")) {
                            Util.showDialog(mContext.getContext(), "删除订单", "确定删除您的订单？", DeleteButtonListener);

                        }
                        break;
                    case "确认收货":
                        if (Activity_Main.networkState == 0) {
                            Util.showToast(mContext.getContext(), "请检查网络连接！");
                            return;
                        }
                        Adapter_OrderAll.holder = holder;
                        Util.showDialog(mContext.getContext(), "确认订单", "是否确认订单？", CompleteButtonListener);
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
        holder.order_complete.setText(order.getOrderState());
        holder.btn_again_buy.setText("删除订单");
        if (order.getOrderState().equals("订单完成")) {
            holder.btn_again_buy.setText("删除订单");
        } else if (order.getOrderState().equals("配送中")) {
            holder.btn_again_buy.setText("确认收货");
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
            btn_again_buy = (Button) itemView.findViewById(R.id.btn_again_buy);
        }
    }


    /**
     * 确认订单监听器
     */
    private IAlertDialogButtonListener CompleteButtonListener = new IAlertDialogButtonListener() {
        @Override
        public void onDialogOkButtonClick() {
            postForm = PostUtility.postCompleteOrder(order.getOrderId(), user.getId());
            if (postForm == null) {
                Util.showToast(mContext.getContext(), "确认订单失败，请重试！");
                return;
            }
            gson = new Gson();
            status = gson.fromJson(postForm, Status.class);
            statusCode = status.getStatusCode();
            if (statusCode == 200) {
                holder.btn_again_buy.setText("删除订单");
                holder.order_complete.setText("订单完成");
            }
            Util.showToast(mContext.getActivity(), status.getStatusDescription());
        }

        @Override
        public void onDialogCancelButtonClick() {

        }

    };

    /**
     * 删除订单监听器
     */
    private IAlertDialogButtonListener DeleteButtonListener = new IAlertDialogButtonListener() {

        @Override
        public void onDialogOkButtonClick() {
            postForm = PostUtility.postDeleteOrder(order.getOrderId(), user.getId());
            if (postForm == null) {
                Util.showToast(mContext.getContext(), "删除订单失败，请重试！");
                return;
            }
            gson = new Gson();
            status = gson.fromJson(postForm, Status.class);
            statusCode = status.getStatusCode();
            loadDialog.dismiss();
            if (statusCode == 200) {
                Util.showToast(mContext.getActivity(), mOrderAllList.get(position).getOrderId() + "\n" + mOrderAllList.get(position).getProductName() + "\t订单删除成功！");
            } else {
                Util.showToast(mContext.getActivity(), status.getStatusDescription());
            }
        }

        @Override
        public void onDialogCancelButtonClick() {
        }
    };
}
