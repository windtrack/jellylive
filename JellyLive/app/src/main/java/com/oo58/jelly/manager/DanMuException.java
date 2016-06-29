package com.oo58.jelly.manager;

import android.util.Log;

/**
 * @author zhongxf
 * @Description 弹幕出现的异常
 * @Date 2016/6/27.
 */
public class DanMuException extends  Exception {


    @Override
    public void printStackTrace() {
        super.printStackTrace();
        Log.e("JellyLive","弹幕异常");
    }

}
