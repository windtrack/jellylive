package com.oo58.jelly.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LoginManager;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.LoadingDialog;
import com.oo58.jelly.wxapi.WXEntryActivity;

/**
 * @author zhongxf
 * @Description 登录界面
 * @Date 2016/6/13.
 */
public class LoginActivity extends BaseActivity {

    private static final int M_Handler_CallLoginAgian = 10001 ;//重读登录


    private ImageButton phoneLogin;//电话登录的按钮
    private TextView zsLoginBtn;//中视账号登录的按钮
    private ImageButton sinaLogin;//微博登录
    private ImageButton qqLogin;//qq登录
    private ImageButton wxLogin;//微信登录

    private LoadingDialog loadingDialog ;

    private Receiver mReceiver;//广播消息接收

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();

        /**
         * 注册一个监听重复登录的广播
         */
        mReceiver = new Receiver() ;
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppAction.RECVIVE_LOGINAGIAN);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case LoginManager.LoginHandler_Success:
                loadingDialog.closeDialog();
                startActivity(new Intent(AppAction.ACTION_MAIN));
                finish();
                break ;
            case LoginManager.LoginHandler_Failed:
                loadingDialog.closeDialog();
                break ;
            case LoginManager.LoginHandler_Error:
                loadingDialog.closeDialog();
                break ;
        }
    }

    //初始化界面
    private void init() {



        IntentFilter filter = new IntentFilter(WXEntryActivity.action);
        registerReceiver(broadcastReceiver, filter);

        loadingDialog  = new LoadingDialog();

        phoneLogin = (ImageButton) findViewById(R.id.login_phone);
        phoneLogin.setOnClickListener(listen);

        zsLoginBtn = (TextView) findViewById(R.id.login_zhongshi);
        zsLoginBtn.setOnClickListener(listen);

        qqLogin = (ImageButton) findViewById(R.id.login_qq);
        qqLogin.setOnClickListener(listen);
        wxLogin = (ImageButton) findViewById(R.id.login_weixin);
        wxLogin.setOnClickListener(listen);



        tryToAutoLogin() ;
    }

    /**
     * 尝试自动登录
     */
    private void tryToAutoLogin(){
        String username = SharedPreUtil.getString(this,AppContance.USER);
        String password = SharedPreUtil.getString(this,AppContance.PASS);
        String loginType = (SharedPreUtil.getString(this,AppContance.LOGIN_TYPE));
        //尝试自动登录
        if(GlobalData.checkAutoLogin(this) && !Util.isEmpty(username) && !Util.isEmpty(password) && !Util.isEmpty(loginType)){
            LoginManager.doLogin(this,LoginManager.LoginType.LoginType_Auto,username,password,AppContance.phoneImeToken,handler);
        }
    }

    //点击监听
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_phone:
                    startActivity(new Intent(AppAction.ACTION_PHONE_LOGIN));
                    finish();
                    break;
                case R.id.login_zhongshi:
                    startActivity(new Intent(AppAction.ACTION_ZS_LOIGN));
                    finish();
                    break;
                case R.id.login_weibo:
                    loadingDialog.show(mContext,false);
                    LoginManager.callSinaLogin();
                    break;
                case R.id.login_weixin:
                    loadingDialog.show(mContext,false);
                    LoginManager.callWeixinlogin(mContext);
                    break;
                case R.id.login_qq:
                    loadingDialog.show(mContext,false);
                    LoginManager.callQQLogin(LoginActivity.this,handler);
                    break;
                default:
                    break;
            }
        }
    };

    private long currentBackPressedTime = 0;// 按下返回键的当前手机系统时间
    private static final int BACK_PRESSED_INTERVAL = 2000; // 两次按下返回键的在这个时间间隔内才会退出

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            // 退出
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());//Kill进程
            System.exit(0);
            super.onBackPressed();
        }
    }






    /**
     * 微信登录
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context arg0, Intent intent) {
            if(intent.getAction() == WXEntryActivity.action){
                String openid = intent.getExtras().getString("openid");
                String access_token = intent.getExtras().getString("access_token");
                LoginManager.doLogin(mContext, LoginManager.LoginType.LoginType_WX,openid,access_token,AppContance.phoneImeToken,handler);
            }
        };
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        if(mReceiver!=null){
            unregisterReceiver(mReceiver); //注销广播
        }
    }


    /**
     * 接收重读登录的广播
     */
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean flag = intent.getBooleanExtra("loginagain",false);
            if(flag){
                handler.sendEmptyMessage(M_Handler_CallLoginAgian) ;
            }
        }
    }
}
