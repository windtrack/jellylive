package com.oo58.jelly.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.util.RpcRequest;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.ToastManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhongxf
 * @Date 2016/6/22.
 */
public class ManagerDialog {

    private static final String [][] STR_MANAGER = {
            {"设管理员","撤销管理员",} ,
            {"管理员列表",} ,
            {"禁言","撤销禁言"},
            {"举报"},
    } ;


    public Context mContext;

    private Dialog dialog;//个人资料卡
    private LayoutInflater mLayoutInflater;//视图加载器
    private int SCREEN_WIDTH = 0;
    private int SCREEN_HEIGHT = 0;

    private Button button_setHelper; //设为管理员
    private Button button_helperList;//管理员列表
    private Button button_shoutup;//禁言
    private Button button_report;//举报




    private UserVo userVo ;
    private View main;

    private String roomid ;

    public ManagerDialog(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;

        Rect outRect = new Rect();
        ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        SCREEN_WIDTH = outRect.width() ;
        SCREEN_HEIGHT = outRect.height();

    }

    /**
     * 管理员 和 禁言的 状态更新
     * @param userVo
     */
    public void updateBt(UserVo userVo){
        button_setHelper.setText(userVo.isHelper()?STR_MANAGER[0][1]:STR_MANAGER[0][0]);
        button_shoutup.setText(userVo.isShoutUp()?STR_MANAGER[2][1]:STR_MANAGER[2][0]);
    }


    public void show(UserVo userVo,String roomid) {
        creatDialog(userVo,roomid);
    }


    private void creatDialog(UserVo uv,String roomid) {
        userVo  = uv;
        this.roomid = roomid ;
        if(main == null){
            main = mLayoutInflater.inflate(R.layout.manage_user, null);
            button_setHelper = (Button) main.findViewById(R.id.bt_sethelper) ;
            button_setHelper.setOnClickListener(listener);
            button_helperList = (Button) main.findViewById(R.id.bt_helplist) ;
            button_helperList.setOnClickListener(listener);
            button_shoutup = (Button) main.findViewById(R.id.bt_shoutup) ;
            button_shoutup.setOnClickListener(listener);
            button_report = (Button) main.findViewById(R.id.bt_report) ;
            button_report.setOnClickListener(listener);
        }


        updateBt(userVo) ;
        closeDialog();//调用关闭方法，防止多层显示
        if(dialog == null){
            dialog = new Dialog(mContext, R.style.loading_dialog);// 创建自定义样式dialog
            dialog.setCancelable(true);// 是否可以用返回键取消
            dialog.setCanceledOnTouchOutside(true);
            LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            fill_parent.width = SCREEN_WIDTH;
            fill_parent.height = SCREEN_HEIGHT;
            dialog.setContentView(main, fill_parent);// 设置布局
        }
        dialog.show();
    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case  R.id.bt_sethelper:
                    callSetHelper() ;
                    break ;
                case  R.id.bt_helplist:
                    showHelperList();
                    break ;
                case  R.id.bt_shoutup:
                    callShoutUp();
                    break ;
                case  R.id.bt_report:
                    callReport();
                    break ;
            }
        }
    };

    /**
     * 设置1/撤销2 管理员
     */
    private void callSetHelper(){
        new RpcRequest(mContext, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {
                userVo.setHelper(!userVo.isHelper());
                updateBt(userVo);
            }

            @Override
            public void onFailed(Message msg) {
                String result = (String )msg.obj;
                try {
                    JSONObject obj = new JSONObject(result);
                    String tips = obj.getString("data") ;
                    ToastManager.makeToast(mContext,tips);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Message msg) {

            }
        }).doRequest(RpcEvent.CallAddHelper, GlobalData.getUID(mContext),GlobalData.getUSecert(mContext),roomid,userVo.getUid(),userVo.isHelper()?0:1);
    }

    /**
     * 显示管理列表
     */
    private void showHelperList(){
        Intent intent = new Intent(AppAction.ACTION_MANAGERS_LIST);
        intent.putExtra("roomid",roomid) ;
        mContext.startActivity(intent);
    }

    /**
     * 设置1/取消2 禁言
     */
    private void callShoutUp(){
        new RpcRequest(mContext, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {
                userVo.setShoutUp(!userVo.isShoutUp());
                updateBt(userVo);
                ToastManager.makeToast(mContext,userVo.isShoutUp()?"用户被禁言":"用户被解禁");
            }

            @Override
            public void onFailed(Message msg) {
                String result = (String )msg.obj;
                try {
                    JSONObject obj = new JSONObject(result);
                    String tips = obj.getString("data") ;
                    ToastManager.makeToast(mContext,tips);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Message msg) {
                ToastManager.makeToast(mContext,userVo.isShoutUp()?"解禁失败":"禁言失败");
            }
        }).doRequest(RpcEvent.CallShutUp, GlobalData.getUID(mContext),GlobalData.getUSecert(mContext),roomid,userVo.getUid(),userVo.isShoutUp()?0:1);
    }

    /**
     * 举报
     */
    private void callReport(){
        new RpcRequest(mContext, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {
                ToastManager.makeToast(mContext,"举报成功");
            }

            @Override
            public void onFailed(Message msg) {
                ToastManager.makeToast(mContext,"举报失败");
            }

            @Override
            public void onError(Message msg) {
                ToastManager.makeToast(mContext,"举报失败");
            }
        }).doRequest(RpcEvent.CallReport, GlobalData.getUID(mContext),GlobalData.getUSecert(mContext),userVo.getUid());
    }


    /**
     * 关闭弹出框
     */
    public void closeDialog() {
        if (dialog != null)
            dialog.dismiss();
    }

}
