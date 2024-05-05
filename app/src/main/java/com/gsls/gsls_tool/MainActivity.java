package com.gsls.gsls_tool;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;

import com.gsls.gt.GT;
import com.gsls.gt_databinding.annotation.GT_DataBinding;

@GT_DataBinding(setLayout = "activity_main", setBindingType = GT_DataBinding.Activity)
@GT.Annotations.GT_AnnotationActivity(R.layout.activity_main)
public class MainActivity extends MainActivityBinding {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GT.logt("单击");


                NotificationCompat.Builder builder = GT.GT_Notification.createNotificationForNormal(
                        MainActivity.this,
                        com.gsls.gt.R.mipmap.gt_logo,//通知栏图标
                        com.gsls.gt.R.mipmap.gt_logo,//通知栏 右下角图片
                        "通知栏标题",//通知栏标题
                        //通知栏内容
                        "1.GT库在很早的版本里就有出 " +
                                "通知栏封装方法，但使用起来非常有局限性，" +
                                "接下来咋们来看看新版GT库里的8种通知栏，是如何实现的",
                        true,//通知栏单击是否自动取消
                        true,//锁屏后是否弹出
                        null,//单击跳转的页面
                        0,//发送通知栏的时间
                        true,//是否 直接启动通知栏
                        222//当前通知的 Id编号
                );
                GT.GT_Notification.startNotification(MainActivity.this, builder,222);

            }
        });
    }




}


