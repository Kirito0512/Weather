package com.xuqi.weather.maplocation;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xuqi.weather.util.MyContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/8/21.
 */
public class Location {
        private static final String TAG = "Location";
        String city = null;
        private MyContext con = new MyContext();
        //声明AMapLocationClient类对象
        private AMapLocationClient mLocationClient;
        //声明定位回调监听器
        private AMapLocationListener mLocationListener;
        //声明AMapLocationClientOption对象
        //AMapLocationClientOption对象用来设置发起定位的模式和相关参数。
        private AMapLocationClientOption mLocationOption;

    public String getLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(con.getContext());

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        // 低功耗定位模式：不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位）；
        // 设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //获取一次定位结果,该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
//            设置setOnceLocationLatest(boolean b)接口为true，
//            启动定位时SDK会返回最近3s内精度最高的一次定位结果。
//            如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
//             */
        //mLocationOption.setOnceLocationLatest(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);

        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    //定位成功
                    if (aMapLocation.getErrorCode() == 0) {
                        Log.d(TAG, "onLocationChanged: 定位成功");
                        //定位成功，可在其中解析amapLocation获取相应内容
                        //                        aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        //                        aMapLocation.getLatitude();//获取纬度
                        //                        aMapLocation.getLongitude();//获取经度
                        //                        aMapLocation.getAccuracy();//获取精度信息
                        //                        aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        //                        aMapLocation.getCountry();//国家信息
                        //                        aMapLocation.getProvince();//省信息
                        //                        aMapLocation.getDistrict();//城区信息
                        //                        aMapLocation.getStreet();//街道信息
                        //                        aMapLocation.getStreetNum();//街道门牌号信息
                        //                        aMapLocation.getCityCode();//城市编码
                        //                        aMapLocation.getAdCode();//地区编码
                        //                        aMapLocation.getAoiName();//获取当前定位点的AOI信息
                        city = aMapLocation.getCity();//城市信息
                        Log.d(TAG, "test city = " +city);
                        //获取定位时间
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date(aMapLocation.getTime());
                        df.format(date);
                    } else {
                        Log.d(TAG, "onLocationChanged: 定位失败");
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                } else
                    Log.d(TAG, "onLocationChanged: aMapLocation = " + aMapLocation.toString());
            }
        };
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //启动定位
        mLocationClient.startLocation();
        return city;
    }

    public void closeLocation(){
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }
}

