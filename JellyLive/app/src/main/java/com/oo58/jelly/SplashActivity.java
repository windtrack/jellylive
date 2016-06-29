package com.oo58.jelly;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LoginManager;
import com.oo58.jelly.util.NetStateUtil;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;

/**
 * Jelly直播的启动界面
 */
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        //跳转到登录界面
        SharedPreUtil.put(this, AppContance.USER_LOGIN, false);
        tryAutoLogin();
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case LoginManager.LoginHandler_Success:
                Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AppAction.ACTION_MAIN));
                finish();
                break;
            case LoginManager.LoginHandler_Failed:
                break;
            case LoginManager.LoginHandler_Error:
                Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AppAction.ACTION_LOGIN));
                finish();
                break;
            case LoginManager.GO_LOGIN:
                startActivity(new Intent(AppAction.ACTION_LOGIN));
                finish();
                break;

        }
    }


    /**
     * 尝试自动登录
     */
    public void tryAutoLogin() {
        if(!NetStateUtil.checkNet(mContext)){
            ToastManager.makeToast(mContext,"网络异常");
            return ;
        }


        String username = SharedPreUtil.getString(this, AppContance.USER);
        String password = SharedPreUtil.getString(this, AppContance.PASS);
        String loginType = (SharedPreUtil.getString(this, AppContance.LOGIN_TYPE));
        //尝试自动登录
        if (GlobalData.checkAutoLogin(this) && !Util.isEmpty(username) && !Util.isEmpty(password) && !Util.isEmpty(loginType)) {
            LoginManager.doLogin(this, LoginManager.LoginType.LoginType_Auto, username, password, AppContance.phoneImeToken, handler);
        } else {
            handler.sendEmptyMessageDelayed(LoginManager.GO_LOGIN, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
