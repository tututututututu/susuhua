package com.tutu.gogohua;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.just.library.AgentWeb;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tutu.gogohua.beana.JumpEvent;
import com.tutu.gogohua.gps.LocationHelper;
import com.tutu.gogohua.rxbus.RxBus2;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private AgentWeb mAgentWeb;
    private LinearLayout parent;
    private LocationHelper locationHelper;

    //15116318447   zxc123
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parent = (LinearLayout) findViewById(R.id.parent);


        mAgentWeb = AgentWeb.with(this)//传入Activity
                .setAgentWebParent(parent, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                //.setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go(Constants.INDEX_URL);

        mAgentWeb.getJsInterfaceHolder().addJavaObject("js", new JavaScriptInterface(this));
        RxBus2.getDefault().toFlowable(JumpEvent.class)
                .subscribe(new Consumer<JumpEvent>() {
                    @Override
                    public void accept(JumpEvent jumpEvent) throws Exception {
                        mAgentWeb.getWebCreator().create().get().loadUrl(jumpEvent.getUrl());
                    }
                });


        RxPermissions rxPermissions = new RxPermissions(this);

        rxPermissions.request(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            locationHelper = new LocationHelper(getApplicationContext());
                            locationHelper.initLocation();
                        } else {
                            Toast.makeText(App.app, "没有获取到需要的权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }
}
