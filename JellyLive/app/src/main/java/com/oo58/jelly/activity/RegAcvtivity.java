package com.oo58.jelly.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;

import com.oo58.jelly.R;

/**
 * @author zhongxf
 * @Description 注册用户界面
 * @Date 2016/6/13.
 */
public class RegAcvtivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg);
    }

    @Override
    protected void handler(Message msg) {

    }
}
