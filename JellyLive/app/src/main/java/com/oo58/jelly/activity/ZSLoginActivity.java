package com.oo58.jelly.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.manager.LoginManager;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.util.network.RequestCallBack;
import com.oo58.jelly.util.network.RequestUtil;
import com.oo58.jelly.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhongxf
 * @Description 中视号码登录
 * @Date 2016/6/13.
 */
public class ZSLoginActivity extends BaseActivity {
    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题
    private LinearLayout rootView;

    private Button loginBtn;//登录按钮

    private EditText textView_name;
    private EditText textView_password;

    private String userName;
    private String passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zs_login);
        init();
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case LoginManager.LoginHandler_Success:
                startActivity(new Intent(AppAction.ACTION_MAIN));
                finish();
                break;
            case LoginManager.LoginHandler_Failed:
                ToastManager.makeToast(mContext, "登录失败");
                break;
            case LoginManager.LoginHandler_Error:
                ToastManager.makeToast(mContext, "账号名或密码错误");
                break;
        }
    }

    //初始化界面
    private void init() {

        textView_name = (EditText) findViewById(R.id.usename);
        textView_password = (EditText) findViewById(R.id.password);

        if (SharedPreUtil.contains(mContext, AppContance.USER)) {
            textView_name.setText(SharedPreUtil.getString(mContext, AppContance.USER));
            textView_name.setSelection(textView_name.getText().toString().length());
        }
        if (SharedPreUtil.contains(mContext, AppContance.PASS)) {
//            textView_password.setText(SharedPreUtil.getString(mContext,AppContance.PASS));
//            textView_password.setSelection(textView_password.getText().toString().length());
        }

        textView_name.clearFocus();
        textView_password.clearFocus();

        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("中视账号登录");
        rootView = (LinearLayout) findViewById(R.id.root);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 800);

        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(listen);
    }

    //点击监听
    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_btn:
                    startActivity(new Intent(AppAction.ACTION_LOGIN));
                    finish();
                    break;
                case R.id.login_btn:
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
                    userName = textView_name.getText().toString();
                    passWord = textView_password.getText().toString();
                    if (Util.isEmpty(userName) || Util.isEmpty(passWord)) {
                        ToastManager.makeToast(mContext, "账户名/密码为空");
                        return;
                    }
                    doLogin(userName, passWord);

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                startActivity(new Intent(AppAction.ACTION_LOGIN));
                finish();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void doLogin(String name, String passwrod) {
        LoginManager.doLogin(mContext, LoginManager.LoginType.LoginType_ZS, name, passwrod, AppContance.phoneImeToken, handler);
//        LoadingDialog ld = new LoadingDialog();
//        RequestUtil ru = new RequestUtil();
//        ru.doRequest(mContext, RpcEvent.CallUserLogin, new RequestCallBack() {
//            @Override
//            public void doStart() {
//
//            }
//
//            @Override
//            public void doFinish() {
//
//            }
//
//            @Override
//            public void doSuccess(JSONObject resJson) {
//                Log.e("unfind", "登录返回：" + resJson.toString());
//            }
//
//            @Override
//            public void doFaild(int ERROR_CODE) {
//
//            }
//
//            @Override
//            public void doError(JSONObject resJson) {
//
//            }
//        }, name, passwrod, AppContance.phoneImeToken);
//
//        ru.doRequest(mContext, RpcEvent.GetHotList, new RequestCallBack() {
//            @Override
//            public void doStart() {
//
//            }
//
//            @Override
//            public void doFinish() {
//
//            }
//
//            @Override
//            public void doSuccess(JSONObject resJson) {
//                Log.e("unfind", "获取热门主播返回：" + resJson.toString());
//            }
//
//            @Override
//            public void doFaild(int ERROR_CODE) {
//
//            }
//
//            @Override
//            public void doError(JSONObject resJson) {
//
//            }
//        });


    }


    private void doLoginSuccess(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONObject usrInfo = obj.getJSONObject("data");
            JSONObject usrauth = obj.getJSONObject("auth");

            //保存登录数据


            String id = usrInfo.getString("id");
            String openkey = usrauth.getString("openkey");
            String timestamp = usrauth.getString("timestamp");
            String useropenid = usrauth.getString("openid");

            String icon = AppUrl.USER_LOGO_ROOT + usrInfo.getString("icon");
            String nickname = usrInfo.getString("nickname");
            String user_type = usrInfo.getString("user_type"); //主播 普通用户
            String phone = usrInfo.getString("phone");
            String secret = usrInfo.getString("secret");
//            String province = usrInfo.getString("province");
//            String city = usrInfo.getString("city");
            int gender = usrInfo.getInt("gender"); //性别  0 未知， 1 男，2 女
            String beans = usrInfo.getString("beans"); //
            int coins = usrInfo.getInt("coins"); //
            int getcoins = usrInfo.getInt("received_beans"); //
            int sendcoins = usrInfo.getInt("cost_beans"); //

            int viplevel = usrInfo.getInt("vip_lv"); //
            int costlevel = usrInfo.getInt("cost_level"); //
            int receivelevel = usrInfo.getInt("received_level"); //

            int heart = usrInfo.getInt("hearts"); //

            String address = usrInfo.getString("address");//位置

//            int exp = usrInfo.getInt("exp"); //
//            int follow = usrInfo.getInt("follow"); //
//            int fans = usrInfo.getInt("fans"); //
//            String sign = usrInfo.getString("sign"); //


            SharedPreUtil.put(mContext, AppContance.USER, userName); //用户名
            SharedPreUtil.put(mContext, AppContance.PASS, passWord);//密码
            SharedPreUtil.put(mContext, AppContance.USER_OPENKEY, openkey);
            SharedPreUtil.put(mContext, AppContance.TIMESTAMP, timestamp);


            SharedPreUtil.put(mContext, AppContance.OPEN_ID, id);
            SharedPreUtil.put(mContext, AppContance.USER_SECRET, secret);
            SharedPreUtil.put(mContext, AppContance.USER_ICON, icon);

            SharedPreUtil.put(mContext, AppContance.NICKNAME, nickname);
            SharedPreUtil.put(mContext, AppContance.USER_TYPE, user_type);
            SharedPreUtil.put(mContext, AppContance.USER_PHONE, phone);
            SharedPreUtil.put(mContext, AppContance.USER_PROVINCE, address);
//            SharedPreUtil.put(mContext, AppContance.USER_CITY, city);
            SharedPreUtil.put(mContext, AppContance.GENDER, gender);

            SharedPreUtil.put(mContext, AppContance.USER_BEANS, beans);
            SharedPreUtil.put(mContext, AppContance.USER_COINS, coins);
            SharedPreUtil.put(mContext, AppContance.USER_GET_COINS, getcoins);
            SharedPreUtil.put(mContext, AppContance.USER_SEND_COINS, sendcoins);
            SharedPreUtil.put(mContext, AppContance.USER_HEART, heart);

            SharedPreUtil.put(mContext, AppContance.USER_VIP_LEV, viplevel);
            SharedPreUtil.put(mContext, AppContance.USER_COST_LEV, costlevel);
            SharedPreUtil.put(mContext, AppContance.USER_RECEIVE_LEV, receivelevel);
//            SharedPreUtil.put(mContext, AppContance.USER_EXP, exp);
//            SharedPreUtil.put(mContext, AppContance.USER_FOLLOW, follow);
//            SharedPreUtil.put(mContext, AppContance.USER_FANS, fans);
//            SharedPreUtil.put(mContext, AppContance.ROOM_NOTICE, sign);


            //保存登录类型
//            SharedPreUtil.put(mContext, AppContance.LOGIN_TYPE, loginType);//保存登录类型
            SharedPreUtil.put(mContext, AppContance.USER_LOGIN, true);//设置为登录状态
            SharedPreUtil.put(mContext, AppContance.USER_AUTO_LOGIN, true);//设置下次自动登录


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
