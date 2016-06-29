package com.oo58.jelly.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Desc:
 * Created by sunjinfang on 2016/5/10.
 */
public class UIHandler extends Handler {

    private IHandler handler;
    boolean pause; //当前界面是否销毁

    public UIHandler(Looper looper) {
        super(looper);
    }

    public UIHandler(Looper looper, IHandler handler) {
        super(looper);
        this.handler = handler;
    }

    public void setHandler(IHandler handler) {
        this.handler = handler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (handler != null && !pause) {
            handler.handleMessage(msg);
        } else {
            Log.i("sjf", "handler has destoryed!");
        }
    }

    public interface IHandler {
        public void handleMessage(Message msg);
    }

    /**
     * 销毁
     */
    public void pause() {
        pause = true;
    }

    public void resume(){
        pause = false ;
    }
}
