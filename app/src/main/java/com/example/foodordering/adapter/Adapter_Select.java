package com.example.foodordering.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.foodordering.fragment.Fragment_home;
import com.example.foodordering.R;
import com.example.foodordering.bean.Foods;

import java.text.NumberFormat;

/**
 * Created by xch on 2017/3/3.
 */


public class Adapter_Select extends RecyclerView.Adapter<Adapter_Select.ViewHolder> {
    private Fragment_home activity;
    private SparseArray<Foods> dataList;
    private NumberFormat nf;
    private LayoutInflater mInflater;

    public Adapter_Select(Fragment_home activity, SparseArray<Foods> dataList) {
        this.activity = activity;
        this.dataList = dataList;
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        mInflater = LayoutInflater.from(activity.getContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_selected_foods, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Foods item = dataList.valueAt(position);
        holder.bindData(item);
    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Foods item;
        private TextView tvCost, tvCount, tvAdd, tvMinus, tvName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvCost = (TextView) itemView.findViewById(R.id.tvCost);
            tvCount = (TextView) itemView.findViewById(R.id.count);
            tvMinus = (TextView) itemView.findViewById(R.id.tvMinus);
            tvAdd = (TextView) itemView.findViewById(R.id.tvAdd);
            tvMinus.setOnClickListener(this);
            tvAdd.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tvAdd:
                    activity.add(item, true);
                    break;
                case R.id.tvMinus:
                    activity.remove(item, true);
                    break;
                default:
                    break;
            }
        }

        public void bindData(Foods item) {
            this.item = item;
            tvName.setText(item.getName());
            tvCost.setText(nf.format(item.count * item.getPrice()));
            tvCount.setText(String.valueOf(item.count));
        }
    }
}
