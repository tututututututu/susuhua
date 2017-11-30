package com.tutu.susuhua;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tutu.susuhua.beana.JumpEvent;
import com.tutu.susuhua.contact.ContactGetHelper;
import com.tutu.susuhua.rxbus.RxBus2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 47066 on 2017/9/6.
 */

public class JavaScriptInterface {
    private Activity context;


    public JavaScriptInterface(Activity context) {
        this.context = context;
    }

    @JavascriptInterface
    public void upLoadAddressBook(final String card) {
        HandlerUtil.post(new Runnable() {
            @Override
            public void run() {
                requestPermission(card);
            }
        });
    }


    private void requestPermission(final String card) {
        RxPermissions rxPermissions = new RxPermissions(context);

        rxPermissions.request(
                Manifest.permission.READ_CONTACTS
        )
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            request(card);
                        } else {
                            Toast.makeText(App.app, "没有获取到需要的权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void request(String card) {
        if (TextUtils.isEmpty(card)) {
            Toast.makeText(App.app, "上传失败", Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, String> params = new HashMap<>();
        params.put("card", card);
        params.put("datas", GsonUtils.listToJson(new ContactGetHelper().getContacts(App.app)));


        OkGo.<String>post("http://www.kayouxiang.com/ansubmit")
                .tag(this)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        ResponBean responBean = GsonUtils.jsonToObj(response.body().toString(), ResponBean.class);
                        if (responBean!=null&&"3".equals(responBean.getStruts())) {
                            Toast.makeText(App.app, "上传成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(App.app, "上传失败", Toast.LENGTH_LONG).show();
                        }

                        RxBus2.getDefault().post(new JumpEvent("http://www.kayouxiang.com/mobile/myRz.htm"));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }
}
