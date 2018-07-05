package com.example.a41;

import android.app.Application;

import org.xutils.x;

/**
 * Created by 帅比浩宇 on 2018/3/8.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this); //初始化xUtils整个模块
    }
}
