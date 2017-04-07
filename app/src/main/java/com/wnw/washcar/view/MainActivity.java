package com.wnw.washcar.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.model.LatLng;
import com.bumptech.glide.Glide;
import com.wnw.washcar.R;
import com.wnw.washcar.adapter.StoreAdapter;
import com.wnw.washcar.bean.Store;
import com.wnw.washcar.bean.StoreBean;
import com.wnw.washcar.bean.User;
import com.wnw.washcar.login.ActivityCollector;
import com.wnw.washcar.util.ConversionUtil;
import com.wnw.washcar.util.Distribution;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener, AdapterView.OnItemClickListener,
        LocationSource, AMapLocationListener {

    private NavigationView navigationView;  //侧边滑动的view

    private CircleImageView userImg;  //头像
    private TextView nickNameView;    //昵称
    private TextView phoneView;       //电话号码
    private ImageView editNickName;   //编辑昵称

    private User user;            // 登录的用户
    private String url = null;    //用户的头像url地址
    private String imgName = null;//用户的头像name名字

    private StoreAdapter storeAdapter;     //商店Adapter
    private ListView storeLv;              //商店ListView
    private List<Store> storeList;         //storeList
    private List<StoreBean> storeBeanList; //storeBeanList


    //声明AMapLocationClient类对象，定位发起端
    private AMapLocationClient mLocationClient = null;
    //声明mLocationOption对象，定位参数
    public AMapLocationClientOption mLocationOption = null;
    //声明mListener对象，定位监听器
    private OnLocationChangedListener mListener = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    //用户的经纬度
    private double userLongitude;
    private double userLatitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bmob.initialize(this, "600d44ca6c6d2eaa137b3d85410ad8ac");
        ActivityCollector.addActivity(this);
        location();
        //获取登录的User
        getUser();
        //初始化View
        initViews();
    }

    //初始化一些View，菜单等
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        userImg = (CircleImageView) navigationView.getHeaderView(0).findViewById(R.id.icon_user);
        nickNameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.username);
        phoneView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.phone);
        editNickName = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.edit_nickname);
        editNickName.setOnClickListener(this);

        storeLv = (ListView) findViewById(R.id.lv_store);
        storeLv.setOnItemClickListener(this);

        //设置NavigationView里面的参数：昵称，电话，头像
        phoneView.setText(user.getPhone());
        nickNameView.setText(user.getNickname());
        Glide.with(this).load(url).into(userImg);

        userImg.setOnClickListener(this);
    }

    //获取本地保存的登录用户信息
    private void getUser() {
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
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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

    //从数据库查询商店的数据
    private void loadData(){
        BmobQuery<Store> query = new BmobQuery<Store>();
        //执行查询方法
        query.findObjects(new FindListener<Store>() {
            @Override
            public void done(List<Store> object, BmobException e) {
                if(e==null){
                    //得到查询回来的数据，进行提取，排序
                    if(object != null){
                        Log.d("wangnaiwen", object.size()+"");
                        storeList = object;
                        prepareData();
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    e.printStackTrace();
                }
            }
        });
    }

    //对数据进行转化成StoreBean类型，并且安排距离进行排序
    private void prepareData(){
        int length = storeList.size();

        storeBeanList = new ArrayList<>();
        //转化成StoreBean
        for(int i = 0; i < length; i++){
            StoreBean storeBean = new StoreBean();
            Store store = storeList.get(i);
            storeBean.setName(store.getName());
            storeBean.setAddress(store.getAddress());
            storeBean.setPic(store.getPic());
            Log.d("wnw", userLongitude+" " + userLatitude + " " + store.getLongitude() +" " +store.getLatitude());
            Distribution d1 = new Distribution(userLongitude, userLatitude);
            Distribution d2 = new Distribution(Double.parseDouble(store.getLongitude()), Double.parseDouble(store.getLatitude()));
            storeBean.setDistance(ConversionUtil.getDistance(d1,d2));
            storeBeanList.add(storeBean);
        }
        //选择排序
        int minIndex=0;
        StoreBean temp ;
        Store temp1;
        for(int i=0;i<length-1;i++)
        {
            minIndex=i;//无序区的最小数据数组下标
            for(int j=i+1; j<length;j++)
            {
                //在无序区中找到最小数据并保存其数组下标
                if(storeBeanList.get(j).getDistance()<storeBeanList.get(minIndex).getDistance())
                {
                    minIndex=j;
                }
            }
            if(minIndex!=i)
            {
                //如果不是无序区的最小值位置不是默认的第一个数据，则交换之。
                temp = storeBeanList.get(i);
                temp1 = storeList.get(i);

                storeBeanList.set(i,storeBeanList.get(minIndex));
                storeList.set(i, storeList.get(minIndex));

                storeBeanList.set(minIndex,temp);
                storeList.set(minIndex, temp1);
            }
        }

        //排序完，就设置Adapter
        storeAdapter = new StoreAdapter(this, storeBeanList);
        storeLv.setAdapter(storeAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon_user:
                //用户头像点击
                Intent intent = new Intent(this, ImgUploadActivity.class);
                startActivityForResult(intent, 2);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.edit_nickname:
                //用户昵称点击
                Intent intent1 = new Intent(this, EditNickNameActivity.class);
                intent1.putExtra("id", user.getObjectId());
                intent1.putExtra("nickname", user.getNickname());
                startActivityForResult(intent1, 4);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.lv_store:
                Intent intent = new Intent(MainActivity.this, StoreDetailActivity.class);
                intent.putExtra("store", storeList.get(i));
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    //设置响应intent请求
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 2) {//获取头像的url路径，返回图片
            if (resultCode == RESULT_OK) {
                url = intent.getStringExtra("url");
                imgName = intent.getStringExtra("imgName");
                Glide.with(this).load(url).error(R.mipmap.error).into(userImg);
            }
        } else if (requestCode == 4) {   //编辑昵称返回
            if (resultCode == RESULT_OK) {
                user.setNickname(intent.getStringExtra("nickname"));
                nickNameView.setText(user.getNickname());
            }
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            /**
             * 当侧边栏处于展开状态时，按下返回键，关闭侧边栏
             * */

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }

            if ((System.currentTimeMillis() - exitTime) > 2000) {
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

    //定位
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    //激活定位
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    //停止定位
    @Override
    public void deactivate() {
        mListener = null;
    }

    //监听位置信息返回
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
                userLatitude = aMapLocation.getLatitude();//获取纬度
                userLongitude = aMapLocation.getLongitude();//获取经度
                isFirstLoc = false;
                loadData(); //定位成功，开始查询数据
                Log.d("wnw", userLatitude+ "   "+userLongitude);
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }
}