package com.tutu.gogohua.gps;


import com.tutu.gogohua.Constants;
import com.tutu.gogohua.SPUtils;

/**
 * Created by tutu on 2017/7/24.
 */

public class GpsGetHelper {
    public static GpsBean getGps() {
        GpsBean gpsBean = new GpsBean();
        gpsBean.setLat(SPUtils.getString(Constants.LAT));
        gpsBean.setLng(SPUtils.getString(Constants.LNG));
        gpsBean.setAdd(SPUtils.getString(Constants.ADDR));
        return gpsBean;
    }
}
