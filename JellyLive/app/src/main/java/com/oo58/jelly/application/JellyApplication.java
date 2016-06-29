package com.oo58.jelly.application;


import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.oo58.jelly.common.AppContance;

/**
 * @author zhongxf
 * @Description Jelly直播的启动的Application
 * @Date 2016/6/13.
 */
public class JellyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initVersion() ;
    }




    /**
     * 获取软件版本信息
     */
    public void initVersion() {
        try {
            PackageInfo packageInfo = getApplicationContext()
                    .getPackageManager().getPackageInfo(getPackageName(), 0);
            AppContance.localVersionCode = packageInfo.versionCode;
            AppContance.localVersionName = packageInfo.versionName;

            /**
             * 获取手机识别码
             */
            TelephonyManager telephonemanager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            AppContance.phoneImeToken =telephonemanager.getDeviceId();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
