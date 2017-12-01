package com.tutu.gogohua;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class FlashActivity extends AppCompatActivity {
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        iv = (ImageView) findViewById(R.id.iv_img);
        iv.postDelayed(new Runnable() {
            @Override
            public void run() {
                jump();
            }
        }, 2000);
//        RequestOptions options = new RequestOptions()
//                .centerCrop()
//                .placeholder(R.drawable.splash)
//                .priority(Priority.HIGH)
//                .diskCacheStrategy(DiskCacheStrategy.NONE);

//        Glide.with(this)
//                .load("http://www.kayouxiang.com/q/images/adver.jpg")
//                .apply(options)
//                .listener(new RequestListener<Drawable>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                        iv.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                jump();
//                            }
//                        }, 2000);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//
//                        iv.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                jump();
//                            }
//                        }, 3000);
//                        return false;
//                    }
//                })
//
//                .into(iv);
    }


    private void jump() {
        Intent intent = new Intent(FlashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
