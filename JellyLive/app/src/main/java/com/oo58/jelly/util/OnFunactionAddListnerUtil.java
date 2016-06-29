package com.oo58.jelly.util;

import android.content.Context;
import android.os.Looper;
import android.os.Message;

import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.manager.GlobalData;

/**
 * Desc:
 * Created by sunjinfang on 2016/6/23 13:41.
 */
public class OnFunactionAddListnerUtil {
    private static final int HandlerCmd_Success = 88888 ;
    private static final int HandlerCmd_Failed = 88889 ;

    public  static  enum EventType{
        Follow,
        Black,
        ShoutUp,
        Helper,
    };


    private UserVo userVo;
    private String uid ;
    private boolean isAdd ; //添加或是删除  设置或者撤销
    private EventType event;
    private OnFunactionListener onFunactionListener ;
    private Context context ;
    public OnFunactionAddListnerUtil(Context context,boolean add,OnFunactionListener listener){
        onFunactionListener = listener;
        this.context = context ;
        this.event = event;
        this.userVo = userVo;
        this.uid = uid;
        this.isAdd = add ;
        doRequest();
    }

    public void doRequest(){



//            String id = GlobalData.getUID()
//            if(event == EventType.Follow){
//                if(isAdd){
//                    Util.addRpcEvent()
//                }else{
//
//                }
//            }
    }


    protected UIHandler handler = new UIHandler(Looper.getMainLooper(), new UIHandler.IHandler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HandlerCmd_Success:
                    onFunactionListener.onSuccess(userVo,uid,isAdd);
                    break ;
                case HandlerCmd_Failed:
                    onFunactionListener.onFailed(userVo,uid,isAdd);
                    break ;
            }
        }
    });

    public interface OnFunactionListener{
        public void onSuccess(UserVo userVo,String uid,boolean add) ;
        public void onFailed(UserVo userVo,String uid,boolean add);
    }

}
