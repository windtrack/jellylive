package com.oo58.jelly.activity.personal;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.Util;

import org.json.JSONException;
import org.json.JSONObject;import java.util.Timer;
import java.util.TimerTask;

/**
 * @author zhongxf
 * @Description 编辑签名安妮
 * @Date 2016/6/16.
 */
public class EditSignedActivity extends BaseActivity {

    private static final int HandlerCmd_EditSuccess = 10001;
    private static final int HandlerCmd_EditFailed = 10002;
    private static final int HandlerCmd_EditError = 10003;


    private ImageButton returnBtn;//返回按钮
    private TextView title;//顶部标题
    private Button rightBtn;//右边的按钮

    private EditText content;//内容输入
    private TextView num;//字数
    private int maxWordsNum = 32;// 最大允许输入数
    private CharSequence temp;// 监听前的文本
    private LinearLayout rootView;//根View
    private String strSign;//签名
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_singed);
        initView();
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what){
            case HandlerCmd_EditSuccess:
                showToast("设置成功");
                SharedPreUtil.put(mContext, AppContance.USER_SIGN,strSign);
                finish();
                break ;
            case HandlerCmd_EditFailed:
                showToast("设置失败");
                break ;
            case HandlerCmd_EditError:
                showToast("设置失败");
                break ;
        }
    }

    //初始化View
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("修改签名");

        rightBtn = (Button) findViewById(R.id.right_btn);
        rightBtn.setText("保存");
        rightBtn.setOnClickListener(listen);
        rightBtn.setVisibility(View.VISIBLE);

        num = (TextView) findViewById(R.id.show_words_num);
        num.setText("0/32");//从前一个页面带签名过来的时候，需要设置这个值
        content = (EditText) findViewById(R.id.sign_content);
        content.addTextChangedListener(wordsWatch);
        String user_sign = SharedPreUtil.getString(mContext, AppContance.USER_SIGN) ;
        if(!Util.isEmpty(user_sign)){
            content.setText(user_sign);
            num.setText(user_sign.length() + "/" + maxWordsNum);
            content.setSelection(content.getText().length());
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 800);
        rootView = (LinearLayout) findViewById(R.id.root);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
            }
        });
    }

    // 寄语输入框的监听
    private TextWatcher wordsWatch = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            temp = arg0;
            num.setText(arg0.length() + "/" + maxWordsNum);
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            /** 得到光标开始和结束位置 ,超过最大数后记录刚超出的数字索引进行控制 */
            if (temp.length() > maxWordsNum) {// 输入的内容超出了允许的长度
                s.delete(maxWordsNum, temp.length());
                int tempSelection = maxWordsNum;
                content.setText(s);
                content.setSelection(tempSelection);
            }
        }
    };


    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0); //强制隐藏键盘
            switch (v.getId()) {
                case R.id.return_btn:
                    hideInput(content) ;
                    finish();
                    break;
                case R.id.right_btn:
                    //保存
                    doSaveSign() ;
                    hideInput(content) ;
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 保存签名
     */
    private void doSaveSign( ) {
        strSign = content.getText().toString() ;
        if(Util.isEmpty(strSign)){
            showToast("请输入签名");
            return ;
        }


        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(EditSignedActivity.this);
                    String secret = GlobalData.getUSecert(EditSignedActivity.this);
                    String result = Util.addRpcEvent(RpcEvent.CallSetSign, openid, secret, strSign);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        handler.sendEmptyMessage(HandlerCmd_EditSuccess);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_EditFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_EditError);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    private  void hideInput(View v){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

}
