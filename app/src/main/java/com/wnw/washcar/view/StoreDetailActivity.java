package com.wnw.washcar.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wnw.washcar.R;
import com.wnw.washcar.bean.Store;

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

    private Store store;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        Intent intent = getIntent();
        store = (Store) intent.getSerializableExtra("store");
        initView();
    }

    private void initView(){
        back = (ImageView)findViewById(R.id.back_detail);
        storeImg = (ImageView)findViewById(R.id.img_store);
        nameTv = (TextView) findViewById(R.id.tv_store_name);
        addressTv = (TextView)findViewById(R.id.tv_store_address);
        phoneImg = (ImageView)findViewById(R.id.img_phone);
        locationImg = (ImageView)findViewById(R.id.img_location);

        back.setOnClickListener(this);
        phoneImg.setOnClickListener(this);
        locationImg.setOnClickListener(this);

        Glide.with(this).load(store.getPic().getFileUrl()).into(storeImg);

        nameTv.setText(store.getName());
        addressTv.setText(store.getAddress());
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
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}
