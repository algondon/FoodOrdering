package com.example.foodordering.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.foodordering.Activity_Main;
import com.example.foodordering.R;
import com.example.foodordering.adapter.Adapter_tab_Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xch on 2017/2/23.
 */

public class Fragment_order extends Fragment {
    private TabLayout orderFragment_title;                            //定义TabLayout
    private ViewPager vp_orderFragment_pager;                             //定义viewPager
    private FragmentPagerAdapter fAdapter;                               //定义adapter
    private List<Fragment> list_fragment;                                //定义要装fragment的列表
    private List<String> list_title;                                     //tab名称列表
    private Fragment_Order_All order_all_fragment;                       //所有订单fragment
    private Fragment_Order_WaitComment order_waitComment_fragment;        //待评价fragment



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_order,container,false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView(View view) {
        orderFragment_title = (TabLayout)view.findViewById(R.id.tab_orderFragment_title);
        vp_orderFragment_pager = (ViewPager)view.findViewById(R.id.vp_orderFragment_pager);

        //初始化各fragment
        order_all_fragment = new Fragment_Order_All();
        order_waitComment_fragment = new Fragment_Order_WaitComment();

        //将fragment装进列表中
        list_fragment = new ArrayList<>();
        list_fragment.add(order_all_fragment);
        list_fragment.add(order_waitComment_fragment);

        //将名称加载tab名字列表
        list_title = new ArrayList<>();
        list_title.add("全部订单");
        list_title.add("待评价");

        //设置TabLayout的模式
        orderFragment_title.setTabMode(TabLayout.MODE_FIXED);
        //为TabLayout添加tab名称
        orderFragment_title.addTab(orderFragment_title.newTab().setText(list_title.get(0)));
        orderFragment_title.addTab(orderFragment_title.newTab().setText(list_title.get(1)));

        fAdapter = new Adapter_tab_Order(getChildFragmentManager(),list_fragment,list_title);

        //viewpager加载adapter
        vp_orderFragment_pager.setAdapter(fAdapter);
        //默认选中
        orderFragment_title.getTabAt(0).select();
        //和ViewPager联动起来
        orderFragment_title.setupWithViewPager(vp_orderFragment_pager);
        if (Activity_Main.networkState == 0) {
            Toast.makeText(getActivity(), "网络连接失败，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
        }
    }
}
