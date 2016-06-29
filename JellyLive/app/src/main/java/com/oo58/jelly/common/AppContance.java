package com.oo58.jelly.common;

/**
 * @author zhongxf
 * @Description 全局的变量类
 * @Date 2016/6/13.
 */
public class AppContance {

    /**
     * 软件基本数据
     */
    public  static int localVersionCode = 0; //本地版本
    public  static String localVersionName = "1.0.0" ; //本地版本号
    public  static String phoneImeToken = "1.0.0" ; //手机唯一识别码

    public static int serverVersionCode = 0 ; //服务器版本
    public static String serverVersionName; //服务器版本号
    public static String apkDownLoadUrl  ; //apk下载地址
    public static String updateTips;//版本更新提示文字

    /**
     * 登录界面信息
     */
    public static final String USER = "login_user";//用户名
    public static final String PASS = "login_pass";//密码
    public static final String LOGIN_TYPE = "logintype"; //登录类型  qq wx 等
    public static final String USER_LOGIN = "login";//是否登录
    public static final String USER_AUTO_LOGIN = "autologin";//是否可以自动登录  登录成功后 默认下次自动登录


    /**
     * 用户的相关信息
     */
    public static final String OPEN_ID = "openid";//用户id
    public static final String USER_SECRET = "secret" ;//secret
    public static final String USER_OPENKEY = "openkey" ;//openkey
    public static final String TIMESTAMP = "timestamp" ;//时间戳
    public static final String DEVICETOKEN = "device_token"; //设备序列号

    public static final String NICKNAME = "nickname"; //玩家昵称
    public static final String USER_AGE = "age"; //玩家昵称
    public static final String USER_ICON = "icon" ;//icon
    public static final String USER_TYPE = "user_type" ;//用户类型  游客  主播 普通用户
    public static final String GENDER = "sex"; //性别  0男  1女
//    public static final String USER_ROOMID = "roomid"; //房间号
    public static final String USER_FANS = "fans"; //粉丝数量
    public static final String USER_FOLLOW = "follow"; //关注数量
    public static final String ROOM_NOTICE = "notice"; //房间公告
    public static final String USER_SIGN = "sign"; //个人签名

    public static final String USER_PHONE = "phonenum";//手机号码
    public static final String USER_PROVINCE = "province";//省
    public static final String USER_CITY = "city";//市

    public static final String USER_BEANS = "beans"; //乐币
    public static final String USER_COINS = "conis"; //乐豆

    public static final String USER_GET_COINS = "getconis"; //收到的乐豆
    public static final String USER_SEND_COINS = "sendonis"; //发出的乐豆

    public static final String USER_HEART = "heart"; //红豆

    public static final String USER_VIP_LEV = "viplev";// vip等级
    public static final String USER_COST_LEV = "costlev";// vip等级
    public static final String USER_RECEIVE_LEV = "receivelev";// vip等级
    public static final String USER_EXP = "wealth_lv";//经验



    public static final String USER_FAST_GIFT_ID = "giftid" ;
    public static final String USER_FAST_GIFT_ICON = "gifticon" ;
    public static final String USER_FAST_GIFT_RPICE = "giftprice" ;

}
