package com.oo58.jelly.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.manager.LoginManager;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhongxf
 * @Description 手机号码登录
 * @Date 2016/6/13.
 */
public class PhoneLoginActivity extends BaseActivity {
    private static final int HandlerCmd_GetCodeSuccess = 1 ;
    private static final int HandlerCmd_GetCodeFailed = 2 ;



    private LinearLayout rootView;//根View
    private static int SEND_CODE_TIME = 60;//发送验证码的时间间隔
    private final static int SEND_CODE_FLAG = 1001;//handler的发送消息
    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题

    private EditText codeInput;//验证码的输入框
    private Button sendCode;//获取验证码
    private Button loginBtn;//登录按钮
    private EditText phoneInput;//电话的输入框
    private LoadingDialog loadingDialog ;


    private String userPhoneNum;//手机号码
    private String checkCode;//验证码


    //出现键盘
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_CODE_FLAG:
                    SEND_CODE_TIME--;
                    if (SEND_CODE_TIME > 0) {
                        sendCode.setText(SEND_CODE_TIME + "秒后重新获取");
                        handler.sendEmptyMessageDelayed(SEND_CODE_FLAG, 1000);
                        sendCode.setEnabled(false);
                        sendCode.setClickable(false);
                        sendCode.setBackgroundResource(R.drawable.gray_coner_btn);
                    } else {
                        sendCode.setText(getResources().getString(R.string.check));
                        sendCode.setEnabled(true);
                        sendCode.setClickable(true);
                        sendCode.setBackgroundResource(R.drawable.yellow_coner);
                        SEND_CODE_TIME = 60;
                    }
                    break;
                case LoginManager.LoginHandler_Success:
                    loadingDialog.closeDialog();
                    startActivity(new Intent(AppAction.ACTION_MAIN));
                    finish();
                    break ;
                case LoginManager.LoginHandler_Failed:
                    loadingDialog.closeDialog();
                    Toast.makeText(mContext,"登录失败",Toast.LENGTH_SHORT).show();
                    break ;
                case LoginManager.LoginHandler_Error:
                    loadingDialog.closeDialog();
                    Toast.makeText(mContext,"登录失败",Toast.LENGTH_SHORT).show();
                    break ;
                default:
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_login);
        init();
    }

    @Override
    protected void handler(Message msg) {

    }

    //初始化界面
    private void init() {
        loadingDialog = new LoadingDialog() ;


        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("手机登录");
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

        codeInput = (EditText) findViewById(R.id.code_input);
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String code = codeInput.getText().toString();
                if (code != null && code.length() == 6 && !"".equals(code)) {
                    loginBtn.setBackgroundResource(R.drawable.yellowbtn);
                    loginBtn.setClickable(true);
                    loginBtn.setEnabled(true);
                } else {
                    loginBtn.setBackgroundResource(R.drawable.gray_cirlce_btn);
                    loginBtn.setClickable(false);
                    loginBtn.setEnabled(false);
                }
            }
        });
        sendCode = (Button) findViewById(R.id.get_code);
        sendCode.setOnClickListener(listen);
        loginBtn = (Button) findViewById(R.id.login_btn);
        phoneInput = (EditText) findViewById(R.id.phone_input);
        String code = codeInput.getText().toString();
        if (code == null || "".equals(code)) {
            loginBtn.setBackgroundResource(R.drawable.gray_cirlce_btn);
            loginBtn.setClickable(false);
            loginBtn.setEnabled(false);
        } else {
            loginBtn.setBackgroundResource(R.drawable.yellowbtn);
            loginBtn.setClickable(true);
            loginBtn.setEnabled(true);
        }
        loginBtn.setOnClickListener(listen);


        //自动输入手机号
        if(SharedPreUtil.contains(mContext,AppContance.USER_PHONE)){
            phoneInput.setText(SharedPreUtil.getString(mContext,AppContance.USER_PHONE));
        }

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
                case R.id.get_code:
                    sendCode();
                    break;
                case R.id.login_btn:
                    callLogin() ;
                    break;
                default:
                    break;
            }
        }
    };

    //发送验证码的方法
    private void sendCode() {
        if(checkPhoneNum()){
            userPhoneNum = phoneInput.getText().toString();;
            getCode(userPhoneNum) ;
            handler.sendEmptyMessage(SEND_CODE_FLAG);
        }

    }

    /**
     * 检测手机号码
     * @return
     */
    private boolean checkPhoneNum(){
        String phoneNum = phoneInput.getText().toString();
        if (phoneNum == null || "".equals(phoneNum)) {
            Toast.makeText(PhoneLoginActivity.this, "请输入手机号码", Toast.LENGTH_LONG).show();
            return false;
        }
        if(phoneNum.length() != 11){
            Toast.makeText(PhoneLoginActivity.this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
            return false;
        }
        return true ;
    }

    /**
     * 检测验证码
     */
    private boolean checkCode(){
        String phoneNum = codeInput.getText().toString();
        if (phoneNum == null || "".equals(phoneNum)) {
            Toast.makeText(PhoneLoginActivity.this, "请输入验证码", Toast.LENGTH_LONG).show();
            return false;
        }
        return true ;
    }

    /**
     * 输入验证码后登录
     */
    private void callLogin(){

        //检测是否输入
        if(!checkPhoneNum()){
            return ;
        }
        if(!checkCode()){
            return ;
        }

        userPhoneNum = phoneInput.getText().toString();
        checkCode = codeInput.getText().toString() ;
        loadingDialog.show(mContext,false);
        //请求登录
        LoginManager.doLogin(mContext,LoginManager.LoginType.LoginType_Code,userPhoneNum,checkCode,AppContance.phoneImeToken,handler);
    }


    /**
     * 获取验证码
     */
    private void getCode(final String phoneNum){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String result  = Util.addRpcEvent(RpcEvent.GetLoginCode,phoneNum);

                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        //保存登录数据
                        String data = obj.getString("data") ;

                        handler.sendEmptyMessage(HandlerCmd_GetCodeSuccess) ;
                    }else{
                        handler.sendEmptyMessage(HandlerCmd_GetCodeFailed) ;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetCodeFailed) ;
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }



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
}
