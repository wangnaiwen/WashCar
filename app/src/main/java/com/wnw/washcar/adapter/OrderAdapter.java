package com.wnw.washcar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wnw.washcar.R;
import com.wnw.washcar.bean.Order;

import java.util.List;

/**
 * Created by wnw on 2017/4/7.
 */

public class OrderAdapter extends BaseAdapter {

    private Context context;
    private List<Order> orderList;

    public OrderAdapter(Context context, List<Order> orderList){
        this.context = context;
        this.orderList = orderList;
    }
    private void setOrderBeanList(List<Order> orderList){
        this.orderList = orderList;
    }
    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int i) {
        return orderList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        OrderHolder orderHolder = null;
        if(view == null){
            orderHolder = new OrderHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_lv_order, null);
            orderHolder.nameTv = (TextView) view.findViewById(R.id.tv_order_store_name);
            orderHolder.priceTv = (TextView)view.findViewById(R.id.tv_order_price);
            orderHolder.timeTv = (TextView)view.findViewById(R.id.tv_order_time);
            view.setTag(orderHolder);
        }else {
            orderHolder = (OrderHolder)view.getTag();
        }
        Order order = orderList.get(i);
        orderHolder.nameTv.setText(order.getStoreName());
        orderHolder.priceTv.setText(order.getMoney()+"");
        orderHolder.timeTv.setText(order.getCreatedAt());
        return view;
    }

    private class OrderHolder{
        TextView nameTv;
        TextView priceTv;
        TextView timeTv;
    }
}
