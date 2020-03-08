package com.wangkangli.roll_call;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

public class WklApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        context = getApplicationContext();

    }
    public static Context getContext(){
        return context;
    }
}
