package com.example.foodordering;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.foodordering.control.BaseActivity;
import com.example.foodordering.bean.Foods;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.RequestUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描二维码之后跳转的界面
 */
public class Activity_QRcodeResult extends BaseActivity {
    private TextView foods_ingredients_text, foods_description_text;
    private FloatingActionButton fab_comment;
    private ImageView foods_imageView;
    private RatingBar ratingBar;
    private String name, ingredients, description, imageUrl;//名字，配料，简介，图片地址
    private double price;
    private int rating;
    private String foodId;//食物id
    private List<Foods> foodsListById = new ArrayList<>();
    private Handler handler = new Handler();
    private ProgressDialog pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_result);
        pg = new ProgressDialog(Activity_QRcodeResult.this);
        getIntentData();
        getFoodData(foodId);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        foods_imageView = (ImageView) findViewById(R.id.foods_image_view);
        foods_ingredients_text = (TextView) findViewById(R.id.foods_ingredients_text);
        foods_description_text = (TextView) findViewById(R.id.foods_description_text);
        fab_comment = (FloatingActionButton) findViewById(R.id.fab_comment);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(name);

        Glide.with(this).load(imageUrl).into(foods_imageView);
        foods_ingredients_text.setText("配料：" + ingredients);
        foods_description_text.setText("简介：" + description);
        ratingBar.setProgress(rating);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * @param context
     * @param foodId  菜品id
     */
    public static void actionStart(Context context, String foodId) {
        Intent intent = new Intent(context, Activity_QRcodeResult.class);
        intent.putExtra("foodId", foodId);
        context.startActivity(intent);
    }

    /**
     * 获取传过来的食品id
     */
    private void getIntentData() {
        Intent intent = getIntent();
        foodId = intent.getStringExtra("foodId");
    }

    /**
     * 通过食物id获取食品信息
     *
     * @param foodId
     */
    private void getFoodData(final String foodId) {
        pg.setMessage("加载中...");
        pg.show();
        if (foodId != null) {
            foodsListById.clear();
            new Thread() {
                @Override
                public void run() {
                    try {
                        foodsListById = RequestUtility.getFoodsListById(foodId);
                        handler.post(runnableFoods);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            pg.dismiss();
        }
    }

    Runnable runnableFoods = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();
            if (foodsListById.size() > 0) {
                Foods foods = foodsListById.get(0);
                name = foods.getName();
                ingredients = foods.getIngredients();//配料
                description = foods.getDescription();
                imageUrl = foods.getImageUrl();
                price = foods.getPrice();
                rating = foods.getRating();
            } else {
                Toast.makeText(Activity_QRcodeResult.this, foodsListById.size() + ":foodsListSize   没有此二维码对应的信息", Toast.LENGTH_SHORT).show();
            }
            initView();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
