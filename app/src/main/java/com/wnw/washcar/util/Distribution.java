package com.wnw.washcar.util;

/**
 * Created by wnw on 2017/4/6.
 */

public class Distribution
{
    // 经度
    double longitude;
    // 维度
    double latitude;

    public Distribution(double longitude, double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}