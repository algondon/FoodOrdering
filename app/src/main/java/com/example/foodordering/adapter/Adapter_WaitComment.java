package com.example.foodordering.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodordering.R;
import com.example.foodordering.Activity_WaitComment;
import com.example.foodordering.bean.Business;
import com.example.foodordering.bean.Order;
import com.example.foodordering.bean.User;
import com.example.foodordering.fragment.Fragment_Order_WaitComment;
import com.example.foodordering.util.DateTimeTransfer;
import com.example.foodordering.util.GetUserData;

import java.util.List;

/**
 * Created by xch on 2017/5/24.
 */

public class Adapter_WaitComment extends RecyclerView.Adapter<Adapter_WaitComment.ViewHolder> {
    private Fragment_Order_WaitComment mContext;
    private List<Order> mOrderAllList;
    private List<Business> mBusinessList;
    private User user;

    public Adapter_WaitComment(List<Order> orderAllList, List<Business> businessList, Fragment_Order_WaitComment context) {
        mOrderAllList = orderAllList;
        mBusinessList = businessList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_all, parent, false);
        final Adapter_WaitComment.ViewHolder holder = new Adapter_WaitComment.ViewHolder(view);
        holder.btn_again_buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                final Order order = mOrderAllList.get(position);
                GetUserData data = new GetUserData();
                user = data.getUser(mContext.getContext());
                Activity_WaitComment.actionStart(mContext.getContext(),order.getOrderId(),user.getId());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Order order = mOrderAllList.get(position);
        if(mBusinessList==null)
        {
            return;
        }
        Glide.with(mContext).load(order.getImageUrl()).into(holder.business_img);
        String orderTime=order.getOrderTime();
        holder.foods_name.setText(order.getProductName());
        String time= DateTimeTransfer.GetDateFromLong(orderTime.substring(orderTime.lastIndexOf("(")+1,orderTime.lastIndexOf(")")-3));
        holder.foods_name.setText(order.getProductName());
        holder.order_time.setText(time);
        holder.foods_totalNum.setText("共" + order.getAmount() + "件商品，实付");
        holder.foods_cost.setText("￥" + order.getOrderAmount().toString());
        holder.order_complete.setText(order.getOrderState());
        if (order.getOrderState().equals("订单完成")) {
            holder.btn_again_buy.setText("订单评价");
        }
    }

    @Override
    public int getItemCount() {
        if( mOrderAllList==null)
        {
            return 0;
        }
        else{
            return mOrderAllList.size();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View item_view;
        ImageView business_img;
        TextView  order_complete, foods_name, order_time, foods_totalNum, foods_cost;
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
}
