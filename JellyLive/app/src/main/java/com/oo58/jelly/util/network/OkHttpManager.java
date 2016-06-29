package com.oo58.jelly.util.network;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @author zhongxf
 * @Description OkHttp的管理类
 * @Date 2016/6/28.
 */
public class OkHttpManager {
    private OkHttpClient client;
    private static OkHttpManager manager;

    //私有构造方法
    private OkHttpManager() {
        client = new OkHttpClient();
        initClient();
    }

    //初始化OkHttpClent
    private void initClient() {
        client.setConnectTimeout(30, TimeUnit.SECONDS);//设置超时时间
    }


    public static OkHttpManager getIntance() {
        if (manager == null) {
            manager = new OkHttpManager();
        }
        return manager;
    }

    //获取OkHttpClient的对象
    public OkHttpClient getOkHttpClient() {
        return client;
    }

}
