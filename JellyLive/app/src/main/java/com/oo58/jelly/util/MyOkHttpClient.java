package com.oo58.jelly.util;
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
 * Desc: 简单封装的 OkHttpClient
 * Created by sunjinfang on 2016/5/10.
 */
public class MyOkHttpClient extends OkHttpClient {
    private String url; //请求的地址
    public MyOkHttpClient(String uri) {
        this.url = uri;
    }

    /**
     * 发送一条rpc请求
     * @param method  协议号
     * @param params 发送的消息体集合
     * @return 服务器返回的消息
     * @throws IOException
     */
    public final String doRequest(String method, Object[] params) throws IOException {

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
        // Create the json request object
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put("id", UUID.randomUUID().hashCode());
            jsonRequest.put("method", method);
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
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        /**
         * 发送请求 接收回调
         */
        Response response = newCall(request).execute();

        /**
         * 成功时 返回消息
         */
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    /**
     * Object[] 转json
     * @param array
     * @return  json数组
     */
    protected static JSONArray getJSONArray(Object[] array) {
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
