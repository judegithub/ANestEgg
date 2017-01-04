package com.keeplearning.anestegg.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.TextView;
import android.widget.Toast;

import com.keeplearning.anestegg.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

public class DeviceInfoActivity extends BaseActivity {

    private final String TAG = "DeviceInfoActivity";

    private TextView mInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        setupViews();

        getScreenInfo();
    }

    private void setupViews() {
        mInfoTextView = (TextView) findViewById(R.id.device_info_text_view);
        mInfoTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void getScreenInfo() {
        String deviceInfo;

        int screenWidth = 0;
        int screenHeight = 0;

        Display disPlay = getWindowManager().getDefaultDisplay();
        screenWidth = disPlay.getWidth();
        screenHeight = disPlay.getHeight();

        //屏幕分辨率容器
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        deviceInfo = "屏幕宽：" + screenWidth + "\n屏幕高：" + screenHeight;

        float density = displayMetrics.density;
        int densityDpi = displayMetrics.densityDpi;
        deviceInfo += "\n density：" + density + "\ndensityDpi：" + densityDpi;

        // 需要在AndroidManifest.xml中加入一个许可：android.permission.READ_PHONE_STATE
        // 根据不同的手机设备返回IMEI，MEID或者ESN码.
        // 缺点：在少数的一些设备上，该实现有漏洞，会返回垃圾数据
        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String imei = TelephonyMgr.getDeviceId();
        deviceInfo += "\n imei：" + imei;

        deviceInfo += "\n macAddress： " + getMacAddress(getBaseContext());
        deviceInfo += "\n 另一种方法获取的 macAddress：" + getLocalMacAddress();

        deviceInfo += "\n 您使用的是：" + checkNetworkState() + "网络";

        deviceInfo += "\n 您使用的运营商是：" + getNetworkOperators(getBaseContext());

        mInfoTextView.setText(deviceInfo);
    }

    public static String getMacAddress(Context context) {
        // 获取mac地址：
        String macAddress = "000000000000";
        try {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr
                    .getConnectionInfo());
            if (null != info) {
                if (!TextUtils.isEmpty(info.getMacAddress())) {
                    macAddress = info.getMacAddress();
//                    macAddress = info.getMacAddress().replace(":", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return macAddress;
        }
    }

    public String getLocalMacAddress() {
//        String mac = "";
//        try {
//            String path = "sys/class/net/eth0/address";
//            FileInputStream fis_name = new FileInputStream(path);
//            byte[] buffer_name = new byte[1024 * 8];
//            int byteCount_name = fis_name.read(buffer_name);
//            if (byteCount_name > 0) {
//                mac = new String(buffer_name, 0, byteCount_name, "utf-8");
//            }
//
//            if (mac.length() == 0 || mac == null) {
//                path = "sys/class/net/eth0/wlan0";
//                FileInputStream fis = new FileInputStream(path);
//                byte[] buffer = new byte[1024 * 8];
//                int byteCount = fis.read(buffer);
//                if (byteCount > 0) {
//                    mac = new String(buffer, 0, byteCount, "utf-8");
//                }
//            }
//
//            if (mac.length() == 0 || mac == null) {
//                return "";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return mac.trim();

            String macSerial = null;
            String str = "";

            try
            {
                Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
                InputStreamReader ir = new InputStreamReader(pp.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);

                for (; null != str;)
                {
                    str = input.readLine();
                    if (str != null)
                    {
                        macSerial = str.trim();// 去空格
                        break;
                    }
                }
            } catch (IOException ex) {
                // 赋予默认值
                ex.printStackTrace();
            }
            return macSerial;
    }

    private String checkNetworkState() {
        String typeName = "未知";

        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
            // 无法判断路由器是否联网
//            int type = activeNetworkInfo.getType();
            typeName = activeNetworkInfo.getTypeName();
//        int subtype = activeNetworkInfo.getSubtype();
//        String subtypeName = activeNetworkInfo.getSubtypeName();
        }

        return typeName;
    }

    private String getNetworkOperators(Context context) {
        String name = "未知";
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String subscriberId = tm.getSubscriberId();
        Log.d("DBG", TAG + " getNetworkOperators: subscriberId: " + subscriberId);
        if (TextUtils.isEmpty(subscriberId)) {
            return name;
        }

        if (subscriberId.startsWith("46000")
                || subscriberId.startsWith("46002")
                || subscriberId.startsWith("46007")) {
            name = "中国移动";
        } else if (subscriberId.startsWith("46001")) {
            name = "中国联通";
        } else if(subscriberId.startsWith("46003")) {
            name = "中国电信";
        }

        return name;
    }

}
