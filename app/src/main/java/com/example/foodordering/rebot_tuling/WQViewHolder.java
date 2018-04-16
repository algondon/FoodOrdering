package com.example.foodordering.rebot_tuling;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by xch on 2018/4/16.
 */
public class WQViewHolder extends RecyclerView.ViewHolder {
    View convertView;
    Context context;

    public WQViewHolder(View itemView, Context context) {
        super(itemView);
        this.convertView = itemView;
        this.context = context;
    }

    public View getItemView() {
        return convertView;
    }

    public void setText(int id, String text) {
        TextView tx = (TextView) convertView.findViewById(id);
        tx.setText(text);
    }

    public void setText(int id, String text, final OnClickListener onClickListener) {
        TextView tx = (TextView) convertView.findViewById(id);
        tx.setText(text);
        tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClickListner(v);
            }
        });
    }


    public void setImageResource(Context context, int id, int resouceId) {
        ImageView img = (ImageView) convertView.findViewById(id);
        Glide.with(context).load(resouceId).into(img);
    }

    public void setImageResource(Context context, int id, String url) {
        ImageView img = (ImageView) convertView.findViewById(id);
        Glide.with(context).load(url).into(img);
    }


    public View getView(int id) {
        return convertView.findViewById(id);
    }

    public interface OnClickListener {
        void onClickListner(View v);
    }

}
