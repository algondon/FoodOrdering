package com.example.foodordering;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Status;
import com.example.foodordering.service.PostUtility;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.util.Util;
import com.google.gson.Gson;

public class Activity_WaitComment extends BaseActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {
    private RatingBar ratingBar;
    private TextView tv_tip;
    private EditText etCommentContent;
    private String orderId, userId;
    private Button btnCommit;
    private String CommentContent;
    private ProgressDialog pg;
    private Handler handler = new Handler();
    private String postFormComment;

    public Activity_WaitComment() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_comment);
        pg = new ProgressDialog(Activity_WaitComment.this);
        getData();
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("评价订单");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        ratingBar = (RatingBar) findViewById(R.id.ratingBar_Comment);
        btnCommit = (Button) findViewById(R.id.btn_commit);
        etCommentContent = (EditText) findViewById(R.id.etCommentContent);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        btnCommit.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(this);
    }


    /**
     * @param context
     * @param OrderId 订单编号
     * @param UserId  用户编号
     */
    public static void actionStart(Context context, String OrderId, String UserId) {
        Intent intent = new Intent(context, Activity_WaitComment.class);
        intent.putExtra("OrderId", OrderId);
        intent.putExtra("UserId", UserId);
        context.startActivity(intent);
    }

    private void getData() {
        Intent intent = getIntent();
        orderId = intent.getStringExtra("OrderId");
        userId = intent.getStringExtra("UserId");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_commit:
                pg.setMessage("正在提交评论...");
                pg.show();
                CommentContent = etCommentContent.getText().toString();
                final String rating = (int) ratingBar.getRating() + "";
                if (!Util.checkNetwork(this)) {
                    pg.dismiss();
                    return;
                }
                if (CommentContent.length() < 5) {
                    Toast.makeText(this, "您输入的评价内容至少5个字！", Toast.LENGTH_SHORT).show();
                    pg.dismiss();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            postFormComment = PostUtility.postCommitCommentOrder(orderId, userId, rating, CommentContent);
                            handler.post(runnableCommitComment);
                        }
                    }.start();
                }
        }
    }

    Runnable runnableCommitComment = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            if (postFormComment == null || postFormComment.isEmpty()) {
                Toast.makeText(Activity_WaitComment.this, "服务器出错，请重试！", Toast.LENGTH_SHORT).show();
            }
            if (postFormComment.length() > 100) {
                Toast.makeText(Activity_WaitComment.this, "服务器出错，请重试！", Toast.LENGTH_SHORT).show();
            } else {
                Gson gson = new Gson();
                Status status = gson.fromJson(postFormComment, Status.class);
                final int statusCode = status.getStatusCode();

                final AlertDialog.Builder builder = new AlertDialog.Builder(Activity_WaitComment.this);
                builder.setTitle("提示");
                builder.setCancelable(false);
                builder.setMessage(status.getStatusDescription());
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (statusCode == 200) {
                                    Intent intent = new Intent(Activity_WaitComment.this, Activity_Main.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                builder.create().show();
            }
        }
    };

    /**
     * 当用户交互改变分值时，触发该事件  该方法可以获取到 3个参数
     *
     * @param ratingBar 第一个参数 当前评分修改的 ratingBar
     * @param rating    当前评分分数，范围 0~星星数量
     * @param fromUser  如果评分改变是由用户触摸手势或方向键轨迹球移动触发的，则返回true
     */
    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        switch ((int) rating) {
            case 1:
                tv_tip.setText("太差");
                break;
            case 2:
                tv_tip.setText("差");
                break;
            case 3:
                tv_tip.setText("中");
                break;
            case 4:
                tv_tip.setText("很好");
                break;
            case 5:
                tv_tip.setText("优");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
