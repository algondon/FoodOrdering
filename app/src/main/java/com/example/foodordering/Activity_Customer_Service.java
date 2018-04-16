package com.example.foodordering;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.rebot_tuling.Activity_RebotNews;
import com.example.foodordering.rebot_tuling.Activity_RebotWeb;
import com.example.foodordering.rebot_tuling.GetRebotQueryInfo;
import com.example.foodordering.rebot_tuling.RebotAdapter;
import com.example.foodordering.rebot_tuling.RebotBean;
import com.example.foodordering.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Customer_Service extends BaseActivity implements View.OnClickListener {
    private Button btn_send;
    private ListView mListView;
    private EditText edit_info;
    List<RebotBean> list = new ArrayList<RebotBean>();
    private RebotAdapter adapter;
    String msge;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String backData = (String) msg.obj;
                    String text;
                    String code;
                    Log.e("backdata", backData);
                    try {
                        JSONObject obj = new JSONObject(backData);
                        text = obj.getString("text");
                        RebotBean bean = new RebotBean();
                        bean.setType(1);
                        bean.setText(text);
                        list.add(bean);
                        adapter.notifyDataSetChanged();// 提醒adapter更新数据
                        mListView.setSelection(list.size() - 1);
                        code = obj.getString("code");
                        Log.e("code***", code);

                        if (code.equals("200000")) {
                            Intent intent = new Intent(Activity_Customer_Service.this, Activity_RebotWeb.class);
                            Log.e("url***", obj.getString("url"));
                            intent.putExtra("url", obj.getString("url"));
                            startActivity(intent);
                        } else if (code.equals("302000")) {//302000 新闻
                            Intent intent = new Intent(Activity_Customer_Service.this, Activity_RebotNews.class);
                            intent.putExtra("result", backData);
                            intent.putExtra("title", msge);
                            intent.putExtra("code", "302000");
                            startActivity(intent);
                        } else if (code.equals("308000")) {//308000 菜谱
                            Intent intent = new Intent(Activity_Customer_Service.this, Activity_RebotNews.class);
                            intent.putExtra("result", backData);
                            intent.putExtra("title", msge);
                            intent.putExtra("code", "308000");
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
        initView();
        RebotBean bean = new RebotBean();
        bean.setType(1);
        bean.setText("欢迎来到懒人外卖客服中心，请问我有什么可以帮助你的吗？");
        list.add(bean);
        adapter = new RebotAdapter(this, list);
        mListView.setAdapter(adapter);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Activity_Customer_Service.class);
        context.startActivity(intent);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("客服中心");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mListView = (ListView) findViewById(R.id.rebot_listView);
        edit_info = (EditText) findViewById(R.id.rebot_info);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send://发送
                msge = edit_info.getText().toString().trim();
                if (msge.equals("")) {
                    return;
                }
                edit_info.setText("");
                RebotBean bean = new RebotBean();
                bean.setType(2);
                bean.setText(msge);
                list.add(bean);
                adapter.notifyDataSetChanged();// adapter更新数据
                mListView.setSelection(list.size() - 1);// 将滚屏定位到最后一条item
                if (!Util.checkNetwork(Activity_Customer_Service.this)) {
                    RebotBean bean1 = new RebotBean();
                    bean1.setType(1);
                    bean1.setText("当前网络不可用");
                    list.add(bean1);
                    adapter.notifyDataSetChanged();
                    mListView.setSelection(list.size() - 1);
                    return;
                }

                new Thread() {
                    public void run() {
                        String netMsg = GetRebotQueryInfo.getMsg(msge);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = netMsg;
                        handler.sendMessage(msg);
                    }
                }.start();

                break;
            default:
                break;
        }
    }
}
