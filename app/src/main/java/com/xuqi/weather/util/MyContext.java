package com.xuqi.weather.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/8/9.
 */
public class MyContext extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
