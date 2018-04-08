package com.example.foodordering.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.foodordering.Activity_FoodsCategory;
import com.example.foodordering.R;
import com.example.foodordering.adapter.Adapter_Find;
import com.example.foodordering.bean.Find_Store;
import com.example.foodordering.weather.ui.WeatherActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by xch on 2017/2/23.
 */

public class Fragment_find extends Fragment implements ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {
    private ListView listview_find;
    private List<Find_Store> list_findStore = new ArrayList<>();
    private SliderLayout mDemoSlider;
    private GridView gridview;
    private Context mContext;
    MyAdapter adapter;

    //定义图标数组
    private int[] imageRes = {
            R.drawable.chinafood,
            R.drawable.icon_warning,
            R.drawable.pull_rider
//            R.drawable.rc,
//            R.drawable.icon_finished,
//            R.drawable.smile
    };

    //定义图标下方的名称数组
    private String[] name = {
            "营养配餐",
            "懒人天气",
            "诗和远方"
//            "休闲娱乐",
//            "景点门票",
//            "更多"
    };


    /**
     * 加载listView
     */
    private void initListViewFind() {
        Find_Store find_store = new Find_Store();
        find_store.setId(1);
        find_store.setName("星巴克");
        find_store.setImage(R.drawable.star_bk);
        find_store.setPrice(30);
        find_store.setRating(4);
        find_store.setEva_per_month(69);
        find_store.setDelivery_price(5);
        find_store.setDistances(3.86);
        find_store.setMonthSellCount("66");
        find_store.setMan_totals(20);
        find_store.setMan_salls(5);
        find_store.setDi(10);
        find_store.setZhuan(Boolean.TRUE);
        find_store.setSu(Boolean.TRUE);
        find_store.setPiao(Boolean.TRUE);
        list_findStore.add(find_store);

        Find_Store find_store2 = new Find_Store();
        find_store2.setId(2);
        find_store2.setName("必胜客");
        find_store2.setImage(R.drawable.pizza);
        find_store2.setPrice(45);
        find_store2.setRating(5);
        find_store2.setEva_per_month(88);
        find_store2.setDelivery_price(3);
        find_store2.setDistances(2.25);
        find_store2.setMonthSellCount("89");
        find_store2.setMan_totals(10);
        find_store2.setMan_salls(3);
        find_store2.setDi(5);
        find_store2.setZhuan(Boolean.FALSE);
        find_store2.setSu(Boolean.TRUE);
        find_store2.setPiao(Boolean.TRUE);
        list_findStore.add(find_store2);

        Find_Store find_store3 = new Find_Store();
        find_store3.setId(3);
        find_store3.setName("哈根达斯");
        find_store3.setImage(R.drawable.ha);
        find_store3.setPrice(40);
        find_store3.setRating(5);
        find_store3.setEva_per_month(68);
        find_store3.setDelivery_price(3);
        find_store3.setDistances(0.70);
        find_store3.setMonthSellCount("75");
        find_store3.setMan_totals(10);
        find_store3.setMan_salls(3);
        find_store3.setDi(5);
        find_store3.setZhuan(Boolean.FALSE);
        find_store3.setSu(Boolean.FALSE);
        find_store3.setPiao(Boolean.TRUE);
        list_findStore.add(find_store3);

        Find_Store find_store4 = new Find_Store();
        find_store4.setId(4);
        find_store4.setName("我Bridge");
        find_store4.setImage(R.drawable.icon_finished);
        find_store4.setPrice(48);
        find_store4.setRating(5);
        find_store4.setEva_per_month(98);
        find_store4.setDelivery_price(6);
        find_store4.setDistances(0.68);
        find_store4.setMonthSellCount("268");
        find_store4.setMan_totals(20);
        find_store4.setMan_salls(18);
        find_store4.setDi(8);
        find_store4.setZhuan(Boolean.TRUE);
        find_store4.setSu(Boolean.TRUE);
        find_store4.setPiao(Boolean.TRUE);
        list_findStore.add(find_store4);

        Adapter_Find adapter_find = new Adapter_Find(mContext, list_findStore);
        listview_find.setAdapter(adapter_find);
        listview_find.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), position + "", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * 加载gridView
     */
    private void initGridView() {
//        HashMap<String, String> url_maps = new HashMap<String, String>();
//        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");

        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("假期不打烊 舒适不打折", R.mipmap.hannibal);
        file_maps.put("生活快节奏 不负好时光", R.mipmap.bigbang);
        file_maps.put("食“色”生香 5折特色菜", R.mipmap.house);
        file_maps.put("图片素材由谢欣拍摄提供", R.mipmap.game_of_thrones);

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(mContext);
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        adapter = new MyAdapter();
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(mContext, Activity_FoodsCategory.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(mContext, WeatherActivity.class);
                        startActivity(intent2);
                        break;
                    case 2:
                        Toast.makeText(mContext, "敬请期待...", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, container, false);
        mContext = getActivity();
        listview_find = (ListView) view.findViewById(R.id.listview_find);
        mDemoSlider = (SliderLayout) view.findViewById(R.id.slider);
        gridview = (GridView) view.findViewById(R.id.gridview);

        initGridView();
        initListViewFind();

        return view;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(mContext, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }


    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // 条目的总数 文字组数 == 图片张数
            return name.length;
        }

        @Override
        public Object getItem(int position) {
            return name[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = View.inflate(mContext, R.layout.layout_find_gridview_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_item);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_item);
            tv_title.setText(name[position]);
            iv_icon.setBackgroundResource(imageRes[position]);
            return view;
        }
    }
}
