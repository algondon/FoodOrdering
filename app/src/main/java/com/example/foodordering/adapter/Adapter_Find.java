package com.example.foodordering.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.foodordering.R;
import com.example.foodordering.bean.Find_Store;

import java.util.List;

/**
 * Created by xch on 2018/3/20.
 */

public class Adapter_Find extends BaseAdapter {
    private Context context;
    private List<Find_Store> list_findStore;

    public Adapter_Find(Context context, List<Find_Store> list_findStore) {
        this.context = context;
        this.list_findStore = list_findStore;
    }

    @Override
    public int getCount() {
        return list_findStore.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.find_store_item,
                    parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) view.findViewById(R.id.item_img);
            viewHolder.zhuan = (ImageView) view.findViewById(R.id.item_zhuan);
            viewHolder.su = (ImageView) view.findViewById(R.id.item_su);
            viewHolder.piao = (ImageView) view.findViewById(R.id.item_piao);
            viewHolder.name= (TextView) view.findViewById(R.id.item_name);
            viewHolder.price= (TextView) view.findViewById(R.id.item_min_price);
            viewHolder.rating= (RatingBar) view.findViewById(R.id.item_rating);
            viewHolder.eva_per_month= (TextView) view.findViewById(R.id.item_eva_per_month);
            viewHolder.delivery_price= (TextView) view.findViewById(R.id.item_extra_price);
            viewHolder.distances= (TextView) view.findViewById(R.id.item_distance);
            viewHolder.MonthSellCount= (TextView) view.findViewById(R.id.item_number_per_month);
            viewHolder.item_man= (TextView) view.findViewById(R.id.item_man);
            viewHolder.di= (TextView) view.findViewById(R.id.item_di);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();// 重新获取viewHolder
        }

        Find_Store find_store = list_findStore.get(position);
        viewHolder.image.setImageResource(find_store.getImage());
        viewHolder.name.setText(find_store.getName());
        viewHolder.price.setText("￥"+find_store.getPrice()+"");
        viewHolder.rating.setProgress(find_store.getRating());
        viewHolder.eva_per_month.setText(find_store.getEva_per_month()+"");
        viewHolder.delivery_price.setText("￥"+find_store.getDelivery_price()+"");
        viewHolder.distances.setText(find_store.getDistances()+"公里");
        viewHolder.MonthSellCount.setText(find_store.getMonthSellCount());
        viewHolder.item_man.setText("满"+find_store.getMan_totals()+"元立减"+find_store.getMan_salls()+"元");
        viewHolder.di.setText("在该商家使用抵金券订餐可抵"+find_store.getDi()+"元");
        boolean is_zhuan=find_store.isZhuan();
        boolean is_su=find_store.isSu();
        boolean is_piao=find_store.isPiao();
        if(is_zhuan){
            viewHolder.zhuan.setVisibility(View.VISIBLE);
        }else {
            viewHolder.zhuan.setVisibility(View.GONE);
        }
        if(is_su){
            viewHolder.su.setVisibility(View.VISIBLE);
        }else {
            viewHolder.su.setVisibility(View.GONE);
        }
        if(is_piao){
            viewHolder.piao.setVisibility(View.VISIBLE);
        }else {
            viewHolder.piao.setVisibility(View.GONE);
        }
        return view;
    }

    class ViewHolder {
        ImageView image, zhuan, su, piao;
        TextView name, price, eva_per_month, delivery_price, distances, MonthSellCount, item_man,di;
        RatingBar rating;
    }

}
