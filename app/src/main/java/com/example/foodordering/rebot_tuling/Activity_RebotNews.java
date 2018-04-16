package com.example.foodordering.rebot_tuling;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.foodordering.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_RebotNews extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WQRecycleAdapter wqRecycleAdapter;
    private Toolbar toolbar;
    private List<News> newss = new ArrayList<>();
    private List<Caipu> caipus = new ArrayList<>();
    String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rebot_news);
        String result = getIntent().getStringExtra("result");
        String title = getIntent().getStringExtra("title");
        code = getIntent().getStringExtra("code");//302000 新闻  308000//菜谱
        toolbar = (Toolbar) findViewById(R.id.news_toolbar);
        toolbar.setTitle(title);
        recyclerView = (RecyclerView) findViewById(R.id.news_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new SpaceItemDecoration(this, SpaceItemDecoration.VERTICAL_LIST, 2));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            JSONObject o = new JSONObject(result);
            JSONArray arr = o.getJSONArray("list");

            if (code.equals("302000")) {
                for (int i = 0; i < arr.length(); i++) {
                    News n = new News();
                    JSONObject obj = arr.getJSONObject(i);
                    n.setArticle(obj.getString("article"));
                    n.setIcon(obj.getString("icon"));
                    n.setSource(obj.getString("source"));
                    n.setUrl(obj.getString("detailurl"));
                    newss.add(n);
                }
                wqRecycleAdapter = new WQRecycleAdapter(this, R.layout.item_rebot_news, newss);

            } else {
                for (int i = 0; i < arr.length(); i++) {
                    Caipu c = new Caipu();
                    JSONObject obj = arr.getJSONObject(i);
                    c.setIcon(obj.getString("icon"));
                    c.setDetailurl(obj.getString("detailurl"));
                    c.setInfo(obj.getString("info"));
                    c.setName(obj.getString("name"));
                    caipus.add(c);
                }
                wqRecycleAdapter = new WQRecycleAdapter(this, R.layout.item_rebot_news, caipus);
            }


            recyclerView.setAdapter(wqRecycleAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        wqRecycleAdapter.setCallBack(new WQRecycleAdapter.CallBack() {
            @Override
            public <T> void convert(WQViewHolder holder, T bean, int position) {

                if (code.equals("302000")) {
                    News n = (News) bean;
                    Log.i("news", n.getArticle());
                    holder.setText(R.id.item_news_article, n.getArticle());
                    holder.setText(R.id.item_news_asource, n.getSource());
                    if (n.getIcon().equals("")) {
                        holder.getView(R.id.item_news_img).setVisibility(View.GONE);
                    } else {
                        holder.setImageResource(Activity_RebotNews.this, R.id.item_news_img, n.getIcon());
                    }
                } else {
                    Caipu c = (Caipu) bean;
                    holder.setText(R.id.item_news_article, c.getInfo());
                    holder.setText(R.id.item_news_asource, c.getName());
                    if (c.getIcon().equals("")) {
                        holder.getView(R.id.item_news_img).setVisibility(View.GONE);
                    } else {
                        holder.setImageResource(Activity_RebotNews.this, R.id.item_news_img, c.getIcon());
                    }
                }


            }
        });

        wqRecycleAdapter.setOnItemClickListner(new WQRecycleAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(View v, int position) {
                String url;
                if (code.equals("302000")) {
                    url = newss.get(position).getUrl();
                } else {
                    url = caipus.get(position).getDetailurl();
                }


                Intent intent = new Intent(Activity_RebotNews.this, Activity_RebotWeb.class);
                intent.putExtra("url", url);
                startActivity(intent);
            }
        });

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
