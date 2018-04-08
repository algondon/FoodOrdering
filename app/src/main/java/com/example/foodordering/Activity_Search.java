package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.foodordering.control.BaseActivity;

public class Activity_Search extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    /**
     * 跳转到此页面
     * @param context
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Activity_Search.class);
        context.startActivity(intent);
    }
}
