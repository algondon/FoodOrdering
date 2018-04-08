package com.example.foodordering;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.foodordering.widget.FixWrapLayout;

import java.util.ArrayList;
import java.util.List;

public class Activity_Remark extends AppCompatActivity implements View.OnClickListener {
    private FixWrapLayout fixWrapLayout;//常用评论
    private List<String> remarks;
    private TextView toolbarText,txt_count;
    private EditText edit_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("添加备注");
        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText("完成");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        fixWrapLayout = (FixWrapLayout) findViewById(R.id.ly_common_remarks);
        setRemarksData();
        setCommonRemarks(remarks);

        edit_content= (EditText) findViewById(R.id.edit_content);
        txt_count= (TextView) findViewById(R.id.txt_count);
        toolbarText.setOnClickListener(this);
    }

    /**
     * 获取备注内容
     * @return
     */
    private String getRemarkContent(){
        String content;
        content=edit_content.getText().toString();
        return content;
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

    /**
     * 设置常用备注数据
     */
    private void setRemarksData() {
        remarks = new ArrayList<>();
        remarks.add("少放辣");
        remarks.add("多放辣");
        remarks.add("不要放辣");
        remarks.add("多点饭");
        remarks.add("不吃葱");
        remarks.add("不吃蒜");
        remarks.add("不吃香菜");
        remarks.add("不吃洋葱");
        remarks.add("不吃小米辣");
    }

    /**
     * 设置常用备注标签
     *
     * @param remarks
     */
    public void setCommonRemarks(List<String> remarks) {
        for (String remark : remarks) {
            TextView remarkTxt = (TextView) View.inflate(this, R.layout.include_common_remark, null);
            remarkTxt.setText(remark);
            remarkTxt.setTag(remark);
            remarkTxt.setOnClickListener(this);
            fixWrapLayout.addView(remarkTxt);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_text://完成
                toolbarText.setTextColor(getResources().getColor(R.color.white));
                getRemarkContent();
                //向结算页面返回备注信息
                Intent intent = new Intent();
                intent.putExtra("remarkContent", getRemarkContent());
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
