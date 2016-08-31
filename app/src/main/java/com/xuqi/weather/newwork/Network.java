package com.xuqi.weather.newwork;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.xuqi.weather.util.MyContext;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Administrator on 2016/8/20.
 */
public class Network {
    private static final String TAG = "Network";
    static String ip;
    public static void getNetWorkStatus(){
        Context context = MyContext.getContext();
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        int NetWorkType = networkInfo.getType();
        if (networkInfo!= null && networkInfo.isConnected()) {
            if(ConnectivityManager.TYPE_MOBILE == NetWorkType){
                //当前为mobile网络
                Log.d(TAG, "移动数据 网络下");
                ip = getLocalIp();
                Log.d(TAG,"本地ip-----"+ip);
            }
            else if(ConnectivityManager.TYPE_WIFI == NetWorkType)
            {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipAddress = wifiInfo.getIpAddress();
                ip = intToIp(ipAddress);
                Log.d(TAG,"wifi_ip地址为------"+ip);
            }
        }
    }


    public static String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }


    private static String getLocalIp() {
        String ip;

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&!inetAddress.isLinkLocalAddress()) {
//	    	                	ip=inetAddress.getHostAddress().toString();
                        System.out.println("ip=========="+inetAddress.getHostAddress().toString());
                        return inetAddress.getHostAddress().toString();

                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, "getLocalIp: "+ex.toString());
        }
        return null;
    }
}
