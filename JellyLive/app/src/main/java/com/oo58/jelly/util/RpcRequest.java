package com.oo58.jelly.util;

import android.content.Context;
import android.os.Looper;
import android.os.Message;

import com.oo58.jelly.common.RpcEvent;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Desc: 一个rpc请求
 * Created by sunjinfang on 2016/6/23 14:03.
 */
public  class RpcRequest {
    private static final int HandlerCmd_Success = 88888 ;
    private static final int HandlerCmd_Failed = 88889 ;
    private static final int HandlerCmd_Error = 88810 ;

    private RpcEvent event;
    private OnRpcRequestListener onRpcListener;
    private Context context ;
    public RpcRequest(Context context, OnRpcRequestListener listener){
        onRpcListener = listener;
        this.context = context ;
    }

    /**
     * 发送一个请求
     * @param event
     * @param par
     */
    public  void doRequest(final RpcEvent event ,final Object...par){
        Runnable runnable = new Runnable() {
            public void run() {
                Message msg = new Message() ;
                try {
                    String result = Util.addRpcEvent(event,par);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        msg.what = HandlerCmd_Success;
                        msg.obj = result;
                        handler.sendMessage(msg) ;
                    } else {
                        msg.what = HandlerCmd_Failed;
                        msg.obj = result;
                        handler.sendMessage(msg) ;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.obj = "" ;
                    msg.what = HandlerCmd_Error;
                    handler.sendMessage(msg) ;
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    protected UIHandler handler = new UIHandler(Looper.getMainLooper(), new UIHandler.IHandler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HandlerCmd_Success:
                    onRpcListener.onSuccess(msg);
                    break ;
                case HandlerCmd_Failed:
                    onRpcListener.onFailed(msg);
                    break ;
                case HandlerCmd_Error:
                    onRpcListener.onError(msg);
                    break ;
            }
        }
    });

    /**
     * rpc请求的结果监听
     */
    public interface OnRpcRequestListener {
        public void onSuccess(Message msg) ;
        public void onFailed(Message msg);
        public void onError(Message msg);
    }
}
