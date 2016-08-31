package com.xuqi.weather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xuqi.weather.R;
import com.xuqi.weather.util.MyContext;
import com.xuqi.weather.util.Utility;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class WeatherActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "WeatherActivity";
    public String cityname = null;
    //切换城市按钮
    private Button switchCity;
    //更新天气按钮
    private Button refreshWeather;
    private LinearLayout weatherInfolayout;
    //显示城市天气的六个信息
    private ImageView image;
    private TextView city;
    private TextView date;
    private TextView weather;
    private TextView temperature;
    private TextView wind;
    private RequestQueue mQueue;

    //用于定位
    private MyContext con = new MyContext();
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    //声明定位回调监听器
    private AMapLocationListener mLocationListener;
    //声明AMapLocationClientOption对象
    //AMapLocationClientOption对象用来设置发起定位的模式和相关参数。
    private AMapLocationClientOption mLocationOption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String cityname = null;
        String str = sHA1(this);
        Log.d(TAG, "mmmmm = "+str);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        Log.d(TAG, "显示");
        //初始化
        initialize();

        String city_name = handleIntent(getIntent());
        //不为空说明是输入了城市名跳转而来
        if (!TextUtils.isEmpty(city_name)) {
            //输入了城市，显示天气
            date.setText("同步中");
            //天气信息不可见
            weatherInfolayout.setVisibility(View.INVISIBLE);
            city.setVisibility(View.INVISIBLE);
            queryWeatherByName(city_name);
        }
        //为空，使用定位函数获取城市名
        else {
            Log.d(TAG, "启动app");

            //初始化定位
            mLocationClient = new AMapLocationClient(this);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            // 低功耗定位模式：不会使用GPS和其他传感器，只会使用网络定位（Wi-Fi和基站定位）；
            // 设置定位模式为AMapLocationMode.Battery_Saving，低功耗模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
            //获取一次定位结果,该方法默认为false。
            mLocationOption.setOnceLocation(true);
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
                            //cityname = aMapLocation.getCity();//城市信息
                            Log.d(TAG, "test city = " + aMapLocation.getCity().toString().substring(0,2));

                            date.setText("同步中");
                            //天气信息不可见
                            weatherInfolayout.setVisibility(View.INVISIBLE);
                            city.setVisibility(View.INVISIBLE);
                            queryWeatherByName(aMapLocation.getCity().toString().substring(0,2));
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
        }
    }

    public void initialize(){
        //使用volley进行网络访问
        mQueue = Volley.newRequestQueue(this);
        //初始化控件
        weatherInfolayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        image = (ImageView) findViewById(R.id.weather_image);
        city = (TextView) findViewById(R.id.city_name);
        date = (TextView) findViewById(R.id.date_text);
        weather = (TextView) findViewById(R.id.weather_desp);
        temperature = (TextView) findViewById(R.id.temp);
        wind = (TextView) findViewById(R.id.wind);
        //两个按钮
        switchCity = (Button) findViewById(R.id.switch_city);
        refreshWeather = (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }
    private void queryWeatherByName(String city_name) {
        String address = "https://api.heweather.com/x3/weather?city="+city_name+"&key=0ce77532195847e9a821586471e9370b";
        Log.d(TAG, "address = "+address);
        queryFromServer(address);
    }

    private void queryFromServer(String data) {
        Log.d(TAG, "queryFromServer: ");
        //使用volley
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(data, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", response.toString());
                        //从服务器返回的json数据中解析出天气
                        Utility.handleWeatherResponse(WeatherActivity.this,response);
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        date.setText("同步失败");
                    }
                });
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    private void showWeather() {
        Log.d(TAG, "showWeather: ");
        SharedPreferences infor = PreferenceManager.getDefaultSharedPreferences(this);
        //显示图片
        ImageRequest imageRequest = new ImageRequest(
                infor.getString("image",""),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        image.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                image.setImageResource(R.drawable.ic_highlight_off_black_18dp);
            }
        });
        mQueue.add(imageRequest);
        //显示天气的其他文字信息
        city.setText(infor.getString("city",""));
        weather.setText(infor.getString("weather",""));
        wind.setText(infor.getString("wind",""));
        temperature.setText(infor.getString("temperature",""));
        date.setText(infor.getString("date",""));

        //天气信息可见
        weatherInfolayout.setVisibility(View.VISIBLE);
        city.setVisibility(View.VISIBLE);
    }

    private String handleIntent(Intent intent) {
        String query = intent.getStringExtra("city");
        Log.d(TAG, "handleIntent: query = "+query);
        return query;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,MainActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;

            case R.id.refresh_weather:
                date.setText("同步中.....");
                SharedPreferences infor = PreferenceManager.getDefaultSharedPreferences(this);
                String city = infor.getString("city","");
                if(!TextUtils.isEmpty(city)){
                    queryWeatherByName(city);
                }
                break;
            default:
                break;
        }
    }

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
