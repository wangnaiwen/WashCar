package com.wnw.washcar.bean;

import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by wnw on 2017/4/6.
 */

public class StoreBean {
    private String name;
    private String address;
    private double distance;
    private BmobFile pic;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public BmobFile getPic() {
        return pic;
    }

    public void setPic(BmobFile pic) {
        this.pic = pic;
    }
}
