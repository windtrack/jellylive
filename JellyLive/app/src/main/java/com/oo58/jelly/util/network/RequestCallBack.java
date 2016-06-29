package com.oo58.jelly.util.network;

import org.json.JSONObject;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/28.
 */
public interface RequestCallBack {
    //请求开始
    void doStart();
    //请求结束
    void doFinish();

    //请求成功
    void doSuccess(JSONObject resJson);

    //请求失败（发生请求的未知失败原因）
    void doFaild(int ERROR_CODE);

    //请求出错(服务器出错)
    void doError(JSONObject resJson);


}
