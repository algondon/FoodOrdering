package com.example.foodordering.fragment;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodordering.Activity_Balance;
import com.example.foodordering.Activity_FoodsDetails;
import com.example.foodordering.Activity_Login;
import com.example.foodordering.Activity_Main;
import com.example.foodordering.R;
import com.example.foodordering.adapter.Adapter_Address;
import com.example.foodordering.bean.Foods;
import com.example.foodordering.adapter.Adapter_Foods;
import com.example.foodordering.tools.dialog.ProgressDialog;
import com.example.foodordering.service.RequestUtility;
import com.example.foodordering.adapter.Adapter_Select;
import com.example.foodordering.widget.EmptyRecyclerView;
import com.flipboard.bottomsheet.BottomSheetLayout;

import org.litepal.crud.DataSupport;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xch on 2017/2/23.
 */

public class Fragment_home extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private View mEmptyView;//无数据视图
    private EmptyRecyclerView recyclerView;
    private List<Foods> foodsList;
    private Adapter_Foods adapter;
    private LinearLayout bottom;
    private ImageView imgCart;
    private ViewGroup anim_mask_layout;
    private RecyclerView rvSelected;
    private TextView tvCount, tvCost, tvSubmit, tvTips;//购买数量，总价格，结算，10元起送
    private BottomSheetLayout bottomSheetLayout;
    private View bottomSheet;
    private SparseArray<Foods> selectedList;//选择的商品集合
    private Adapter_Select adapterSelect;
    private NumberFormat nf;
    private Handler mHanlder;
    private double cost;
    private String userName, userId, email, sex, phoneNumber, nickname, address, avatar;
    private int statusCode = 0;//登录状态码
    private SwipeRefreshLayout swipeRefreshLayout;
    //用于刷新
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private ProgressDialog pg;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        LitePal.getDatabase();//利用这次查询操作创建数据库
        initView();
        pg = new ProgressDialog(getActivity());
        selectedList = new SparseArray<>();
        showList();
        tvSubmit.setOnClickListener(this);
        bottom.setOnClickListener(this);
    }


    private void initView() {
        mEmptyView = getActivity().findViewById(R.id.id_empty_view);
        recyclerView = (EmptyRecyclerView) getActivity().findViewById(R.id.foodsRecyclerView);
        bottom = (LinearLayout) getActivity().findViewById(R.id.bottom);
        tvCount = (TextView) getActivity().findViewById(R.id.tvCount);
        tvCost = (TextView) getActivity().findViewById(R.id.tvCost);
        tvTips = (TextView) getActivity().findViewById(R.id.tvTips);
        tvSubmit = (TextView) getActivity().findViewById(R.id.tvSubmit);
        imgCart = (ImageView) getActivity().findViewById(R.id.imgCart);
        anim_mask_layout = (RelativeLayout) getActivity().findViewById(R.id.containerLayout);
        bottomSheetLayout = (BottomSheetLayout) getActivity().findViewById(R.id.bottomSheetLayout);
        nf = NumberFormat.getCurrencyInstance();//数字格式化输出，返回语言环境的金融格式
        nf.setMaximumFractionDigits(2);//设置数值的小数部分允许的最大位数
        mHanlder = new Handler(getActivity().getMainLooper());
        //刷新控件
        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void showList() {
        pg.setMessage("数据加载中...");
        pg.show();
        if (Activity_Main.networkState == 0) {
            Toast.makeText(getActivity(), "网络连接失败，请检查网络连接设置！", Toast.LENGTH_SHORT).show();
            pg.dismiss();
            return;
        }
        new Thread() {
            public void run() {
                try {
                    foodsList = RequestUtility.getFoodsList();
                    handler.post(runnableFoodList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Runnable runnableFoodList = new Runnable() {
        @Override
        public void run() {
            pg.dismiss();

            if (foodsList != null && foodsList.size() > 0) {
                adapter = new Adapter_Foods(foodsList, Fragment_home.this);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.setEmptyView(mEmptyView);

                adapter.setOnItemClickListener(new Adapter_Address.OnItemClickListener() {//点击条目跳转到食品详情页
                    @Override
                    public void onItemClick(View view, int position) {
                        Foods foods = foodsList.get(position);
                        int foodId = foods.getId();
                        int count = foods.count;

                        Intent intent = new Intent(getActivity(), Activity_FoodsDetails.class);
                        intent.putExtra("foodId", foodId);
                        intent.putExtra("count", count);
                        startActivityForResult(intent, 1);
                    }
                });
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == 3) {
                    int count = data.getIntExtra("count", -1);
                    int foodId = data.getIntExtra("foodId", -1);
                    Foods foods = null;
                    for (int i = 0; i < foodsList.size(); i++) {
                        if (foodsList.get(i).getId() == foodId) {
                            foods = foodsList.get(i);
                            break;
                        }
                    }
                    if (count > 0) {//添加商品
                        for (int i = 0; i < count; i++) {
                            add(foods, true);
                        }
                    }
                    if (count < 0) {//移除商品
                        for (int i = 0; i < -(count); i++) {
                            remove(foods, true);
                        }
                    }
                }
                break;
        }
    }

    public void playAnimation(int[] start_location) {
        ImageView img = new ImageView(getActivity());
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(64, 64);//LayoutParams类可以改变view大小
        img.setLayoutParams(mParams);
        img.setImageResource(R.drawable.button_add);
        int count = 0;
        for (int i = 0; i < selectedList.size(); i++) {
            Foods item = selectedList.valueAt(i);
            count += item.count;
        }
        if (count > 1) {//小于1时购物车隐藏，抛物线动画会异常
            setAnim(img, start_location);
        }
    }

    /**
     * 创建动画 平移动画直接传递偏移量
     *
     * @param startX
     * @param startY
     * @return
     */
    private Animation createAnim(int startX, int startY) {
        int[] des = new int[2];
        imgCart.getLocationInWindow(des);
        AnimationSet set = new AnimationSet(false);
        Animation translationX = new TranslateAnimation(0, des[0] - startX, 0, 0);
        translationX.setInterpolator(new LinearInterpolator());
        Animation translationY = new TranslateAnimation(0, 0, 0, des[1] - startY);
        translationY.setInterpolator(new AccelerateInterpolator());
        Animation alpha = new AlphaAnimation(1, 0.5f);
        set.addAnimation(translationX);
        set.addAnimation(translationY);
        set.addAnimation(alpha);
        set.setDuration(500);
        return set;
    }

    /**
     * 计算动画view在根部局中的坐标 添加到根部局中
     *
     * @param vg
     * @param view
     * @param location
     */
    private void addViewToAnimLayout(final ViewGroup vg, final View view, int[] location) {
        int x = location[0];
        int y = location[1];
        int[] loc = new int[2];
        vg.getLocationInWindow(loc);
        view.setX(x);
        view.setY(y - loc[1]);
        vg.addView(view);
    }

    /**
     * 设置动画结束移除动画view
     *
     * @param v
     * @param start_location
     */
    private void setAnim(final View v, int[] start_location) {
        addViewToAnimLayout(anim_mask_layout, v, start_location);
        Animation set = createAnim(start_location[0], start_location[1]);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(final Animation animation) {
                //直接remove可能会因为界面仍在绘制中成而报错
                mHanlder.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        anim_mask_layout.removeView(v);
                    }
                }, 100);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        v.startAnimation(set);
    }


    /**
     * 添加商品
     *
     * @param item
     * @param refreshFoodList
     */
    public void add(Foods item, boolean refreshFoodList) {
        Foods temp = selectedList.get(item.getId());
        if (temp == null) {
            item.count = 1;
            selectedList.append(item.getId(), item);
            //saveSelectedFoodsList(item);//将选择的商品添加到本地数据库
        } else {
            temp.count++;
            //saveSelectedFoodsList(temp);
            //updateSelectedFoodsList(temp);//更新本地数据库里的这条记录

        }
        update(refreshFoodList);
    }

    /**
     * 移除商品
     *
     * @param item
     * @param refreshFoodList
     */
    public void remove(Foods item, boolean refreshFoodList) {
        Foods temp = selectedList.get(item.getId());
        if (temp != null) {
            if (temp.count < 2) {
                selectedList.remove(item.getId());
//                deleteSelectedFoodsList(temp);//将这个商品从本地数据库中删除
            } else {
                item.count--;
//                updateSelectedFoodsList(item);//更新本地数据库里的这条记录
            }
        }
        update(refreshFoodList);
    }

    /**
     * 将选择的商品保存到本地数据库中，使用LitePal
     */
    public void saveSelectedFoodsList(Foods item) {
        item.save();
    }

    /**
     * 将选择的商品从本地数据库中删除，使用LitePal
     */
    private void deleteSelectedFoodsList(Foods item) {
        item.delete();
    }

    /**
     * 根据foodId更新当前本地数据库中的这条记录，使用LitePal
     * (把Foods表中id为item.getId()的记录的count改成item.count)
     */
    private void updateSelectedFoodsList(Foods item) {
        ContentValues values = new ContentValues();
        values.put("count", item.count);
        DataSupport.update(Foods.class, values, item.getId());
    }

    /**
     * 根据商品id获取当前商品的采购数量
     *
     * @param id
     * @return
     */
    public int getSelectedItemCountById(int id) {
        Foods temp = selectedList.get(id);
        if (temp == null) {
            return 0;
        }
        return temp.count;
    }

    /**
     * 刷新布局 总价、购买数量等
     *
     * @param refreshFoodList
     */
    public void update(boolean refreshFoodList) {
        int size = selectedList.size();
        int count = 0;
        cost = 0;
        for (int i = 0; i < size; i++) {
            Foods item = selectedList.valueAt(i);
            count += item.count;
            cost += item.count * item.getPrice();
        }

        if (count < 1) {
            tvCount.setVisibility(View.GONE);
        } else {
            tvCount.setVisibility(View.VISIBLE);
        }
        tvCount.setText(String.valueOf(count));

        if (cost > 9.9) {//9.9元以上显示结算按钮
            tvTips.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.VISIBLE);
        } else {
            tvSubmit.setVisibility(View.GONE);
            tvTips.setVisibility(View.VISIBLE);
        }
        tvCost.setText(nf.format(cost));

        if (adapter != null && refreshFoodList) {
            adapter.notifyDataSetChanged();
        }
        if (adapterSelect != null) {
            adapterSelect.notifyDataSetChanged();
        }
        if (bottomSheetLayout.isSheetShowing() && selectedList.size() < 1) {
            bottomSheetLayout.dismissSheet();
        }
        //按数量显示隐藏底部结算栏
        if (count <= 0) {
            bottom.setVisibility(View.GONE);
        } else {
            bottom.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 清空购物车
     */
    public void clearCart() {
        //对话框提示确定清空购物车
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("提示？");
        dialog.setMessage("您确定要清空购物车中所有商品？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedList.clear();
                update(true);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    /**
     * 创建购物车view
     *
     * @return
     */
    private View createBottomSheetView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.shopping_cart, (ViewGroup) getActivity().getWindow().getDecorView(), false);
        rvSelected = (RecyclerView) view.findViewById(R.id.selectRecyclerView);
        rvSelected.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextView clear = (TextView) view.findViewById(R.id.clear);
        clear.setOnClickListener(this);
        adapterSelect = new Adapter_Select(this, selectedList);
        rvSelected.setAdapter(adapterSelect);
        return view;
    }

    /**
     * 弹出购物车view
     */
    private void showBottomSheet() {
        if (bottomSheet == null) {
            bottomSheet = createBottomSheetView();
        }
        if (bottomSheetLayout.isSheetShowing()) {
            bottomSheetLayout.dismissSheet();
        } else {
            if (selectedList.size() != 0) {
                bottomSheetLayout.showWithSheetView(bottomSheet);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bottom://弹出购物车
                showBottomSheet();
                break;
            case R.id.clear://清空购物车
                clearCart();
                break;
            case R.id.tvSubmit://结算
                getUserData();
                if (statusCode == 200) {
                    ArrayList<Foods> arrayList = new ArrayList();
                    for (int i = 0; i < selectedList.size(); i++) {
                        arrayList.add(selectedList.valueAt(i));
                        Log.i("Main", selectedList.valueAt(i).getName());
                    }
                    Intent intent = new Intent(getContext(), Activity_Balance.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("selectList", arrayList);
                    bundle.putString("userId", userId);
                    bundle.putString("userName", userName);
                    bundle.putString("email", email);
                    bundle.putString("sex", sex);
                    bundle.putString("phoneNumber", phoneNumber);
                    bundle.putString("nickname", nickname);
                    bundle.putString("address", address);
                    bundle.putString("avatar", avatar);
                    bundle.putInt("statusCode", statusCode);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "请先登录!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getContext(), Activity_Login.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    private void getUserData() {
        Activity_Main activityMain = (Activity_Main) getActivity();
        userId = activityMain.userId;
        userName = activityMain.name;
        email = activityMain.email;
        sex = activityMain.sex;
        phoneNumber = activityMain.phoneNumber;
        nickname = activityMain.nickname;
        address = activityMain.address;
        avatar = activityMain.avatar;
        statusCode = activityMain.statusCode;
    }

    //刷新事件方法
    @Override
    public void onRefresh() {
        showList();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //刷新控件停止两秒后消失
                    Thread.sleep(1000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
