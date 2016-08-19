package com.xuqi.weather.util;

/**
 * Created by Administrator on 2016/8/15.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e );
}
