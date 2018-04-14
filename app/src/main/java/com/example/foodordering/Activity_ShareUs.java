package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xyzlf.share.library.bean.ShareEntity;
import com.xyzlf.share.library.interfaces.ShareConstant;
import com.xyzlf.share.library.util.ShareUtil;

public class Activity_ShareUs extends AppCompatActivity {
    private ImageView iv_qrcode_download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_us);
        initView();
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Activity_ShareUs.class);
        context.startActivity(intent);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("分享我们");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        iv_qrcode_download= (ImageView) findViewById(R.id.iv_qrcode_download);

        Glide.with(Activity_ShareUs.this).load("http://123.207.239.170/FoodOrdering/qrcode/foodordering_apk_download.png").into(iv_qrcode_download);
        iv_qrcode_download.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showShareDialog();
                return true;
            }
        });
    }

    /**
     * 弹出分享对话框
     */
    public void showShareDialog() {
        ShareEntity testBean = new ShareEntity("懒人外卖", "点击此推送即可下载懒人外卖App。");
        testBean.setUrl("http://123.207.239.170/FoodOrdering/apk/foodOrdering.apk"); //分享链接
        testBean.setImgUrl("http://123.207.239.170/FoodOrdering/share/img_share.jpg");
        ShareUtil.showShareDialog(this, testBean, ShareConstant.REQUEST_CODE);
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
}
