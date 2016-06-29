package com.oo58.jelly.activity.personal;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
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
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/18.
 */
public class UpdateNickActivity extends BaseActivity {
    //修改昵称
    private static final int HandlerCmd_EditNameSuccess = 10001 ;
    private static final int HandlerCmd_EditNameFailed = 10002 ;



    private ImageButton returnBtn;//返回按钮
    private TextView title;//标题

    private EditText nickInput;//昵称的输入框
    private ImageButton clearBtn;//清除按钮
    private Button rightBtn;//保存按钮
    private LinearLayout rootView;//根View

    String editNickName ;//修改后的昵称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_nick);
        initView();
    }

    @Override
    protected void handler(Message msg) {
            switch (msg.what){
                case HandlerCmd_EditNameSuccess:
                    showToast("昵称保存成功");
                    SharedPreUtil.put(mContext,AppContance.NICKNAME,editNickName);
                    finish();
                    break ;
                case HandlerCmd_EditNameFailed:
                    showToast("昵称保存失败");
                    break ;
            }
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(linten);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("修改昵称");
        rightBtn = (Button) findViewById(R.id.right_btn);
        rightBtn.setText("保存");
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setOnClickListener(linten);

        nickInput = (EditText) findViewById(R.id.nick_input);
        clearBtn = (ImageButton) findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(linten);
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

        String name = SharedPreUtil.getString(mContext,AppContance.NICKNAME) ;
        if(!Util.isEmpty(name)){
            nickInput.setText(name);
            nickInput.setSelection(name.length());
        }


    }

    //点击监听
    private View.OnClickListener linten = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.return_btn://返回按钮
                    Util.hideInput(mContext,v);
                    finish();
                    break;
                case R.id.right_btn://保存昵称
                    Util.hideInput(mContext,v);
                    callEditNickName() ;
                    break;
                case R.id.clear_btn://清除内容
                    nickInput.setText("");
                    break;
                default:
                    break;
            }
        }
    };
    private void callEditNickName( ) {
        editNickName = nickInput.getText().toString();
        if(Util.isEmpty(editNickName)){
            showToast("输入一个名字");
            return ;
        }

        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);
                    String result = Util.addRpcEvent(RpcEvent.CallEditNickName, openid, secret, editNickName);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        handler.sendEmptyMessage(HandlerCmd_EditNameSuccess);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_EditNameFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_EditNameFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }



}
