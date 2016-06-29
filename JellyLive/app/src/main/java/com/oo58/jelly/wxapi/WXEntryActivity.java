package com.oo58.jelly.wxapi;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.oo58.jelly.util.ThreadPoolWrap;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private String access_token = "";
    private String openid = "";
    public static final String action = "com.oo58.jelly.wxloginaction";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//		setContentView(R.layout.weixin);
        handleIntent(getIntent());
    }

    @Override
    public void onReq(BaseReq arg0) {
        Log.e("sjf", "sjf errorcode == " + 33);
    }

    @Override
    public void onResp(BaseResp resp) {

        Log.e("sjf", "sjf errorcode == " + 22);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        handleIntent(intent);

        Log.e("sjf", "sjf errorcode == " + 11);
    }

    private void handleIntent(Intent intent) {
        SendAuth.Resp resp = new SendAuth.Resp(intent.getExtras());
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            // 用户同意
            String code = ((SendAuth.Resp) resp).code;
            Log.i("lvjian", "code======================>" + code);

            if (code == "" || "".equals(code) || code == null) {
                finish();
            } else {
                String dizhi = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxe3b6571489e8a96d&secret=9c02e9ea17fd0cf6ca73640a970ee7ef&code="
                        + code + "&grant_type=authorization_code";
                GETHTTP(dizhi);
            }

        } else {
            // 用户不同意
            finish();
        }
    }

    private void GETHTTP(final String url) {
        Runnable canclefollowrun = new Runnable() {
            @Override
            public void run() {
                String result = get(url);
                try {
                    JSONObject obj = new JSONObject(result);
                    openid = obj.getString("openid");
                    access_token = obj.getString("access_token");
                    handler.sendEmptyMessage(1);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    finish();
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(canclefollowrun);
    }

    private String get(String url) {
        String result = null;


//创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
//创建一个Request
        final Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            /**
             * 发送请求 接收回调
             */
            Response response = mOkHttpClient.newCall(request).execute();

            /**
             * 成功时 返回消息
             */
            if (response.isSuccessful()) {
                result = response.body().string();
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(action);
                    intent.putExtra("openid", openid);
                    intent.putExtra("access_token", access_token);
                    sendBroadcast(intent);
                    finish();
                    break;
                case 2:
                    finish();
                    break;
                default:
                    break;
            }
        }

        ;
    };
}