package com.example.foodordering.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.foodordering.R;
import com.example.foodordering.bean.Address;

import java.util.List;

/**
 * Created by xch on 2017/12/7.
 */

public class Adapter_Address extends RecyclerView.Adapter<Adapter_Address.ViewHolder> implements View.OnClickListener {
    private List<Address> addressList;
    private OnItemClickListener mOnItemClickListener = null;

    public Adapter_Address(List<Address> addressList) {
        this.addressList = addressList;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);//为每个item添加点击事件
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
        Address address = addressList.get(position);
        holder.tvName_Address.setText(address.getReceiveName());
        String sex = address.getSex();
        if (sex.equals("男")) {
            holder.tvSex_Address.setText("先生");
        } else if (sex.equals("女")) {
            holder.tvSex_Address.setText("女士");
        }
        holder.tvPhoneNumber_Address.setText(address.getContact());
        holder.tvAddress_Address.setText(address.getAddress());
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName_Address, tvSex_Address, tvPhoneNumber_Address, tvAddress_Address;
        ImageButton btn_delete, btn_edit;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName_Address = (TextView) itemView.findViewById(R.id.tvName_Address);
            tvSex_Address = (TextView) itemView.findViewById(R.id.tvSex_Address);
            tvPhoneNumber_Address = (TextView) itemView.findViewById(R.id.tvPhoneNumber_Address);
            tvAddress_Address = (TextView) itemView.findViewById(R.id.tvAddress_Address);
            btn_delete = (ImageButton) itemView.findViewById(R.id.btn_delete);
            btn_edit = (ImageButton) itemView.findViewById(R.id.btn_edit);
        }
    }
}
