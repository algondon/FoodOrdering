package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Foods;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.util.Util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Activity_FoodsDetails extends BaseActivity implements View.OnClickListener {
    private NumberFormat nf;
    private TextView tv_price, tv_count, tvMinus, tvAdd;
    private TextView foods_ingredients_text, foods_description_text;
    private FloatingActionButton fab_comment;
    private ImageView foods_imageView;
    private RatingBar ratingBar;
    private String name, ingredients, description, imageUrl;//名字，配料，简介，图片地址
    private double price;
    private int rating;
    private int foodId;//食物id
    private List<Foods> foodsListById = new ArrayList<>();
    private int count;//购买数量
    private int mCount;//记录刚跳转进此页面此商品的数量
    private Handler handler = new Handler();
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods_details);
        pg = new ProgressDialog(Activity_FoodsDetails.this);
        getIntentData();
        getFoodsListById(foodId);
    }

    private void initView() {
        nf = NumberFormat.getCurrencyInstance();
        nf.setMaximumFractionDigits(2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        foods_imageView = (ImageView) findViewById(R.id.foods_image_view);
        foods_ingredients_text = (TextView) findViewById(R.id.foods_ingredients_text);
        foods_description_text = (TextView) findViewById(R.id.foods_description_text);
        fab_comment = (FloatingActionButton) findViewById(R.id.fab_comment);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_count = (TextView) findViewById(R.id.tvCount);
        tvMinus = (TextView) findViewById(R.id.tvMinus);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(name);

        Glide.with(this).load(imageUrl).into(foods_imageView);
        tv_price.setText(nf.format(price));
        tv_count.setText(count + "");
        foods_ingredients_text.setText("配料：" + ingredients);
        foods_description_text.setText("简介：" + description);
        ratingBar.setProgress(rating);

        tvMinus.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        fab_comment.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //向Fragment_Home页面返回结果
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("count", count - mCount);//这个count数为正是添加商品 为负是移除商品
                bundle.putInt("foodId", foodId);
                intent.putExtras(bundle);
                setResult(3, intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static void actionStart(Context context, int foodId) {
        Intent intent = new Intent(context, Activity_FoodsDetails.class);
        intent.putExtra("foodId", foodId);
        context.startActivity(intent);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        foodId = intent.getIntExtra("foodId", -1);
        count = intent.getIntExtra("count", -1);
        mCount = count;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_comment:
                break;
            case R.id.tvMinus:
                if (count > 0) {
                    count--;
                    tv_count.setText(count + "");
                }
                break;
            case R.id.tvAdd:
                count++;
                tv_count.setText(count + "");
                break;
            default:
                break;
        }
    }

    /**
     * 利用foodId从服务器获取food信息
     *
     * @param foodId
     */
    private void getFoodsListById(final int foodId) {
        if (foodId != -1) {
            pg.setMessage("加载中...");
            pg.show();

            if (!Util.checkNetwork(this)) {
                pg.dismiss();
                return;
            }
            new Thread() {
                @Override
                public void run() {
                    try {
                        foodsListById = RequestUtility.getFoodsListById(foodId + "");
                        handler.post(runnableFood);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    Runnable runnableFood = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            Foods foods = foodsListById.get(0);
            imageUrl = foods.getImageUrl();
            name = foods.getName();
            price = foods.getPrice();
            rating = foods.getRating();
            description = foods.getDescription();
            ingredients = foods.getIngredients();

            initView();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果是返回键
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //重写返回键
            //向Fragment_Home页面返回结果
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putInt("count", count - mCount);//这个count数为正是添加商品 为负是移除商品
            bundle.putInt("foodId", foodId);
            intent.putExtras(bundle);
            setResult(3, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
