package com.wnw.washcar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wnw.washcar.R;
import com.wnw.washcar.bean.Store;
import com.wnw.washcar.bean.StoreBean;

import java.util.List;

/**
 * Created by wnw on 2017/4/6.
 */

public class StoreAdapter extends BaseAdapter {

    private Context context;
    private List<StoreBean> storeBeanList;

    public StoreAdapter(Context context, List<StoreBean> storeBeanList){
        this.context = context;
        this.storeBeanList = storeBeanList;
    }

    public void setStoreBeanList(List<StoreBean> storeBeanList){
        this.storeBeanList = storeBeanList;
    }

    @Override
    public int getCount() {
        return storeBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return storeBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        StoreHolder storeHolder = null;
        if(view == null){
            storeHolder = new StoreHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_lv_store, null);
            storeHolder.storeImg = (ImageView)view.findViewById(R.id.store_img);
            storeHolder.nameTv = (TextView)view.findViewById(R.id.store_name);
            storeHolder.addressTv = (TextView)view.findViewById(R.id.store_address);
            storeHolder.distanceTv = (TextView)view.findViewById(R.id.distance);
            view.setTag(storeHolder);
        }else {
            storeHolder = (StoreHolder)view.getTag();
        }
        Glide.with(context).load(storeBeanList.get(i).getPic().getFileUrl()).into(storeHolder.storeImg);
        storeHolder.nameTv.setText(storeBeanList.get(i).getName());
        storeHolder.addressTv.setText(storeBeanList.get(i).getAddress());
        storeHolder.distanceTv.setText(storeBeanList.get(i).getDistance()+"km");
        return view;
    }

    private class StoreHolder{
        ImageView storeImg;
        TextView nameTv;
        TextView addressTv;
        TextView distanceTv;
    }
}
