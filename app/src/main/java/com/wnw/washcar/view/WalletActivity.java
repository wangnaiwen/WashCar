package com.wnw.washcar.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wnw.washcar.R;
/**
 * Created by wnw on 2017/4/7.
 */

public class WalletActivity extends Activity implements View.OnClickListener{

    private ImageView back;
    private TextView moneyTv;

    private String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Intent intent = getIntent();
        money = intent.getStringExtra("money");
        initView();
    }

    private void initView(){
        back = (ImageView)findViewById(R.id.back_wallet);
        moneyTv = (TextView)findViewById(R.id.dis_money);
        moneyTv.setText("￥"+money);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_wallet:
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}
