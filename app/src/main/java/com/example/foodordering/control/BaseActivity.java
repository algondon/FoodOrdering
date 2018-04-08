package com.example.foodordering.control;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by xch on 2017/5/14.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);//将Activity加入集合
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);//将Activity从集合移除
    }
}
