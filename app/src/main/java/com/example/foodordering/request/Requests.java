package com.example.foodordering.request;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.example.foodordering.bean.Foods;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * Created by xch on 2017/4/2.
 */

public class Requests implements RequestManager {
    private static OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * @param url     请求的URL地址
     * @param typeOfT 需要绑定的对象的类型
     * @param params  （可选）需要添加的参数
     */
    @Override
    public <T> T request(String url, Type typeOfT, String... params) throws Exception {
        //请求获得model数据
        RequestTask requestTask = new RequestTask();
        requestTask.executeRequest(url, RequestTask.REQUEST_MODE_GET, params);
        String result = requestTask.get();
        //将获取的数据反序列化为对象
        Gson gson = new Gson();
        T target = gson.fromJson(result, typeOfT);
        return target;
    }

    /**
     * @param context 上下文对象
     * @param view    图片绑定的视图
     * @param url     图片下载的url
     */
    @Override
    public void getImage(Context context, View view, String url) throws Exception {
        Picasso.with(context).load(url).into((ImageView) view);
    }

    /**
     * @param url    需要提交请求的url
     * @param params 表单参数键值对
     */
    @Override
    public String postForm(String url, String... params) throws Exception {
        RequestTask requestTask = new RequestTask();
        requestTask.executeRequest(url, RequestTask.REQUEST_MODE_POST, params);
        //返回state_code
        String result = requestTask.get();
        return result;
    }

    @Override
    public String postForms(String url, String ProductsName, List<Foods> Products, String OrderAmountName, double OrderAmount, String CountName, int Count, String... args) throws Exception {
        return null;
    }
}
