package com.oo58.jelly.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.oo58.jelly.R;
import com.oo58.jelly.common.AppAction;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.entity.UserVo;
import com.oo58.jelly.util.RpcRequest;
import com.oo58.jelly.manager.GlobalData;
import com.oo58.jelly.manager.LocalImageManager;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;
import com.oo58.jelly.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhongxf
 * @Description 其他人的主页
 * @Date 2016/6/18.
 */
public class OtherUserInfoActivity extends BaseActivity {
    //获取信息
    private static final int HandlerCmd_GetUserInfo_Success = 10001;
    private static final int HandlerCmd_GetUserInfo_Failed = 10002;


    private LinearLayout linearLayoutGx;//贡献排行

    private CircleImageView img_head;
    private TextView textView_name; //名称
    private TextView textView_uid; //名称
    private ImageView imageView_gender; //性别
    private TextView TextView_gender; //性别
    private ImageView imageView_caifuLev;//财富等级
    private TextView textView_followNum;//关注数
    private TextView textView_fansNum;// 粉丝数
    private TextView textView_sign;//签名

    private CheckBox checkBoxFollow;//关注按钮
    private CheckBox checkBoxBack;//拉黑按钮

    private TextView textView_age;//年龄
    private TextView textView_pos;//家乡


    public DisplayImageOptions mOptions;//头像

    private UserVo userVo;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_info);

        Intent intent = getIntent();
        userID = intent.getStringExtra("uid");
        initView();
        getCurUserInfo();
    }


    private void initView() {
        img_head = (CircleImageView) findViewById(R.id.userhead);
        textView_name = (TextView) findViewById(R.id.name);
        imageView_gender = (ImageView) findViewById(R.id.sex_icon);
        imageView_caifuLev = (ImageView) findViewById(R.id.label_icon);
        textView_followNum = (TextView) findViewById(R.id.follownum);
        textView_fansNum = (TextView) findViewById(R.id.fansNum);
        textView_sign = (TextView) findViewById(R.id.usersign);
        TextView_gender = (TextView) findViewById(R.id.show_sex);

        textView_uid = (TextView) findViewById(R.id.show_id);

        textView_age = (TextView) findViewById(R.id.show_age);
        textView_pos = (TextView) findViewById(R.id.show_hometown);

        checkBoxFollow = (CheckBox) findViewById(R.id.follow_btn);
        checkBoxFollow.setOnClickListener(listener);
        checkBoxBack = (CheckBox) findViewById(R.id.lahei_btn);
        checkBoxBack.setOnClickListener(listener);

        linearLayoutGx = (LinearLayout) findViewById(R.id.rank_list_btn);
        linearLayoutGx.setOnClickListener(listener);

        mOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.mipmap.testpic)
                .showImageForEmptyUri(R.mipmap.testpic)
                .showImageOnFail(R.mipmap.testpic)
                .showImageOnLoading(R.mipmap.testpic).cacheOnDisc()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
    }

    private void loadData() {


        GlobalData.getmImageLoader(mContext).displayImage(userVo.getIcon(), img_head, mOptions);
        textView_uid.setText(userVo.getUid());

        textView_name.setText(userVo.getName());
        LocalImageManager.setGender(mContext, imageView_gender, userVo.getGender());
        LocalImageManager.setWealthLev(mContext, imageView_caifuLev, userVo.getViplev());
        textView_followNum.setText("关注" + String.valueOf(userVo.getFollowCount()));
        textView_fansNum.setText("粉丝:" + String.valueOf(userVo.getFansCount()));

        if(userVo.getGender()==0){
            TextView_gender.setText("秘密");
        }else if(userVo.getGender() == 1){
            TextView_gender.setText("男");
        }else{
            TextView_gender.setText("女");
        }

        textView_pos.setText(userVo.getProvice()+userVo.getCity());

        if (Util.isEmpty(userVo.getSign())) {
            textView_sign.setText("TA好像忘记写签名了");
        } else {
            textView_sign.setText(userVo.getSign());
        }

        textView_age.setText(userVo.getAge()+"岁");



        doFollowButtonChange() ;
        doBlackButtonChange();
    }


    /**
     * 获取我的个人数据
     */
    private void getCurUserInfo() {
        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    String openid = GlobalData.getUID(mContext);
                    String secret = GlobalData.getUSecert(mContext);

                    String result = Util.addRpcEvent(RpcEvent.GetUserInfo, openid, secret, userID, 1, 1, 1, 1);

                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONObject userInfo = obj.getJSONObject("data");

                        userVo = new UserVo();
                        userVo.setUid(userInfo.getString("id"));
                        userVo.setName(userInfo.getString("nickname"));
                        userVo.setIcon(AppUrl.USER_LOGO_ROOT+userInfo.getString("icon"));
                        userVo.setGender(userInfo.getInt("gender"));
                        userVo.setViplev(userInfo.getInt("vip_lv"));
                        userVo.setCostlev(userInfo.getInt("cost_level"));
                        userVo.setReceivelev(userInfo.getInt("received_level"));
                        userVo.setSign(userInfo.getString("sign"));
                        userVo.setCostBeans(userInfo.getInt("cost_beans"));
                        userVo.setReceivedBeans(userInfo.getInt("received_beans"));

                        userVo.setProvice(userInfo.getString("province"));
                        userVo.setCity(userInfo.getString("city"));


                        userVo.setFansCount(userInfo.getInt("fans_num"));
                        userVo.setFollowCount(userInfo.getInt("followed_num"));
                        userVo.setAge(userInfo.getString("age"));

                        userVo.setFollow(userInfo.getInt("is_followed")==0?false:true);
                        userVo.setBlacked(userInfo.getInt("is_blacked")==0?false:true);
                        userVo.setHelper(userInfo.getInt("is_helper")==1?true:false);
                        userVo.setShoutUp(userInfo.getInt("is_shutuped")==1?true:false);

                        handler.sendEmptyMessage(HandlerCmd_GetUserInfo_Success);
                    } else {
                        handler.sendEmptyMessage(HandlerCmd_GetUserInfo_Failed);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerCmd_GetUserInfo_Failed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };

        ThreadPoolWrap.getThreadPool().executeTask(runnable);

    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rank_list_btn:
                    Intent intent = new Intent(AppAction.ACTION_RANK_LIST);
                    intent.putExtra("uid", userVo.getUid());
                    startActivity(intent);
                    break;
                case R.id.follow_btn:
                    callFollow();
                    break;
                case R.id.lahei_btn:
                    callAddBlack() ;
                    break;
                case R.id.sixin_btn:
                    callPrivateMessage() ;
                    break;

            }
        }
    };

    /**
     * 私信
     */
    private void callPrivateMessage() {
        if (userVo.getUid().equals(GlobalData.getUID(mContext))) {
            ToastManager.makeToast(mContext, "不能私信自己");
            return;
        }
    }

    /**
     * 关注按钮的改变
     */
    private void doFollowButtonChange(){
        checkBoxFollow.setChecked(userVo.isFollow());
    }

    /**
     * 拉黑按钮的改变
     */
    private void doBlackButtonChange(){
        checkBoxBack.setChecked(userVo.isBlacked());
    }

    /**
     * 关注
     */
    private void callFollow() {

        if(userVo.getUid().equals(GlobalData.getUID(mContext))){
            ToastManager.makeToast(mContext,"不能关注自己");
            doFollowButtonChange() ;
            return;
        }
        RpcRequest.OnRpcRequestListener listener= new RpcRequest.OnRpcRequestListener(){
            @Override
            public void onSuccess(Message msg) {
                userVo.setFollow(!userVo.isFollow());
                doFollowButtonChange();
                ToastManager.makeToast(mContext,userVo.isFollow()?"关注成功":"取消关注");
            }
            @Override
            public void onFailed(Message msg) {
                doFollowButtonChange();
                String reslut = (String) msg.obj;
                try {
                    JSONObject obj = new JSONObject(reslut);
                    String tips =   obj.getString("data") ;
                    ToastManager.makeToast(mContext,tips);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onError(Message msg) {
            }
        };
        if(userVo.isFollow()){
            new RpcRequest(mContext,listener).doRequest(RpcEvent.CallCancelFollowAnchor,GlobalData.getUID(mContext),GlobalData.getUSecert(mContext), userVo.getUid());
        }else{
            new RpcRequest(mContext,listener).doRequest(RpcEvent.CallFollowAnchor,GlobalData.getUID(mContext),GlobalData.getUSecert(mContext),userVo.getUid());
        }


    }

    /**
     * 添加黑名单
     */
    public  void callAddBlack() {

        if (userVo.getUid().equals(GlobalData.getUID(mContext))) {
            ToastManager.makeToast(mContext, "不能拉黑自己");
            doBlackButtonChange();
            return;
        }
        String uid = GlobalData.getUID(mContext);
        String secerts = GlobalData.getUSecert(mContext);

        RpcRequest.OnRpcRequestListener listener = new RpcRequest.OnRpcRequestListener() {
            @Override
            public void onSuccess(Message msg) {
                userVo.setBlacked(!userVo.isBlacked());
                doBlackButtonChange() ;
                ToastManager.makeToast(mContext, userVo.isBlacked()?"已拉黑":"解除拉黑");
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
                doBlackButtonChange() ;
            }
            @Override
            public void onError(Message msg) {
            }
        };

        if (userVo.isBlacked()) {
            new RpcRequest(mContext, listener).doRequest(RpcEvent.CallRemoveBlackList, uid, secerts, userVo.getUid());
        } else {
            new RpcRequest(mContext, listener).doRequest(RpcEvent.CallAddBlackList, uid, secerts, userVo.getUid());
        }

    }


    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            //玩家信息
            case HandlerCmd_GetUserInfo_Success:
                loadData();
                break;
            case HandlerCmd_GetUserInfo_Failed:
                ToastManager.makeToast(mContext, "信息获取失败");
                break;

        }
    }
}
