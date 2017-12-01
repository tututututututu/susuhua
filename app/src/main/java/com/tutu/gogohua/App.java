package com.tutu.gogohua;

import android.app.Application;

/**
 * Created by 47066 on 2017/9/6.
 */

public class App extends Application {
    public static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        SPUtils.initSP("appData");
    }
}
