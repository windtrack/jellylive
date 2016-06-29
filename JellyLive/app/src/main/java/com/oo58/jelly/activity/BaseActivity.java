package com.oo58.jelly.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.oo58.jelly.util.UIHandler;

/**
 * @author zhongxf
 * @Description activity的基类
 * @Date 2016/6/13.
 */
public abstract class BaseActivity extends Activity {
    protected UIHandler handler = new UIHandler(Looper.getMainLooper());
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHandler(); // 设置handler
        mContext = this; //获取上下文
    }

    /**
     * Desc: 设置handler
     */
    private void setHandler() {
        handler.setHandler(new UIHandler.IHandler() {
            public void handleMessage(Message msg) {
                handler(msg);
            }
        });
    }

    /**
     * handler消息接收
     *
     * @param msg
     */
    protected abstract void handler(Message msg);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.resume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showToast(String str){
        Toast.makeText(mContext,str,Toast.LENGTH_SHORT).show();
    }
}
