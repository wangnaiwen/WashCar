package com.wnw.washcar.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.wnw.washcar.R;
import com.wnw.washcar.bean.Order;
import com.wnw.washcar.bean.Store;
import com.wnw.washcar.bean.User;
import com.wnw.washcar.login.ActivityCollector;
import com.wnw.washcar.login.LoginSetPasswdAty;
import com.wnw.washcar.login.ResetPasswordActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wnw on 2017/4/6.
 */

public class StoreDetailActivity extends Activity implements View.OnClickListener{

    private ImageView back;
    private ImageView storeImg;
    private TextView nameTv;
    private TextView addressTv;
    private ImageView phoneImg;
    private ImageView locationImg;
    private TextView moneyTv;
    private TextView washOrderTv;

    private Store store;
    private User user;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        Intent intent = getIntent();
        store = (Store) intent.getSerializableExtra("store");
        userId = intent.getStringExtra("userId");
        initView();
    }

    private void initView(){
        back = (ImageView)findViewById(R.id.back_detail);
        storeImg = (ImageView)findViewById(R.id.img_store);
        nameTv = (TextView) findViewById(R.id.tv_store_name);
        addressTv = (TextView)findViewById(R.id.tv_store_address);
        phoneImg = (ImageView)findViewById(R.id.img_phone);
        locationImg = (ImageView)findViewById(R.id.img_location);
        moneyTv = (TextView) findViewById(R.id.tv_money);
        washOrderTv = (TextView)findViewById(R.id.wash_order);

        back.setOnClickListener(this);
        phoneImg.setOnClickListener(this);
        locationImg.setOnClickListener(this);
        washOrderTv.setOnClickListener(this);

        Glide.with(this).load(store.getPic().getFileUrl()).into(storeImg);

        nameTv.setText(store.getName());
        addressTv.setText(store.getAddress());
        moneyTv.setText("￥"+store.getWashPrice());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_detail:
                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                break;
            case R.id.img_phone:
                //进入拨号页面
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+store.getPhone()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.img_location:
                Intent intent1 = new Intent(this, MapActivity.class);
                intent1.putExtra("longitude", store.getLongitude());
                intent1.putExtra("latitude", store.getLatitude());
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                break;
            case R.id.wash_order:
                findUser();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    //查出User，更新余额
    private void findUser(){
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(userId, new QueryListener<User>() {
            @Override
            public void done(User object, BmobException e) {
                if(e==null){
                    user  = object;
                    createOrder();
                    updateLocalUser();
                }else{
                    Toast.makeText(StoreDetailActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //创建订单
    private void createOrder(){
        if(user.getMoney() < store.getWashPrice()){
            Toast.makeText(StoreDetailActivity.this, "余额不足", Toast.LENGTH_SHORT).show();
        }else {
            showDialog();
        }
    }

    //更新保存在本地的user对象
    private void updateLocalUser(){
        SharedPreferences.Editor editor = getSharedPreferences("account",
                MODE_PRIVATE).edit();
        editor.clear();
        editor.putString("id", user.getObjectId());
        editor.putString("phone",user.getPhone());
        editor.putString("nickname",user.getNickname());
        editor.putString("password", user.getPassword());
        BmobFile file = user.getImg();
        editor.putString("url", file.getFileUrl());
        editor.putString("imgName", file.getFilename());
        editor.putString("menoy",user.getMoney()+"");
        editor.apply();
    }

    //弹出对话框
    AlertDialog alertDialog = null;
    private void showDialog(){
         alertDialog = new AlertDialog.Builder(this)
                .setTitle("购买洗车服务")
                .setMessage("支付￥"+store.getWashPrice())
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateUserMoney();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        alertDialog.show();

    }

    //update user money,
    ///第一次更新
    private boolean isFirst = true;
    private void updateUserMoney(){
        Toast.makeText(this,"正在下单",Toast.LENGTH_SHORT).show();
        user.setMoney(user.getMoney() - store.getWashPrice());
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    if(isFirst){
                        insertOrder();
                        isFirst = false;
                    }
                }else{
                    if(isFirst){
                        Log.i("bmob","下单失败："+e.getMessage()+","+e.getErrorCode());
                        //alertDialog.dismiss();
                    }
                }
            }
        });
    }

    //insert order
    private void insertOrder(){
        Order order = new Order();
        Date d = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmssSS");

        order.setNumbering(sdf.format(d));
        order.setUserId(user.getObjectId());
        order.setStoreId(store.getObjectId());
        order.setStoreName(store.getName());
        order.setType("洗车服务");
        order.setMoney(store.getWashPrice());

        order.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Toast.makeText(StoreDetailActivity.this, "下单成功！", Toast.LENGTH_SHORT).show();
                    //alertDialog.dismiss();
                    updateLocalUser();
                    Intent intent = new Intent();
                    intent.putExtra("user",user);
                    setResult(RESULT_OK, intent);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else{
                    Toast.makeText(StoreDetailActivity.this, "下单失败！", Toast.LENGTH_SHORT).show();
                    //把钱打回来
                    user.setMoney(user.getMoney()+store.getWashPrice());
                    updateUserMoney();
                }
            }
        });
    }
}
