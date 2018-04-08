package com.example.foodordering.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodordering.Activity_Main;
import com.example.foodordering.R;
import com.example.foodordering.bean.Business;
import com.example.foodordering.bean.Order;
import com.example.foodordering.bean.User;
import com.example.foodordering.adapter.Adapter_WaitComment;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.util.GetUserData;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.widget.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xch on 2017/2/23.
 */

public class Fragment_Order_WaitComment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View  mEmptyView;//无数据视图
    private EmptyRecyclerView recyclerView;
    private List<Order> orderAllList = new ArrayList<>();//订单列表
    private List<Business> businessList = new ArrayList<>();//商家列表
    private User user;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private ProgressDialog pg;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_waitcomment, container, false);
        pg=new ProgressDialog(getActivity());
        GetUserData data = new GetUserData();
        user = data.getUser(getContext());
        getBusinessData();
        initView(view);
        showList();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        mEmptyView=view.findViewById(R.id.id_empty_view);
        recyclerView = (EmptyRecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        //刷新控件
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutWaitComment);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void getBusinessData() {
        Activity_Main activityMain = (Activity_Main) getActivity();
        businessList = activityMain.businessList;
    }

    private void showList() {
        pg.setMessage("数据加载中...");
        pg.show();
        if (Activity_Main.networkState == 0) {
            pg.dismiss();
            Toast.makeText(getActivity(), "网络连接失败，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    // 获取订单
                    orderAllList = RequestUtility.getWaitCommentOrders(user.getId());
                    handler.post(runnableWCO);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

    }
    Runnable runnableWCO=new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            Adapter_WaitComment adapter = new Adapter_WaitComment(orderAllList, businessList, Fragment_Order_WaitComment.this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setEmptyView(mEmptyView);
        }
    };

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
                                    swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
