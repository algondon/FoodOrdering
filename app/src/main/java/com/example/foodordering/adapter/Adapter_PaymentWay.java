package com.example.foodordering.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodordering.Activity_Payment;
import com.example.foodordering.R;
import com.example.foodordering.bean.PaymentWay;

import java.util.List;

/**
 * Created by xch on 2017/3/27.
 */

public class Adapter_PaymentWay extends RecyclerView.Adapter<Adapter_PaymentWay.ViewHolder> {
    private List<PaymentWay> paymentWayList;
    private int selectedPos = -1;//变量保存当前选中的position

    public Adapter_PaymentWay(List<PaymentWay> paymentWayList) {
        this.paymentWayList = paymentWayList;
        //设置数据集时，找到默认选中的pos
        for (int i = 0; i < paymentWayList.size(); i++) {
            if (paymentWayList.get(i).isSelected()) {//为ture时就是选中项
                selectedPos = i;
                Activity_Payment.selected_position = selectedPos;
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_way, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final PaymentWay paymentWay = paymentWayList.get(position);
        holder.pay_img.setImageResource(paymentWay.getPayImg());
        holder.pay_name.setText(paymentWay.getPayName());
        holder.pay_hint.setText(paymentWay.getPayHint());
        holder.imgv_payChoose.setSelected(paymentWay.isSelected());
        holder.imgv_payChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // 每次点击时，先将所有的selected设为false，并且将当前点击的item设为true，刷新整个视图
//                for (PaymentWay payWay : paymentWayList) {
//                    payWay.setSelected(false);
//                }
//                paymentWay.setSelected(true);
//                notifyDataSetChanged();

                //如果勾选的不是已经勾选状态的Item
                Activity_Payment.selected_position = position;
                if (selectedPos != position) {
                    //先取消上个item的勾选状态
                    paymentWayList.get(selectedPos).setSelected(false);
                    notifyItemChanged(selectedPos);
                    //设置新Item的勾选状态
                    selectedPos = position;
                    paymentWayList.get(selectedPos).setSelected(true);
                    notifyItemChanged(selectedPos);
                }
            }
        });
        holder.item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity_Payment.selected_position = position;
                if (selectedPos != position) {
                    paymentWayList.get(selectedPos).setSelected(false);
                    notifyItemChanged(selectedPos);
                    selectedPos = position;
                    paymentWayList.get(selectedPos).setSelected(true);
                    notifyItemChanged(selectedPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return paymentWayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View item_view;
        ImageView pay_img;
        TextView pay_name, pay_hint;
        ImageView imgv_payChoose;

        public ViewHolder(View itemView) {
            super(itemView);
            item_view = itemView;
            pay_img = (ImageView) itemView.findViewById(R.id.pay_img);
            pay_name = (TextView) itemView.findViewById(R.id.pay_name);
            pay_hint = (TextView) itemView.findViewById(R.id.pay_hint);
            imgv_payChoose = (ImageView) itemView.findViewById(R.id.imgv_paychoose);
        }
    }
}
