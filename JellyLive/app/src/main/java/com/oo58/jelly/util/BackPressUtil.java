package com.oo58.jelly.util;

import android.app.Activity;
import android.widget.Toast;

/**
 * Desc:
 * Created by sunjinfang on 2016/6/1 15:09.
 */
public class BackPressUtil {



    private static long currentBackPressedTime = 0;// 按下返回键的当前手机系统时间
    private static final int BACK_PRESSED_INTERVAL = 2000; // 两次按下返回键的在这个时间间隔内才会退出

    /**
     * 按两次退出应用
     * @param activity
     */
    public static void onBackPressedToExit(Activity activity) {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(activity, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            // 退出
            activity.finish();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 按两次退出当年界面
     * @param activity
     */
    public static void onBackPressedToFinishCurActivity(Activity activity,String tips) {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(activity, tips, Toast.LENGTH_SHORT).show();
        } else {
            // 退出
            activity.finish();
        }
    }

    /**
     * 自己决定返回键做什么
     * @param activity
     * @return
     */
    public static boolean doBackPressed(Activity activity ){
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            return false ;
        } else {
            return true ;
        }
    }

}
