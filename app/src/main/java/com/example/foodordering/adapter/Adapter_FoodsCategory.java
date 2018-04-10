package com.example.foodordering.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodordering.R;
import com.example.foodordering.bean.SetMeal;

import java.util.List;


/**
 * 基本功能：右侧Adapter
 * 创建：王杰
 * 创建时间：2018/03/10
 */
public class Adapter_FoodsCategory extends SectionedBaseAdapter {

    private Context mContext;
    private String[] leftStr;
    private List<List<SetMeal>> setMealArray;

    public Adapter_FoodsCategory(Context context, String[] leftStr, List<List<SetMeal>> SetMealArray) {
        this.mContext = context;
        this.leftStr = leftStr;
        this.setMealArray = SetMealArray;
    }

    @Override
    public Object getItem(int section, int position) {
        return setMealArray.get(section).get(position);
    }

    @Override
    public long getItemId(int section, int position) {
        return position;
    }

    @Override
    public int getSectionCount() {
        return setMealArray.size();
    }

    @Override
    public int getCountForSection(int section) {
        return setMealArray.get(section).size();
    }

    @Override
    public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.layout_category_right_list_item, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        SetMeal setMeal = setMealArray.get(section).get(position);
        String food_name = "主食："+setMeal.getStapleFood() + "\n肉类：" + setMeal.getMeatDishes() + "\n素菜：" + setMeal.getVegetarianDishes1() + "+" + setMeal.getVegetarianDishes2();
        ((TextView) layout.findViewById(R.id.food_name)).setText(food_name);
        ((TextView)layout.findViewById(R.id.tv_foods_energy)).setText("能量值："+setMeal.getEnergy()+"千焦");
        ((TextView) layout.findViewById(R.id.tv_foods_month_sales)).setText("月销售:"+setMeal.getMonthSellCount()+"份");
        ((TextView) layout.findViewById(R.id.tv_food_price)).setText("￥ "+setMeal.getPrice());
        ((RatingBar) layout.findViewById(R.id.ratingBar)).setProgress(setMeal.getRating());
        Glide.with(mContext).load(setMeal.getImageUrl()).into((ImageView) layout.findViewById(R.id.food_img));
        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            }
        });
        return layout;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {
        LinearLayout layout = null;
        if (convertView == null) {
            LayoutInflater inflator = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inflator.inflate(R.layout.layout_category_header_item, null);
        } else {
            layout = (LinearLayout) convertView;
        }
        layout.setClickable(false);
        ((TextView) layout.findViewById(R.id.textItem)).setText(leftStr[section]);
        return layout;
    }

}
