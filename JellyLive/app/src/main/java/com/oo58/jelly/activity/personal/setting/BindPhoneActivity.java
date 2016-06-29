package com.oo58.jelly.activity.personal.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.RpcRequest;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhongxf
 * @Description 绑定手机的activity
 * @Date 2016/6/21.
 */
public class BindPhoneActivity extends BaseActivity {

    private LinearLayout rootView;//根View
    private static int SEND_CODE_TIME = 60;//发送验证码的时间间隔
    private final static int SEND_CODE_FLAG = 1001;//handler的发送消息
    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题

    private EditText codeInput;//验证码的输入框
    private Button sendCode;//获取验证码
    private Button bindBtn;//登录按钮
    private EditText phoneInput;//电话的输入框

    private String phoneNum; //手机号
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
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_phone);
        initViews();
    }

    private void initViews() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("绑定手机");
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
                    bindBtn.setBackgroundResource(R.drawable.yellowbtn);
                    bindBtn.setClickable(true);
                    bindBtn.setEnabled(true);
                } else {
                    bindBtn.setBackgroundResource(R.drawable.gray_cirlce_btn);
                    bindBtn.setClickable(false);
                    bindBtn.setEnabled(false);
                }
            }
        });
        sendCode = (Button) findViewById(R.id.get_code);
        sendCode.setOnClickListener(listen);
        bindBtn = (Button) findViewById(R.id.bind_btn);
        phoneInput = (EditText) findViewById(R.id.phone_input);
        String code = codeInput.getText().toString();
        if (code == null || "".equals(code)) {
            bindBtn.setBackgroundResource(R.drawable.gray_cirlce_btn);
            bindBtn.setClickable(false);
            bindBtn.setEnabled(false);
        } else {
            bindBtn.setBackgroundResource(R.drawable.yellowbtn);
            bindBtn.setClickable(true);
            bindBtn.setEnabled(true);
        }
        bindBtn.setOnClickListener(listen);
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
                    handler.sendEmptyMessage(SEND_CODE_FLAG);
//                    callBlindPhoe() ;
                    getCode();
                    break;
                case R.id.bind_btn:
                    callBlindPhoe() ;
//                    Intent intnet = new Intent();
//                    intnet.putExtra("phone", phoneInput.getText().toString());
//                    setResult(RESULT_OK, intnet);
//                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void handler(Message msg) {

    }

    /**
     * 获取短信验证码
     */
    private  void getCode(){
        phoneNum = phoneInput.getText().toString() ;
        if(Util.isEmpty(phoneNum) && phoneNum.length()!=11){
            ToastManager.makeToast(mContext,"请输入正确的手机号");
            return ;
        }

        new RpcRequest(mContext, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {

            }

            @Override
            public void onFailed(Message msg) {

            }

            @Override
            public void onError(Message msg) {

            }
        }).doRequest(RpcEvent.GetLoginCode,phoneNum );

    }
    private  void callBlindPhoe(){


        phoneNum = phoneInput.getText().toString() ;
        if(Util.isEmpty(phoneNum) && phoneNum.length()!=11){
            ToastManager.makeToast(mContext,"请输入正确的手机号");
            return ;
        }
        checkCode = codeInput.getText().toString() ;

        if(Util.isEmpty(checkCode) && checkCode.length()!=6){
            ToastManager.makeToast(mContext,"请输入正确的验证码");
            return ;
        }

        new RpcRequest(mContext, new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {
                SharedPreUtil.put(mContext, AppContance.USER_PHONE,phoneNum);
                ToastManager.makeToast(mContext,"绑定成功");
                finish();
            }

            @Override
            public void onFailed(Message msg) {
                String reslut = (String) msg.obj;
                try {
                    JSONObject obj = new JSONObject(reslut);
                    String tips =  obj.getString("data") ;
                    ToastManager.makeToast(mContext,tips);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Message msg) {

            }
        }).doRequest(RpcEvent.CallBlindPhone,GlobalData.getUID(mContext),GlobalData.getUSecert(mContext),phoneNum,checkCode );

    }





}
