package com.wangkangli.roll_call.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.wangkangli.roll_call.Activitys.RegisterActivity;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static android.support.v4.content.ContextCompat.getSystemService;

public class PhoneService {

    public static String getLocalMacAddress(Context mContext) throws SocketException{
//        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
////        WifiInfo info = wifi.getConnectionInfo();
////       Log.d("TAGphonemac",info.getMacAddress());
////        return info.getMacAddress();
       String address = null;
       // 把当前机器上的访问网络接口的存入 Enumeration集合中
       Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
       Log.d("TEST_BUG", " interfaceName = " + interfaces );
       while (interfaces.hasMoreElements()) {
           NetworkInterface netWork = interfaces.nextElement();
           // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
           byte[] by = netWork.getHardwareAddress();
           if (by == null || by.length == 0) {
               continue;
           }
           StringBuilder builder = new StringBuilder();
           for (byte b : by) {
               builder.append(String.format("%02X:", b));
           }
           if (builder.length() > 0) {
               builder.deleteCharAt(builder.length() - 1);
           }
           String mac = builder.toString();
           Log.d("TEST_BUG", "interfaceName="+netWork.getName()+", mac="+mac);
           // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
           if (netWork.getName().equals("wlan0")) {
               Log.d("TEST_BUG", " interfaceName ="+netWork.getName()+", mac="+mac);
               address = mac;
           }
       }
       return address;

    }

   static public String getPhoneNum(Context mContext) {
        String PhoneNum = "0";
        List<String> permissionList = new ArrayList<>();
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions((Activity) mContext, permissions, 1);
        } else {
            PhoneNum = tm.getLine1Number();//获取本机号码
            Log.d("TAGphonenum", PhoneNum);
        }
        return PhoneNum;
    }

   static public String getImei(Context mContext) {
        String imei = "未获取";
        List<String> permissionList = new ArrayList<>();

        TelephonyManager telephonemanage = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions((Activity) mContext, permissions, 1);
        } else {


            imei = telephonemanage.getDeviceId();
            Log.d("TAGimei", imei);
        }
        return imei;
    }
    static public String getPhoneNumber(Context mContext) {
        String forReturn = "";

        List<String> permissionList = new ArrayList<>();
        TelephonyManager telephonemanage = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);


        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions((Activity) mContext, permissions, 1);
        } else {
            forReturn = telephonemanage.getLine1Number();
        }
        return forReturn;
    }

}
