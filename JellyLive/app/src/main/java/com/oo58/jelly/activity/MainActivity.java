package com.oo58.jelly.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.fragment.HomeFragment;
import com.oo58.jelly.fragment.LivingFragment;
import com.oo58.jelly.fragment.PersonalFragment;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LoginManager;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.Util;


/**
 * @author zhongxf
 * @Description 主界面
 * @Date 2016/6/13.
 */
public class MainActivity extends FragmentActivity {

    private FrameLayout content;//放置fragment的容器

    private ImageButton homeBtn;//首页的按钮
    private ImageButton livingBtn;//直播按钮
    private ImageButton personalBtn;//个人中心按钮

    private HomeFragment homeFragment;//首页的Fragment的对象
    private LivingFragment livFragment;//直播的Fragment对象
    private PersonalFragment personalFragment;//个人中心的Fragment对象

    private int CHECK_INDEX = 0;//底部菜单正在选中的位置  0：主页   1:直播   2：个人中心

    private Context mContext ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this ;
        initView();
//        tryAutoLogin() ;//尝试自动登录
    }

    //初始化界面
    private void initView() {

        content = (FrameLayout) findViewById(R.id.content);
        homeBtn = (ImageButton) findViewById(R.id.main_home);
        homeBtn.setOnClickListener(listener);
        livingBtn = (ImageButton) findViewById(R.id.main_living);
        livingBtn.setOnClickListener(listener);
        personalBtn = (ImageButton) findViewById(R.id.main_personal);
        personalBtn.setOnClickListener(listener);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (homeFragment == null) {
            homeFragment = new HomeFragment();

        }
        ft.add(R.id.content, homeFragment);
        ft.commit();
    }

    //界面的点击监听
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_home:
                    if (CHECK_INDEX != 0) {
                        if (homeFragment == null) {
                            homeFragment = new HomeFragment();
                        }
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.content, homeFragment);
                        ft.commit();
                        CHECK_INDEX = 0;

                        homeBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.home_clicked));
                        personalBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.person_normal));

                    }

                    break;
                case R.id.main_personal:
                    if (CHECK_INDEX != 2) {
                        if (personalFragment == null) {
                            personalFragment = new PersonalFragment();
                        }
                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.content, personalFragment);
                        ft.commit();
                        CHECK_INDEX = 2;
                        homeBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.home_normal));
                        personalBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.personal_clicked));
                    }
                    break;
                case R.id.main_living:
//                    if (CHECK_INDEX != 1) {
//                        if (livFragment == null) {
//                            livFragment = new LivingFragment();
//                        }
//                        FragmentManager fm = getSupportFragmentManager();
//                        FragmentTransaction ft = fm.beginTransaction();
//                        ft.replace(R.id.content, livFragment);
//                        ft.commit();
//                        CHECK_INDEX = 1;
//                        homeBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.home_normal));
//                        personalBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.person_normal));
//                    }
                    startActivity(new Intent(AppAction.ACTION_LIVING));//跳转到直播界面
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
            finish();
            // 退出
            android.os.Process.killProcess(android.os.Process.myPid());//Kill进程
            System.exit(0);
            super.onBackPressed();
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LoginManager.LoginHandler_Success:
                    Toast.makeText(mContext,"登录成功",Toast.LENGTH_SHORT).show();
                    break ;
                case LoginManager.LoginHandler_Failed:
                case LoginManager.LoginHandler_Error:
                    Toast.makeText(mContext,"登录失败",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AppAction.ACTION_LOGIN));
                    finish();
                    break ;
            }
        }
    };

//    /**
//     * 尝试自动登录
//     */
//    public void tryAutoLogin(){
//
//        if(GlobalData.checkLogin(mContext)){
//            return ;
//        }
//
//        String username = SharedPreUtil.getString(this, AppContance.USER);
//        String password = SharedPreUtil.getString(this,AppContance.PASS);
//        String loginType = (SharedPreUtil.getString(this,AppContance.LOGIN_TYPE));
//        //尝试自动登录
//        if(GlobalData.checkAutoLogin(this) && !Util.isEmpty(username) && !Util.isEmpty(password) && !Util.isEmpty(loginType)){
//            LoginManager.doLogin(this, LoginManager.LoginType.LoginType_Auto,username,password,AppContance.phoneImeToken,handler);
//        }else{
//            startActivity(new Intent(AppAction.ACTION_LOGIN));
////            finish();
//        }
//    }


}
