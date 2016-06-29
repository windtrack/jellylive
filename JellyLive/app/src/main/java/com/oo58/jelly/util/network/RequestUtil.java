package com.oo58.jelly.util.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

/**
 * @author zhongxf
 * @Description 网络请求的工具类
 * @Date 2016/6/28.
 */
public class RequestUtil {
    //网络请求错误
    public final static int NET_ERROR_CODE = 1001;
    //服务器返回数据格式错误
    public final static int SERVER_DATA_FORMAT_ERROR_CODE = 1003;
    //其他未知网络请求错误
    public final static int OTHER_ERROR_CODE = 1002;

    private OkHttpClient client;//okHttp的client对象

    public RequestUtil() {
        client = OkHttpManager.getIntance().getOkHttpClient();
    }

    private Handler handler = new Handler();

    public void doRequest(Context content, final RpcEvent event, final RequestCallBack callBack, final Object... params) {

        if (!NetWorkUtil.isNetworkAvailable(content)) {//当手机没有网络的时候
            callBack.doFaild(NET_ERROR_CODE);
            return;
        }
        Runnable runnable = new Runnable() {
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.doStart();//开始执行请求
                    }
                });

                /**
                 * Object[] 转json
                 */
                JSONArray jsonParams = new JSONArray();
                for (int i = 0; i < params.length; i++) {
                    if (params[i].getClass().isArray()) {
                        jsonParams.put(getJSONArray((Object[]) params[i]));
                    }
                    jsonParams.put(params[i]);
                }
                JSONObject jsonRequest = new JSONObject();
                try {
                    jsonRequest.put("id", UUID.randomUUID().hashCode());
                    jsonRequest.put("method", event.name);
                    jsonRequest.put("params", jsonParams);
                    jsonRequest.put("jsonrpc", "2.0");
                } catch (JSONException e1) {

                }
                /**
                 * 组装数据
                 * 发送的消息类型  为json数据  编码格式 utf-8
                 */
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequest.toString());
                /**
                 * 创建一个连接请求
                 */
                Request request = new Request.Builder().url(AppUrl.APP_RPC).post(body).build();
                /**
                 * 发送请求 接收回调
                 */
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        Log.e("unfind", "服务器：" + res);
                        JSONObject serverJson = new JSONObject(res);
                        final JSONObject resJson = serverJson.getJSONObject("result");
                        int resCode = resJson.getInt("s");
                        if (resCode == 1) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.doSuccess(resJson);//请求执行成功
                                }
                            });
                        } else {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callBack.doError(resJson);//服务器业务逻辑报错
                                }
                            });
                        }
                    } else {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callBack.doFaild(OTHER_ERROR_CODE);//服务器处理请求错误错误
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.doFaild(OTHER_ERROR_CODE);//服务器请求不成功错误
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.doFaild(SERVER_DATA_FORMAT_ERROR_CODE);//服务器返回数据错误
                        }
                    });

                } finally {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.doFinish();//结束执行
                        }
                    });
                    ThreadPoolWrap.getThreadPool().removeTask(this);//将线程从线程池钟移除
                }
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);//利用线程池执行请求线程

    }


    /**
     * Object[] 转json
     *
     * @param array
     * @return json数组
     */
    private JSONArray getJSONArray(Object[] array) {
        JSONArray arr = new JSONArray();
        for (Object item : array) {
            if (item.getClass().isArray()) {
                arr.put(getJSONArray((Object[]) item));
            } else {
                arr.put(item);
            }
        }
        return arr;
    }
}
