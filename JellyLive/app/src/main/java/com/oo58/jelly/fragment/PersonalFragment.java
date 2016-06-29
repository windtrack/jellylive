package com.oo58.jelly.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageButton;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LocalImageManager;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.UIHandler;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

/**
 * @author zhongxf
 * @Description 个人中心的Fragment
 * @Date 2016/6/14.
 */
public class PersonalFragment extends Fragment {
    private static final int HandlerCmd_GetUserInfo_Success = 10001;
    private static final int HandlerCmd_GetUserInfo_Failed = 10002;
    private static final int HandlerCmd_GetUserInfo_Error = 10003;

    private RelativeLayout myAccountBtn;//我的账户界面
    private ImageButton searchBtn;//搜索按钮
    private LinearLayout settingBtn;//设置按钮
    private RelativeLayout labelBtn;//我的等级按钮

    private CircleImageView img_head;
    private TextView textView_name; //名称
    private ImageView imageView_gender; //性别
    private ImageView imageView_caifuLev;//财富等级
    private TextView textView_uid;//uid
    private TextView textView_followNum;//关注数
    private View textView_space;// 中间分隔
    private TextView textView_fansNum;// 粉丝数
    private TextView textView_sign;//签名
    private TextView textView_accountMoney;//账户
    private TextView textView_lev;//等级


    public DisplayImageOptions mOptions;//头像
    private View rootView;

    /**
     * 优先载入本地
     */
    private void loadUserInfo() {
        Context context = getActivity();
        String user_headUrl = SharedPreUtil.getString(context, AppContance.USER_ICON);
        String user_name = SharedPreUtil.getString(context, AppContance.NICKNAME);
        int user_sex = SharedPreUtil.getInt(context, AppContance.GENDER, 0);
        String user_id = SharedPreUtil.getString(context, AppContance.OPEN_ID);
        int user_followNum = SharedPreUtil.getInt(context, AppContance.USER_FOLLOW, 0);
        int user_fansNum = SharedPreUtil.getInt(context, AppContance.USER_FANS, 0);
        String user_sign = SharedPreUtil.getString(context, AppContance.USER_SIGN);
        String user_accountMoney = SharedPreUtil.getString(context, AppContance.USER_BEANS);
        int user_inComeMoney = SharedPreUtil.getInt(context, AppContance.USER_COINS, 0);
        int user_Lev = SharedPreUtil.getInt(context, AppContance.USER_VIP_LEV, 0);


        GlobalData.getmImageLoader(getActivity()).displayImage(user_headUrl, img_head, mOptions);


        textView_name.setText(user_name);
        LocalImageManager.setGender(getActivity(), imageView_gender, user_sex);
        LocalImageManager.setWealthLev(getActivity(), imageView_caifuLev, user_Lev);
        textView_uid.setText("果冻号：" + user_id);
        textView_followNum.setText("关注" + String.valueOf(user_followNum));
        textView_fansNum.setText("粉丝:" + String.valueOf(user_fansNum));

        if (Util.isEmpty(user_sign)) {
            textView_sign.setText("TA好像忘记写签名了");
        } else {
            textView_sign.setText(user_sign);
        }

        textView_accountMoney.setText(String.valueOf(user_accountMoney));
        textView_lev.setText(user_Lev + "");


        if(GlobalData.isAnchor(context)){
            textView_space.setVisibility(View.VISIBLE);
            textView_fansNum.setVisibility(View.VISIBLE);
        }else{
            textView_space.setVisibility(View.GONE);
            textView_fansNum.setVisibility(View.GONE);
        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if (rootView == null) {
            rootView = inflater.inflate(R.layout.main_personal, null);
            img_head = (CircleImageView) rootView.findViewById(R.id.userhead);
            img_head.setOnClickListener(listen);
            textView_name = (TextView) rootView.findViewById(R.id.name);
            imageView_gender = (ImageView) rootView.findViewById(R.id.sex_icon);
            imageView_caifuLev = (ImageView) rootView.findViewById(R.id.label_icon);
            textView_uid = (TextView) rootView.findViewById(R.id.userid);
            textView_followNum = (TextView) rootView.findViewById(R.id.follownum);
            textView_followNum.setOnClickListener(listen);
            textView_fansNum = (TextView) rootView.findViewById(R.id.fansNum);
            textView_fansNum.setOnClickListener(listen);


            textView_space = (View) rootView.findViewById(R.id.space);

            textView_sign = (TextView) rootView.findViewById(R.id.usersign);
            textView_sign.setOnClickListener(listen);
            textView_accountMoney = (TextView) rootView.findViewById(R.id.useraccount);
            textView_lev = (TextView) rootView.findViewById(R.id.userlev);


            myAccountBtn = (RelativeLayout) rootView.findViewById(R.id.my_account);
            myAccountBtn.setOnClickListener(listen);
            searchBtn = (ImageButton) rootView.findViewById(R.id.search_btn);
            searchBtn.setOnClickListener(listen);

            mOptions = new DisplayImageOptions.Builder()
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showStubImage(R.mipmap.testpic)
                    .showImageForEmptyUri(R.mipmap.testpic)
                    .showImageOnFail(R.mipmap.testpic)
                    .showImageOnLoading(R.mipmap.testpic).cacheOnDisc()
                    .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
            settingBtn = (LinearLayout) rootView.findViewById(R.id.setting);
            settingBtn.setOnClickListener(listen);
            labelBtn = (RelativeLayout) rootView.findViewById(R.id.my_label_btn);
            labelBtn.setOnClickListener(listen);
            loadUserInfo();
        }
//        getSelfUserInfo();
        return rootView;
    }


    private UIHandler handler = new UIHandler(Looper.getMainLooper(), new UIHandler.IHandler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case HandlerCmd_GetUserInfo_Success:
                    loadUserInfo();
                    break;
                case HandlerCmd_GetUserInfo_Failed:
                    break;
                case HandlerCmd_GetUserInfo_Error:
                    break;
            }
        }
    });


    /**
     * 获取我的个人数据
     */
    private void getSelfUserInfo() {
        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    String openid = GlobalData.getUID(getActivity());
                    String secret = GlobalData.getUSecert(getActivity());

                    String result = Util.addRpcEvent(RpcEvent.GetUserInfo, openid, secret, openid, 1, 1, 1, 1);

                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {

                        JSONObject userInfo = obj.getJSONObject("data");

                        Context context = getActivity();
                        SharedPreUtil.put(context, AppContance.USER_ICON, AppUrl.USER_LOGO_ROOT + userInfo.getString("icon"));
                        SharedPreUtil.put(context, AppContance.NICKNAME, userInfo.getString("nickname"));
                        SharedPreUtil.put(context, AppContance.GENDER, userInfo.getInt("gender"));
                        SharedPreUtil.put(context, AppContance.OPEN_ID, userInfo.getString("id"));
                        SharedPreUtil.put(context, AppContance.USER_FOLLOW, userInfo.getInt("follow"));
                        SharedPreUtil.put(context, AppContance.USER_FANS, userInfo.getInt("fans"));
                        SharedPreUtil.put(context, AppContance.USER_SIGN, userInfo.getString("sign"));
                        SharedPreUtil.put(context, AppContance.USER_BEANS, userInfo.getString("beans"));
                        SharedPreUtil.put(context, AppContance.USER_COINS, userInfo.getInt("coins"));
                        SharedPreUtil.put(context, AppContance.USER_VIP_LEV, userInfo.getInt("level"));

                        handler.sendEmptyMessage(HandlerCmd_GetUserInfo_Success);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetUserInfo_Failed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetUserInfo_Error);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };

        ThreadPoolWrap.getThreadPool().executeTask(runnable);

    }


    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //设置签名
        handler.resume();

        if (getActivity() != null) {
            loadUserInfo();
        }
    }


    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup localViewGroup = (ViewGroup) this.rootView.getParent();
        if (localViewGroup != null)
            localViewGroup.removeView(this.rootView);
    }


    private View.OnClickListener listen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.my_account://我的账户
                    startActivity(new Intent(AppAction.ACTION_MY_ACCOUNT));
                    break;

                case R.id.rank_list_btn: {
                    Intent intent = new Intent(AppAction.ACTION_RANK_LIST);
                    intent.putExtra("uid", GlobalData.getUID(getContext()));
                    startActivity(intent);
                }
                break;
                case R.id.search_btn://搜索按钮
                    startActivity(new Intent(AppAction.ACTION_SEARCH));
                    break;

                case R.id.usersign://签名
                    startActivity(new Intent(AppAction.ACTION_EDIT_SIGN));
                    break;

                case R.id.userhead://点击头像
                    startActivity(new Intent(AppAction.ACTION_USER_INFO));
                    break;

                case R.id.setting://设置
                    startActivity(new Intent(AppAction.ACTION_SETTING));
                    break;
                case R.id.fansNum:
                    startActivity(new Intent(AppAction.ACTION_MYFANS));
                    break;
                case R.id.follownum:
                    startActivity(new Intent(AppAction.ACTION_MYFOLLOW));
                    break;
                case R.id.my_label_btn://我的等级
                    startActivity(new Intent(AppAction.ACTION_MY_LABEL));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        handler.pause();
    }


}
