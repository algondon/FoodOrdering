package com.example.foodordering;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.adapter.LeftListAdapter;
import com.example.foodordering.adapter.Adapter_FoodsCategory;
import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.SetMeal;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.util.Util;
import com.example.foodordering.widget.PinnedHeaderListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 套餐
 */
public class Activity_FoodsCategory extends BaseActivity {
    private Handler handler = new Handler();
    private ProgressDialog pg;
    private static List<SetMeal> setMealList = new ArrayList<>();//套餐
    private ListView leftListview;
    private PinnedHeaderListView pinnedListView;
    private boolean isScroll = true;
    private LeftListAdapter adapter;

    private String[] leftStr = new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private boolean[] flagArray = {true, false, false, false, false, false, false, false, false};
    // 二级列表
    private List<List<SetMeal>> SetMealArray;
    List<SetMeal> tempArray0 = new ArrayList<>();
    List<SetMeal> tempArray1 = new ArrayList<>();
    List<SetMeal> tempArray2 = new ArrayList<>();
    List<SetMeal> tempArray3 = new ArrayList<>();
    List<SetMeal> tempArray4 = new ArrayList<>();
    List<SetMeal> tempArray5 = new ArrayList<>();
    List<SetMeal> tempArray6 = new ArrayList<>();

    private void findViewById() {
        leftListview = (ListView) findViewById(R.id.left_listview);
        pinnedListView = (PinnedHeaderListView) findViewById(R.id.pinnedListView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_category);
        pg = new ProgressDialog(Activity_FoodsCategory.this);
        SetMealArray = new ArrayList<>();
        findViewById();
        getCategoryFoodsList();
    }

    /**
     * 获取套餐信息
     */
    private void getCategoryFoodsList() {
        pg.setMessage("加载数据中...");
        pg.show();
        if (!Util.checkNetwork(this)) {
            pg.dismiss();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                setMealList = RequestUtility.getSetMealList();
                handler.post(runnableCategoryFoods);
            }
        }.start();
    }

    Runnable runnableCategoryFoods = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            if (setMealList != null) {
                for (int i = 0; i < setMealList.size(); i++) {
                    String data = setMealList.get(i).getDate();
                    if (getWeekByData(data).equals("星期一")) {
                        tempArray0.add(setMealList.get(i));
                        SetMealArray.add(tempArray0);
                    }
                    if (getWeekByData(data).equals("星期二")) {
                        tempArray1.add(setMealList.get(i));
                        SetMealArray.add(tempArray1);
                    }
                    if (getWeekByData(data).equals("星期三")) {
                        tempArray2.add(setMealList.get(i));
                        SetMealArray.add(tempArray2);
                    }
                    if (getWeekByData(data).equals("星期四")) {
                        tempArray3.add(setMealList.get(i));
                        SetMealArray.add(tempArray3);
                    }
                    if (getWeekByData(data).equals("星期五")) {
                        tempArray4.add(setMealList.get(i));
                        SetMealArray.add(tempArray4);
                    }
                    if (getWeekByData(data).equals("星期六")) {
                        tempArray5.add(setMealList.get(i));
                        SetMealArray.add(tempArray5);
                    }
                    if (getWeekByData(data).equals("星期日")) {
                        tempArray6.add(setMealList.get(i));
                        SetMealArray.add(tempArray6);
                    }


                }
            }
            initView();
        }
    };

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("老杨家地道菜馆");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Adapter_FoodsCategory sectionedAdapter = new Adapter_FoodsCategory(this, leftStr,SetMealArray);

        pinnedListView.setAdapter(sectionedAdapter);
        adapter = new LeftListAdapter(this, leftStr, flagArray);
        leftListview.setAdapter(adapter);
        leftListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                try {
                    isScroll = false;

                    for (int i = 0; i < leftStr.length; i++) {
                        if (i == position) {
                            flagArray[i] = true;
                        } else {
                            flagArray[i] = false;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    int rightSection = 0;
                    for (int i = 0; i < position; i++) {
                        rightSection += sectionedAdapter.getCountForSection(i) + 1;
                    }
                    pinnedListView.setSelection(rightSection);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(Activity_FoodsCategory.this, "未获取到数据", Toast.LENGTH_SHORT).show();
                } finally {

                }

            }

        });

        pinnedListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (pinnedListView.getLastVisiblePosition() == (pinnedListView.getCount() - 1)) {
                            leftListview.setSelection(ListView.FOCUS_DOWN);
                        }

                        // 判断滚动到顶部
                        if (pinnedListView.getFirstVisiblePosition() == 0) {
                            leftListview.setSelection(0);
                        }

                        break;
                }
            }

            int y = 0;
            int x = 0;
            int z = 0;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (isScroll) {
                    for (int i = 0; i < SetMealArray.size(); i++) {
                        if (i == sectionedAdapter.getSectionForPosition(pinnedListView.getFirstVisiblePosition())) {
                            flagArray[i] = true;
                            x = i;
                        } else {
                            flagArray[i] = false;
                        }
                    }
                    if (x != y) {
                        adapter.notifyDataSetChanged();
                        y = x;
                        if (y == leftListview.getLastVisiblePosition()) {
//                            z = z + 3;
                            leftListview.setSelection(z);
                        }
                        if (x == leftListview.getFirstVisiblePosition()) {
//                            z = z - 1;
                            leftListview.setSelection(z);
                        }
                        if (firstVisibleItem + visibleItemCount == totalItemCount - 1) {
                            leftListview.setSelection(ListView.FOCUS_DOWN);
                        }
                    }
                } else {
                    isScroll = true;
                }
            }
        });
    }

    /**
     * toolbar的action按钮点击事件
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
            case R.id.call:
                String phoneNumber = "18468182835";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.like:
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 加载toolbar菜单文件
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.categoryfd_toolbar, menu);
        return true;
    }

    /**
     * 根据日期判断是星期几
     *
     * @param pTime
     * @return
     */
    private String getWeekByData(String pTime) {
        String Week = "星期";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                Week += "日";
                break;
            case 2:
                Week += "一";
                break;
            case 3:
                Week += "二";
                break;
            case 4:
                Week += "三";
                break;
            case 5:
                Week += "四";
                break;
            case 6:
                Week += "五";
                break;
            case 7:
                Week += "六";
                break;
            default:
                break;
        }
        return Week;
    }
}
