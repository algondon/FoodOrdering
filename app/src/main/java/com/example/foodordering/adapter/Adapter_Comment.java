package com.example.foodordering.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodordering.Activity_MyComment;
import com.example.foodordering.bean.Comment;
import com.example.foodordering.R;
import com.example.foodordering.util.DateTimeTransfer;

import java.util.List;


/**
 * Created by xch on 2017/5/29.
 */

public class Adapter_Comment extends RecyclerView.Adapter<Adapter_Comment.ViewHolder> {
    private Activity_MyComment mContext;
    private List<Comment> mCommentList;

    public Adapter_Comment(List<Comment> commentsList, Activity_MyComment mContext) {
        this.mCommentList = commentsList;
        this.mContext = mContext;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View comment_item_view;
        ImageView food_img;
        TextView tv_food_name, tv_content,tv_time;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            comment_item_view = itemView.findViewById(R.id.comment_item_view);

            food_img = (ImageView) itemView.findViewById(R.id.comment_food_img);
            tv_food_name = (TextView) itemView.findViewById(R.id.comment_food_name);
            tv_time = (TextView) itemView.findViewById(R.id.comment_time);
            ratingBar = (RatingBar) itemView.findViewById(R.id.comment_ratingBar);
            tv_content = (TextView) itemView.findViewById(R.id.comment_food_content);

        }
    }

    @Override
    public Adapter_Comment.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        final Adapter_Comment.ViewHolder holder = new Adapter_Comment.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Adapter_Comment.ViewHolder holder, int position) {
        final Comment comment = mCommentList.get(position);
        String time= DateTimeTransfer.GetDateFromLong(comment.getCommentDateTime().substring(comment.getCommentDateTime().lastIndexOf("(")+1,comment.getCommentDateTime().lastIndexOf(")")-3));
        holder.ratingBar.setRating(comment.getRating());
        holder.tv_content.setText(comment.getCommentContent());
        holder.tv_time.setText(time);
        holder.tv_food_name.setText(comment.getName());
        Glide.with(mContext).load(comment.getImageUrl()).into(holder.food_img);
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }
}