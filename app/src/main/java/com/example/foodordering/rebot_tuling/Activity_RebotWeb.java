package com.example.foodordering.rebot_tuling;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.foodordering.R;

public class Activity_RebotWeb extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebot_web);
        String url = getIntent().getStringExtra("url");
        webView = (WebView) findViewById(R.id.webview_webview);
        progressBar = (ProgressBar) findViewById(R.id.webview_progress);
        toolbar = (Toolbar) findViewById(R.id.webview_toolbar);
        toolbar.setTitle("详细内容");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initWebView();
        initWebViewListener();
        webView.loadUrl(url);
    }

    private void initWebViewListener() {
        webView.setWebViewClient(new WebViewClient() {
            //加载的时候
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url == null) return false;
                try {
                    if (url.startsWith("http:") || url.startsWith("https:"))
                    {
                        view.loadUrl(url);
                        return true;
                    }
                    else
                    {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                    return false;
                }

            }
            //网络加载错误的时候回调
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }
            //webview开始加载时候触发
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
            //webview加载完成的时候
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });
    }

    private void initWebView() {
        // 设置编码
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        webView.getSettings().setTextZoom(70);
        // 设置背景颜色 透明
        webView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置缓存模式
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        // //添加Javascript调用java对象
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        // 扩大比例的缩放设置此属性，可任意比例缩放。
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBlockNetworkImage(false);
        // 不启用硬件加速
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // 自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        webView.canGoBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
