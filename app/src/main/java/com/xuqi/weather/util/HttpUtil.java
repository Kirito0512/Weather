package com.xuqi.weather.util;

/**
 * Created by Administrator on 2016/8/15.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/7/21.
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    /**
     * @param urlAll
     *            :请求接口
     * @param httpArg
     *            :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",  "您自己的apikey");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        Log.d(TAG, "sendHttpRequest: ");
        new Thread(new Runnable(){
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("apikey","1ccc09eb80c1b06de206f15b27fdf75f");
                    connection.setConnectTimeout(16000);
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuffer response = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        if(listener != null){
                            //回调onFinish()方法
                            Log.d(TAG, "response = "+response.toString());
                            listener.onFinish(response.toString());
                        }
                    }
                } catch (IOException e) {
                    if(listener != null)
                        //回调onError()方法
                    listener.onError(e);
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            }
            }).start();
            }
}
