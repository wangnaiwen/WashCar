package com.wnw.washcar.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wnw.washcar.R;
import com.wnw.washcar.adapter.OrderAdapter;
import com.wnw.washcar.bean.Order;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by wnw on 2017/4/7.
 */

public class MyOrderActivity extends Activity implements View.OnClickListener {
    private ImageView back;
    private ListView orderLv;
    private TextView noOrderTv;

    private String userId;

    private List<Order> orderList;
    private OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        initView();
        loadData();
    }

    private void initView(){
        back = (ImageView)findViewById(R.id.back_my_order);
        orderLv = (ListView)findViewById(R.id.lv_order);
        noOrderTv = (TextView)findViewById(R.id.no_order);

        back.setOnClickListener(this);
    }

    //加载数据
    private void loadData(){
        BmobQuery<Order> query = new BmobQuery<Order>();
        query.addWhereEqualTo("userId",userId);
        query.findObjects(new FindListener<Order>() {
            @Override
            public void done(List<Order> object, BmobException e) {
                if(e==null){
                    if(object != null){
                        orderList = object;
                        setOrderAdapter();
                    }else{
                        Toast.makeText(MyOrderActivity.this, "网络错误1",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MyOrderActivity.this, "网络错误",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setOrderAdapter(){
        if(orderList.size() == 0){
            noOrderTv.setVisibility(View.VISIBLE);
        }else {
            noOrderTv.setVisibility(View.GONE);
            orderAdapter = new OrderAdapter(this, orderList);
            orderLv.setAdapter(orderAdapter);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_my_order:
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
