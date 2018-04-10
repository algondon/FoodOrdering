package com.example.foodordering.request;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by xch on 2017/4/2.
 */


public class RequestTask extends AsyncTask<Request, Void, String> {
    //创建OkHttpclient
    private OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.SECONDS).build();

    //标记请求方式为get
    public static final int REQUEST_MODE_GET = 0;
    //标记请求方式为post
    public static final int REQUEST_MODE_POST = 1;

    /**
     * 执行网络请求
     *
     * @param url          请求的url地址.
     * @param REQUEST_MODE 标记请求方式get和post
     *                     （REQUEST_MODE_GET和REQUEST_MODE_POST）
     * @param args         为请求添加的参数
     * @return 返回请求的文本信息
     */
    @Nullable
    public void executeRequest(String url, int REQUEST_MODE, String... args) {

        switch (REQUEST_MODE) {
            case REQUEST_MODE_GET:
                requestGET(url, args);
                break;
            case REQUEST_MODE_POST:
                requestPOST(url, args);
                break;
        }
    }

    /**
     * @param url    请求的URL地址
     * @param params 请求所需的参数
     */
    private void requestPOST(String url, String... params) {
        FormBody.Builder builder = new FormBody.Builder();
        for (int i = 0; i < params.length; i += 2) {
            builder.add(params[i], params[i + 1]);
        }
        Request request = new Request.Builder().post(builder.build()).url(url).build();
        this.execute(request);
    }

    /**
     * @param params 封装的好的Request请求
     */
    @Override
    protected String doInBackground(Request... params) {
        //生成执行Request的Call
        Call call = okHttpClient.newCall(params[0]);
        try {
            //执行call
            Response response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param url    请求的URL地址
     * @param params 请求所需的参数
     */
    private void requestGET(String url, String... params) {
        //将参数添加到URL中
        String parameter = "";
        for (int i = 0; i < params.length; i += 2) {
            parameter = "?" + params[i] + "=" + params[i + 1];
        }
        url = url + parameter;
        //构造Request
        Request request = new Request.Builder().url(url).get().build();
        //将请求提交到子线程中执行
        this.execute(request);
    }
}
