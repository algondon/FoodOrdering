package com.example.foodordering.weather.ui.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.foodordering.R;
import com.example.foodordering.weather.dao.bean.OptDO;

/**
 * Created by xch on 2018/3/10.
 */
public class OptMenuAdapter extends BaseAdapter {
    private Activity context;
    private List<OptDO> optDOs;

    public OptMenuAdapter(List<OptDO> optDOs, Activity context) {
        this.optDOs = optDOs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return optDOs.size();
    }

    @Override
    public Object getItem(int position) {
        return optDOs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;


        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.opt_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_icon);
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            convertView.setBackgroundResource(R.drawable.ripple);
//        }

        final OptDO optDO = optDOs.get(position);

        holder.icon.setImageResource(optDO.getIcon());
        holder.icon.setColorFilter(context.getResources().getColor(R.color.colorPrimary));
        holder.title.setText(optDO.getTltle());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }


    public class ViewHolder {
        public TextView title;
        public ImageView icon;
    }
}
