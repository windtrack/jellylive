package com.oo58.jelly.activity.personal.setting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oo58.jelly.R;
import com.oo58.jelly.activity.BaseActivity;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.Util;

/**
 * @author zhongxf
 * @Description 账号与安全界面
 * @Date 2016/6/21.
 */
public class SecurityActivity extends BaseActivity {

    private ImageView secLabelIcon;//安全等级标志
    private TextView showPhoneNum;//显示绑电话的按钮
    private ImageView icon;//可点击标志

    private LinearLayout bindBtn;//手机绑定的按钮

    private ImageButton returnBtn;//返回按钮
    private TextView title;//顶部标题

    //是否绑定手机的标志
    private boolean isBindPhone = false;

    //安全等级高的图片样式
    private Bitmap highLight;
    //安全等级低的图片样式
    private Bitmap low;
    //绑定的手机号码
    private String phoneNum = "13739284895";
    //跳转到绑定手机界面的请求码
    private final static int REQEUST_CODE = 888;


    private TextView textView_safe ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security);
        initView();
    }

    //初始化界面
    private void initView() {
        returnBtn = (ImageButton) findViewById(R.id.return_btn);
        returnBtn.setOnClickListener(listen);
        title = (TextView) findViewById(R.id.top_title);
        title.setText("账号与安全");

        secLabelIcon = (ImageView) findViewById(R.id.sec_flag_icon);
        showPhoneNum = (TextView) findViewById(R.id.show_phone_num);
        icon = (ImageView) findViewById(R.id.show_bind_icon);
        bindBtn = (LinearLayout) findViewById(R.id.bind_phone_btn);
        bindBtn.setOnClickListener(listen);
        highLight = BitmapFactory.decodeResource(getResources(), R.mipmap.account_sec_hight);
        low = BitmapFactory.decodeResource(getResources(), R.mipmap.account_sec_gray);

        textView_safe = (TextView) findViewById(R.id.safe);

        checkBlind() ;

    }

    /**
     * 检测是否绑定
     */
    private void checkBlind() {

        phoneNum = SharedPreUtil.getString(mContext, AppContance.USER_PHONE);
        if(Util.isEmpty(phoneNum)){
            isBindPhone = false ;
        }else{
            isBindPhone = true ;
        }

        if (isBindPhone) {//已经绑定手机了
            textView_safe.setText("安全等级:高");
            secLabelIcon.setImageBitmap(highLight);
            icon.setVisibility(View.GONE);
            showPhoneNum.setVisibility(View.VISIBLE);
            showPhoneNum.setText(phoneNum);
        } else {
            textView_safe.setText("安全等级:低");
            secLabelIcon.setImageBitmap(low);
            icon.setVisibility(View.VISIBLE);
            showPhoneNum.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQEUST_CODE && data != null) {


//            secLabelIcon.setImageBitmap(low);
//            icon.setVisibility(View.GONE);
//            showPhoneNum.setVisibility(View.VISIBLE);
//            showPhoneNum.setText(phoneNum);
//            isBindPhone = true;
        }
    }

    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.return_btn:
                    finish();
                    break;
                case R.id.bind_phone_btn:
                    if (isBindPhone) {//如果已经绑定了那么点击就不跳转了
                        return;
                    }
                    startActivityForResult(new Intent(AppAction.ACTION_BINDPHONE), REQEUST_CODE);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void handler(Message msg) {

    }


    @Override
    protected void onResume() {
        super.onResume();

        checkBlind();

    }
}
