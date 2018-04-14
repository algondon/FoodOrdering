package com.example.foodordering;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Comment;
import com.example.foodordering.adapter.Adapter_Comment;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.util.Util;
import com.example.foodordering.widget.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;


public class Activity_MyComment extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    private View mEmptyView;
    private EmptyRecyclerView recyclerView;
    private List<Comment> commentList = new ArrayList<>();//我的评价
    private String userId;//用户ID
    private SwipeRefreshLayout swipeRefreshLayout;
    private Adapter_Comment adapter;
    //用于刷新
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private ProgressDialog pg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycomment);
        pg=new ProgressDialog(Activity_MyComment.this);
        getUserData();
        initView();
    }

    private void initView() {
        mEmptyView=findViewById(R.id.id_empty_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("我的评价");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = (EmptyRecyclerView) findViewById(R.id.commentsRecyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        if (!Util.checkNetwork(Activity_MyComment.this)) {
            return;
        }
        showList();
    }

    private void showList() {
        pg.setMessage("数据加载中...");
        pg.show();
        if (!Util.checkNetwork(Activity_MyComment.this)) {
            pg.dismiss();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                try {
                    commentList = RequestUtility.getCommentList(userId);
                    handler.post(runnableComment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    Runnable runnableComment = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            adapter = new Adapter_Comment(commentList, Activity_MyComment.this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(Activity_MyComment.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setEmptyView(mEmptyView);
        }
    };

    /**
     * toolbar中的菜单选中监听
     *
     * @param item
     * @return
     */
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

    /**
     * @param context
     * @param userId  用户编号
     */
    public static void actionStart(Context context, String userId) {
        Intent intent = new Intent(context, Activity_MyComment.class);
        intent.putExtra("userId", userId);
        context.startActivity(intent);
    }

    /**
     * 获取用户数据（这里主要获取用户ID）
     */
    private void getUserData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
    }


    /**
     * 刷新事件方法
     */
    @Override
    public void onRefresh() {
        showList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //刷新控件停止两秒后消失
                    Thread.sleep(1000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //adapter.notifyDataSetChanged();
                            //停止刷新
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(Activity_MyComment.this, "刷新成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
