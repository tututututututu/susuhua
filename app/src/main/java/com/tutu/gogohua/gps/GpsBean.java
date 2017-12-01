package com.tutu.gogohua.gps;

/**
 * Created by tutu on 2017/7/24.
 */

public class GpsBean {
    private String lat;
    private String lng;
    private String add;

    public GpsBean() {
    }

    public GpsBean(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    @Override
    public String toString() {
        return "GpsBean{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", add='" + add + '\'' +
                '}';
    }
}
