package com.xuqi.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xuqi.weather.R;
import com.xuqi.weather.util.Utility;

import org.json.JSONObject;

public class WeatherActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "WeatherActivity";
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
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

        String city_name = handleIntent(getIntent());
        if(!TextUtils.isEmpty(city_name)){
            //输入了城市，显示天气
            date.setText("同步中");
            //天气信息不可见
            weatherInfolayout.setVisibility(View.INVISIBLE);
            city.setVisibility(View.INVISIBLE);
            queryWeatherByName(city_name);
        }
    }

    private void queryWeatherByName(String city_name) {
        String address = "https://api.heweather.com/x3/weather?city="+city_name+"&key=0ce77532195847e9a821586471e9370b";
        Log.d(TAG, "address = "+address);
        queryFromServer(address);
    }
    private void queryWeatherByIP(String ip){
        String address = "https://api.heweather.com/x3/weather?cityip="+ip+"&key=0ce77532195847e9a821586471e9370b";
        Log.d(TAG, "ip = "+ip);
        queryFromServer(ip);
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
        //从网站上获取json数据
//        HttpUtil.sendHttpRequest(data, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//                if(!TextUtils.isEmpty(response)){
//                    //从服务器返回的json数据中解析出天气
//                    Utility.handleWeatherResponse(WeatherActivity.this,response);
//                    runOnUiThread(new Runnable(){
//                        @Override
//                        public void run() {
//                            showWeather();
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onError(Exception e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        date.setText("同步失败");
//                    }
//                });
//            }
//        });
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
        Toast.makeText(this,query,Toast.LENGTH_SHORT).show();
        Log.d(TAG, "handleIntent: query = "+query);
        return query;
            //通过某种方法，根据请求检索你的数据
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
}
