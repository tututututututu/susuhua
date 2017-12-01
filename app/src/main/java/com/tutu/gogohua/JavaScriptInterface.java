package com.tutu.gogohua;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tutu.gogohua.beana.JumpEvent;
import com.tutu.gogohua.contact.ContactGetHelper;
import com.tutu.gogohua.rxbus.RxBus2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 47066 on 2017/9/6.
 */

public class JavaScriptInterface {
    private Activity context;
    private int count = 0;


    public JavaScriptInterface(Activity context) {
        this.context = context;
    }

    @JavascriptInterface
    public void upLoadAddressBook(final String card) {
        HandlerUtil.post(new Runnable() {
            @Override
            public void run() {
                requestPermissionContact(card);
            }
        });
    }

    @JavascriptInterface
    public void onUploadLocation(final String card) {
        count = 0;
        HandlerUtil.post(new Runnable() {
            @Override
            public void run() {
                requestPermissionLocation(card);
            }
        });
    }

    private void requestPermissionLocation(final String card) {
        RxPermissions rxPermissions = new RxPermissions(context);

        rxPermissions.request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            requestLocation(card);
                        } else {
                            Toast.makeText(App.app, "没有获取到需要的权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void requestPermissionContact(final String card) {
        RxPermissions rxPermissions = new RxPermissions(context);

        rxPermissions.request(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            requestContacts(card);
                        } else {
                            Toast.makeText(App.app, "没有获取到需要的权限", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void requestContacts(String card) {
        if (TextUtils.isEmpty(card)) {
            Toast.makeText(App.app, "上传失败", Toast.LENGTH_SHORT).show();
            return;
        }


        Map<String, String> params = new HashMap<>();
        params.put("card", card);
        params.put("datas", GsonUtils.listToJson(new ContactGetHelper().getContacts(App.app)));


        OkGo.<String>post(Constants.UPLOAD_CONTACTS_URL)
                .tag(this)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        ResponBean responBean = GsonUtils.jsonToObj(response.body().toString(), ResponBean.class);
                        if (responBean != null && "3".equals(responBean.getStruts())) {
                            Toast.makeText(App.app, "上传成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(App.app, "上传失败", Toast.LENGTH_LONG).show();
                        }

                        RxBus2.getDefault().post(new JumpEvent(Constants.UPLOAD_SUCCESS_URL));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }

    private void requestLocation(final String card) {
        String lng = SPUtils.getString(Constants.LNG);
        String lat = SPUtils.getString(Constants.LAT);

        if (TextUtils.isEmpty(lng) ||
                TextUtils.isEmpty(lat)) {
            Toast.makeText(App.app, "定位失败", Toast.LENGTH_SHORT).show();
            return;
        }



        Map<String, String> params = new HashMap<>();
        params.put("card", card);
        params.put("lng", lng);  //经度
        params.put("lat", lat);  //纬度

        Toast.makeText(App.app, "lat="+lat+" lng="+lng,Toast.LENGTH_SHORT).show();

//        latitude : 4.9E-324
//        lontitude : 4.9E-324
//        params.put("lng", "120.423");  //经度
//        params.put("lat", "65.432");  //纬度


        OkGo.<String>post(Constants.UPLOAD_LOCATION_URL)
                .tag(this)
                .params(params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        ResponBean responBean = GsonUtils.jsonToObj(response.body().toString(), ResponBean.class);
                        if (responBean != null && "0".equals(responBean.getStruts())) {
                            if (count == 0) {
                                Toast.makeText(App.app, "上传成功", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (count == 0) {
                                Toast.makeText(App.app, "上传失败", Toast.LENGTH_LONG).show();
                            }
                        }
                        if (count == 0) {
                            RxBus2.getDefault().post(new JumpEvent(Constants.UPLOAD_SUCCESS_URL));
                        }
                        retry(card);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        retry(card);
                        super.onError(response);
                    }
                });

    }

    private void retry(String card) {
        if (count < 3) {
            requestLocation(card);
            count++;
        }
    }

}
