package com.oo58.jelly.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.common.AppAction;

/**
 * @author zhongxf
 * @Description 我的收益
 * @Date 2016/6/16.
 */
public class MyProfitActivity extends BaseActivity {

    private ImageButton returnBtn;
    private TextView topTitle;
    private Button rightBtn;


    private TextView textView_beans;
    private TextView textView_redpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profit);
        initView();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        topTitle = (TextView) findViewById(R.id.top_title);
        topTitle.setText("我的收益");
        rightBtn = (Button) findViewById(R.id.right_btn);
        rightBtn.setOnClickListener(listen);
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("领取记录");


        textView_beans = (TextView)findViewById(R.id.allcount) ;
        textView_redpaper = (TextView)findViewById(R.id.allhongbao) ;
    }

    @Override
    protected void handler(Message msg) {

    }

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_btn:
                    finish();
                    break;
                case R.id.right_btn:
                    startActivity(new Intent(AppAction.ACTION_PROFIT_LIST));
                    break;
                default:
                    break;
            }
        }
    };


}
