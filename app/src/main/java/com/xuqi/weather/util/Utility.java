package com.xuqi.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import com.xuqi.weather.db.WeatherDB;
import com.xuqi.weather.model.City;
import com.xuqi.weather.model.County;
import com.xuqi.weather.model.Province;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/15.
 */

//由于服务器返回的省县市数据都是“代号|城市，代号|城市”这种格式的，需要一个工具来解析和处理这种数据
public class Utility {
    private static final String TAG = "Utility";
    /*
    解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB,String response){
        //Returns true if the string is null or 0-length.
        if(!TextUtils.isEmpty(response)){
            String []allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length >0){
                for(String p:allProvinces){
                    String []array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    weatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /*
    解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(WeatherDB weatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String []allCities = response.split(",");
            if(allCities != null && allCities.length>0){
                for(String c:allCities){
                    String []array = c.split("\\|");
                    City city = new City();
                    city.setProvinceId(provinceId);
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    //将解析出来的数据存储到City表
                    weatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }


    /*
    解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(WeatherDB weatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String []allCountries = response.split(",");
            if(allCountries != null && allCountries.length>0){
                for(String c:allCountries){
                    String []array = c.split("\\|");
                    County county = new County();
                    county.setCityId(cityId);
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    //将解析出来的数据存储到City表
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /*
    解析服务器返回的JSON数据，并将解析出的数据存储到本地
     */
    public static void handleWeatherResponse(Context context,JSONObject response){
        try{
            Log.d(TAG, "handleWeatherResponse: ");
            //JSONObject jsonObject = new JSONObject(response);
            String str = response.toString().substring(response.toString().indexOf("[")+1,response.toString().lastIndexOf("]"));
            Log.d(TAG, "str = "+str);
            response = new JSONObject(str);
            String code = response.getString("status");

            if(code.equals("ok")){
                String city = new JSONObject(response.getString("basic")).getString("city");
                String result = response.getString("daily_forecast");
                Log.d(TAG, "result = "+result);
                //result = result.substring(result.indexOf("today")+7,result.indexOf("future")-2);
                //Json数组类型数据
                JSONArray jsonArray = new JSONArray(result);
                JSONObject infor = jsonArray.getJSONObject(0);

                //当天天气信息
                JSONObject cond = new JSONObject(infor.getString("cond"));
                String weather_image = "http://files.heweather.com/cond_icon/"+cond.getString("code_d")+".png";
                String weather = cond.getString("txt_d");
                Log.d(TAG, "cond = "+cond.toString());

                JSONObject tmp = new JSONObject(infor.getString("tmp"));
                String temperature = tmp.getString("min")+"℃~"+tmp.getString("max")+"℃";

                JSONObject wid = new JSONObject(infor.getString("wind"));
                String wind = wid.getString("sc");
                String date = infor.getString("date");
                
                saveWeatherInfo(context,city,weather,weather_image,temperature,wind,date);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    将服务器返回的所有天气信息存储到SharedPreferences文件中
     */
    private static void saveWeatherInfo(Context context, String city, String weather,String image, String temperature, String wind, String date) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("city",city);
        editor.putString("weather",weather);
        editor.putString("image",image);
        editor.putString("temperature",temperature);
        editor.putString("wind",wind);
        editor.putString("date",date);
        editor.commit();
    }
}
