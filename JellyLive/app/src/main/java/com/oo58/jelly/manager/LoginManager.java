package com.oo58.jelly.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.common.AppUrl;
import com.oo58.jelly.common.CommonKey;
import com.oo58.jelly.common.RpcEvent;
import com.oo58.jelly.util.NetStateUtil;
import com.oo58.jelly.util.SharedPreUtil;
import com.oo58.jelly.util.ThreadPoolWrap;
import com.oo58.jelly.util.ToastManager;
import com.oo58.jelly.util.Util;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Desc: 用户登录
 * Created by sunjinfang on 2016/6/14 11:37.
 */
public class LoginManager {


    //登录类型
    public static enum LoginType {
        LoginType_Code("phone"),
        LoginType_QQ("qq"),
        LoginType_WX("weixin"),
        LoginType_Sina("sina"),
        LoginType_Auto("auto"),
        LoginType_ZS("zs");

        public String name;

        private LoginType(String name) {
            this.name = name;
        }
    }

    ;

    public static final int LoginHandler_Success = 38864;
    public static final int LoginHandler_Failed = 38865;
    public static final int LoginHandler_Error = 38866;
    public static final int GO_LOGIN = 38867;

    private static IWXAPI mWeixinAPI; //微信接口

    /**
     * 登录的数据 全部保存
     *
     * @param type
     * @param openid
     * @param token
     * @param deviceToken
     */
    public static void doLogin(final Context mContext, final LoginType type, final String openid, final String token, final String deviceToken, final Handler handler) {
        if(!NetStateUtil.checkNet(mContext)){
            ToastManager.makeToast(mContext,"网络异常");
            return ;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String result = "";
                    String loginType = type.name ;

                    if(type == LoginType.LoginType_Auto){
                        if(SharedPreUtil.contains(mContext,AppContance.LOGIN_TYPE)){
                            loginType = SharedPreUtil.getString(mContext,AppContance.LOGIN_TYPE);
                        }
                    }
                    if (type == LoginType.LoginType_Code) {
                        result = Util.addRpcEvent(RpcEvent.CallUserLogin, openid, token, deviceToken, "");
                    } else if (type == LoginType.LoginType_QQ) {
                        result = Util.addRpcEvent(RpcEvent.CallQQLogin, openid, token, deviceToken, "");
                    } else if (type == LoginType.LoginType_WX) {
                        result = Util.addRpcEvent(RpcEvent.CallWXLogin, openid, token, deviceToken, "");
                    } else if (type == LoginType.LoginType_Sina) {
                        result = Util.addRpcEvent(RpcEvent.CallSinaLogin, openid, token, deviceToken, "");
                    } else if (type == LoginType.LoginType_ZS) {
                        result = Util.addRpcEvent(RpcEvent.CallUserLogin, openid, token, deviceToken, "");
                    } else if (type == LoginType.LoginType_Auto) {
                        result = Util.addRpcEvent(RpcEvent.CallAutoLogin, openid, token, deviceToken, 1);
                    }

                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
//                        JSONObject obj = new JSONObject(result);
                        JSONObject usrInfo = obj.getJSONObject("data");
                        JSONObject usrauth = obj.getJSONObject("auth");

                        //保存登录数据

                        String username = usrInfo.getString("username");
                        String password = usrInfo.getString("password");

                        String id = usrInfo.getString("id");
                        String openkey =usrauth.getString("openkey");
                        String timestamp =usrauth.getString("timestamp");
                        String useropenid =usrauth.getString("openid");

                        String icon = AppUrl.USER_LOGO_ROOT+usrInfo.getString("icon");
                        String nickname = usrInfo.getString("nickname");
                        String user_type = usrInfo.getString("user_type"); //主播 普通用户
                        String phone = usrInfo.getString("phone");
                        String secret = usrInfo.getString("secret");
                        String province = usrInfo.getString("province");
                        String city = usrInfo.getString("city");
                        int gender = usrInfo.getInt("gender"); //性别  0 未知， 1 男，2 女
                        String beans = usrInfo.getString("beans"); //
                        int coins = usrInfo.getInt("coins"); //
                        int getcoins = usrInfo.getInt("received_beans"); //
                        int sendcoins = usrInfo.getInt("cost_beans"); //

                        int viplevel = usrInfo.getInt("vip_lv"); //
                        int costlevel = usrInfo.getInt("cost_level"); //
                        int receivelevel = usrInfo.getInt("received_level"); //

                        int heart = usrInfo.getInt("hearts"); //

                        String address = usrInfo.getString("address");//位置
                        String birthday = usrInfo.getString("birthday");//出生日期

//            int exp = usrInfo.getInt("exp"); //
//            int follow = usrInfo.getInt("follow"); //
//            int fans = usrInfo.getInt("fans"); //
//            String sign = usrInfo.getString("sign"); //


                        SharedPreUtil.put(mContext, AppContance.USER, username); //用户名
                        SharedPreUtil.put(mContext, AppContance.PASS, password);//密码
                        SharedPreUtil.put(mContext,AppContance.USER_OPENKEY,openkey);
                        SharedPreUtil.put(mContext,AppContance.TIMESTAMP,timestamp);


                        SharedPreUtil.put(mContext, AppContance.OPEN_ID, id);
                        SharedPreUtil.put(mContext, AppContance.USER_SECRET, secret);
                        SharedPreUtil.put(mContext, AppContance.USER_ICON, icon);

                        SharedPreUtil.put(mContext, AppContance.NICKNAME, nickname);
                        SharedPreUtil.put(mContext, AppContance.USER_TYPE, user_type);
                        SharedPreUtil.put(mContext, AppContance.USER_PHONE, phone);
                        SharedPreUtil.put(mContext, AppContance.USER_PROVINCE, province);
                        SharedPreUtil.put(mContext, AppContance.USER_CITY, city);
                        SharedPreUtil.put(mContext, AppContance.GENDER, gender);

                        SharedPreUtil.put(mContext, AppContance.USER_BEANS, beans);
                        SharedPreUtil.put(mContext, AppContance.USER_COINS, coins);
                        SharedPreUtil.put(mContext, AppContance.USER_GET_COINS, getcoins);
                        SharedPreUtil.put(mContext, AppContance.USER_SEND_COINS, sendcoins);
                        SharedPreUtil.put(mContext, AppContance.USER_HEART, heart);

                        SharedPreUtil.put(mContext, AppContance.USER_VIP_LEV, viplevel);
                        SharedPreUtil.put(mContext, AppContance.USER_COST_LEV, costlevel);
                        SharedPreUtil.put(mContext, AppContance.USER_RECEIVE_LEV, receivelevel);
                        SharedPreUtil.put(mContext, AppContance.USER_AGE, birthday);


//            SharedPreUtil.put(mContext, AppContance.USER_EXP, exp);
//            SharedPreUtil.put(mContext, AppContance.USER_FOLLOW, follow);
//            SharedPreUtil.put(mContext, AppContance.USER_FANS, fans);
//            SharedPreUtil.put(mContext, AppContance.ROOM_NOTICE, sign);




                        //保存登录类型
                        SharedPreUtil.put(mContext, AppContance.LOGIN_TYPE, loginType);//保存登录类型
                        SharedPreUtil.put(mContext, AppContance.USER_LOGIN, true);//设置为登录状态
                        SharedPreUtil.put(mContext, AppContance.USER_AUTO_LOGIN, true);//设置下次自动登录






//                        //保存登录数据
//                        String data = obj.getString("data");
//                        String auth = obj.getString("auth");
//                        JSONObject usrInfo = new JSONObject(data);
//                        JSONObject usrauth = new JSONObject(auth);
//
//                        String id = usrInfo.getString("id");
//                        String openkey =usrauth.getString("openkey");
//                        String timestamp =usrauth.getString("timestamp");
//                        String useropenid =usrauth.getString("openid");
//
//                        String icon = AppUrl.USER_LOGO_ROOT+usrInfo.getString("icon");
//                        String username = usrInfo.getString("username");
//                        String nickname = usrInfo.getString("nickname");
//                        String user_type = usrInfo.getString("user_type"); //主播 普通用户
//                        String password = usrInfo.getString("password");
//                        String phone = usrInfo.getString("phone");
//                        String secret = usrInfo.getString("secret");
//                        String province = usrInfo.getString("province");
//                        String city = usrInfo.getString("city");
//                        int gender = usrInfo.getInt("gender"); //性别
//                        int beans = usrInfo.getInt("beans"); //
//                        int coins = usrInfo.getInt("coins"); //
//                        int level = usrInfo.getInt("level"); //
//                        int exp = usrInfo.getInt("exp"); //
//                        int follow = usrInfo.getInt("follow"); //
//                        int fans = usrInfo.getInt("fans"); //
//                        String sign = usrInfo.getString("sign"); //
//
//
//                        SharedPreUtil.put(context, AppContance.USER, openid); //用户名
//                        SharedPreUtil.put(context,AppContance.USER_OPENKEY,openkey);
//                        SharedPreUtil.put(context,AppContance.TIMESTAMP,timestamp);
//
//
//                        SharedPreUtil.put(context, AppContance.PASS, token);//密码
//
//                        SharedPreUtil.put(context, AppContance.OPEN_ID, id);
//                        SharedPreUtil.put(context, AppContance.USER_SECRET, secret);
//                        SharedPreUtil.put(context, AppContance.USER_ICON, icon);
//
//                        SharedPreUtil.put(context, AppContance.NICKNAME, nickname);
//                        SharedPreUtil.put(context, AppContance.USER_TYPE, user_type);
//                        SharedPreUtil.put(context, AppContance.USER_PHONE, phone);
//                        SharedPreUtil.put(context, AppContance.USER_PROVINCE, province);
//                        SharedPreUtil.put(context, AppContance.USER_CITY, city);
//                        SharedPreUtil.put(context, AppContance.GENDER, gender);
//
//                        SharedPreUtil.put(context, AppContance.USER_BEANS, beans);
//                        SharedPreUtil.put(context, AppContance.USER_COINS, coins);
//
//                        SharedPreUtil.put(context, AppContance.USER_VIP_LEV, level);
//                        SharedPreUtil.put(context, AppContance.USER_EXP, exp);
//                        SharedPreUtil.put(context, AppContance.USER_FOLLOW, follow);
//                        SharedPreUtil.put(context, AppContance.USER_FANS, fans);
//                        SharedPreUtil.put(context, AppContance.ROOM_NOTICE, sign);
//
//
//                        //保存登录类型
//                        SharedPreUtil.put(context, AppContance.LOGIN_TYPE, loginType);//保存登录类型
//                        SharedPreUtil.put(context, AppContance.USER_LOGIN, true);//设置为登录状态
//                        SharedPreUtil.put(context, AppContance.USER_AUTO_LOGIN, true);//设置下次自动登录

                        handler.sendEmptyMessage(LoginHandler_Success);
                    } else {
                        handler.sendEmptyMessage(LoginHandler_Failed);
                        SharedPreUtil.put(mContext, AppContance.USER_AUTO_LOGIN, false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    SharedPreUtil.put(mContext, AppContance.USER_LOGIN, false);
                    handler.sendEmptyMessage(LoginHandler_Error);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    /**
     * qq登录接收
     *
     * @author Administrator
     */
    private static class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            // Utils.showToast(getApplicationContext(), response.toString()
            // + "登录成功");
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            // Utils.showToast(getApplicationContext(), "onError: "
            // + e.errorDetail);
            doFalid();
        }

        @Override
        public void onCancel() {
            // Utils.showToast(getApplicationContext(), "onCancel: ");
            doFalid();
        }

        protected void doFalid() {

        }
    }

    /**
     * qq登录
     */
    public static void callQQLogin(final Activity context, final Handler handler) {
        Tencent mTencent = Tencent.createInstance(CommonKey.APPID_QQ, context);
        if (mTencent == null) {
            return;
        }
        IUiListener listener = new BaseUiListener() {
            protected void doComplete(JSONObject paramAnonymousJSONObject) {
                String access_token = paramAnonymousJSONObject
                        .optString("access_token");
                String openid = paramAnonymousJSONObject.optString("openid");
                LoginManager.doLogin(context, LoginManager.LoginType.LoginType_QQ, openid, access_token, AppContance.phoneImeToken, handler);
            }

            @Override
            protected void doFalid() {
                handler.sendEmptyMessage(LoginHandler_Failed);
            }
        };
        mTencent.login(context, "all", listener);
    }


    /**
     * 微信登录
     */
    public static void callWeixinlogin(Context mContext) {
        if (mWeixinAPI == null) {
            mWeixinAPI = WXAPIFactory.createWXAPI(mContext, CommonKey.APPID_WX, false);
        }
        if (!mWeixinAPI.isWXAppInstalled()) {
            // 提醒用户没有按照微信
            Toast.makeText(mContext, "没有安装微信客户端！", Toast.LENGTH_SHORT).show();
            return;
        }
        mWeixinAPI.registerApp(CommonKey.APPID_WX);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.transaction = CommonKey.APPID_WX;
        req.state = "none";
        mWeixinAPI.sendReq(req);
    }


    /**
     * 新浪微博登录
     */
    public static void callSinaLogin() {

    }

    /**
     * 验证码登录
     */
    public static void callUserLogin(Context context, Handler handler) {
        String phone = "";
        LoginManager.doLogin(context, LoginManager.LoginType.LoginType_Code, phone, AppContance.phoneImeToken, AppContance.phoneImeToken, handler);
    }

}
