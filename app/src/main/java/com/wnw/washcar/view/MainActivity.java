package com.wnw.washcar.view;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.megvii.cloud.http.CommonOperate;
import com.megvii.cloud.http.Response;
import com.wnw.washcar.R;
import com.wnw.washcar.bean.User;
import com.wnw.washcar.login.ActivityCollector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.os.Environment.getExternalStorageDirectory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener{

    private NavigationView navigationView;  //侧边滑动的view

    private CircleImageView userImg;  //头像
    private TextView nickNameView;    //昵称
    private TextView phoneView;       //电话号码
    private ImageView editNickName;   //编辑昵称

    private User user;            // 登录的用户
    private String url = null;    //用户的头像url地址
    private String imgName = null;//用户的头像name名字


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);

        //获取登录的User
        getUser();
        //初始化View
        initViews();
    }

    //初始化一些View，菜单等
    private void initViews()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        userImg = (CircleImageView)navigationView.getHeaderView(0).findViewById(R.id.icon_user);
        nickNameView = (TextView)navigationView.getHeaderView(0).findViewById(R.id.username);
        phoneView = (TextView)navigationView.getHeaderView(0).findViewById(R.id.phone);
        editNickName = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.edit_nickname);
        editNickName.setOnClickListener(this);

        //设置NavigationView里面的参数：昵称，电话，头像
        phoneView.setText(user.getPhone());
        nickNameView.setText(user.getNickname());
        Glide.with(this).load(url).into(userImg);

        userImg.setOnClickListener(this);
    }

    //获取本地保存的登录用户信息
    private void getUser(){
        user = new User();
        SharedPreferences preferences = getSharedPreferences("account", MODE_PRIVATE);
        user.setObjectId(preferences.getString("id", ""));
        user.setPhone(preferences.getString("phone", ""));
        user.setNickname(preferences.getString("nickname", ""));
        user.setPassword(preferences.getString("password", ""));
        url = preferences.getString("url", "");
        imgName = preferences.getString("imgName", "");
    }

    // 初始化，加载菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //菜单选中监听
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_setting) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.icon_user:
                //用户头像点击
                Intent intent = new Intent(this, ImgUploadActivity.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.edit_nickname:
                //用户昵称点击
                Intent intent1 = new Intent(this, EditNickNameActivity.class);
                intent1.putExtra("id",user.getObjectId());
                intent1.putExtra("nickname", user.getNickname());
                startActivityForResult(intent1, 4);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
                break;
        }
    }

    //设置响应intent请求
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
       if(requestCode == 2){//获取头像的url路径，返回图片
            if(resultCode == RESULT_OK){
                url = intent.getStringExtra("url");
                imgName = intent.getStringExtra("imgName");
                Glide.with(this).load(url).error(R.mipmap.error).into(userImg);
            }
        }else if (requestCode == 4){   //编辑昵称返回
            if(resultCode == RESULT_OK){
                user.setNickname(intent.getStringExtra("nickname"));
                nickNameView.setText(user.getNickname());
            }
        }
    }
    private long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            /**
             * 当侧边栏处于展开状态时，按下返回键，关闭侧边栏
             * */

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
