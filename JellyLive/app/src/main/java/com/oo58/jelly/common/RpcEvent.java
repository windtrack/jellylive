package com.oo58.jelly.common;

/**
 * Desc: rpc协议配置
 * RpcEvent 最后一个是 分号 其他的 逗号隔开
 * Created by sunjinfang on 2016/5/10.
 */
public enum RpcEvent {
    GetLoginCode("m_sms_login_code"), //获取登录验证码
    CallUserLogin("m_user_login"), //手机登录
    CallSinaLogin("m_user_login"), //sina登录
    CallWXLogin("m_user_login"), //微信登录
    CallQQLogin("m_user_login"), //qq登录
    CallAutoLogin("m_user_login"), //自动登录


    GetHotList("m_hall_hot"),//获取热门主播列表   1热门 2最新
    GetHotBanner("m_system_banner"),//热门广告
    GetFansList("m_user_my_fans"),//关注列表
    GetFollowList("m_user_my_fans"),//粉丝列表
    GetUserInfo("m_profile"),//获取个人信息  目标 uid  是否需要等级数据 1 需要 0 不要 （下同）  关注 & 粉丝  账户 &收益  粉丝贡献榜
    GetRoomInfo("m_into_room"),//获取房间信息

    CallFollowAnchor("m_user_follow_add"),//关注主播
    CallCancelFollowAnchor("m_user_follow_cancel"),//取消关注主播
    CallAddHelper("m_user_helper"),//设置管理员 1 设置管理员 2 取消管理员
    CallShutUp("m_user_shut_up"),//禁言
    CallSetSign("m_user_sign_edit"),//设置签名
    CallSetArea("m_user_area_edit"),//设置地区
    CallEditNickName("m_user_nickname_edit"),//修改昵称
    CallEditGender("m_user_gender_edit"),//修改性别
    CallEditAge("m_user_age_edit"),//修改年龄

    CallStartLiving("m_room_start_live"),//直播间开始直播
    CallEndLiving("m_room_start_live"),//直播间结束直播
    CallUpdateIsLiving("m_room_update_endtime"),//标记直播时间点
    GetGiftList("m_get_gifts_list"),//礼物列表
    CallSendGift("m_send_gifts"),//送礼物
    CallSearch("m_search"),//搜索
    GetMyBlackList("m_user_blacklist_list"),//我的黑名单
    CallAddBlackList("m_user_blacklist_add"),//添加黑名单
    CallRemoveBlackList("m_user_blacklist_cancel"),//取消黑名单
    CallReport("m_user_report"),//举报
    CallHelperList("m_user_helper_list"),//管理员列表

    GetRoomViewer("m_get_room_views"),//获取房间观众列表
    GetConRankList("m_gx_rank"),//票贡献榜
    CallBlindPhone("m_user_mobile_bind"),//手机绑定

    CallSaveIconName("m_avatar_upload") ,//上传头像
    CallSendDanMu("m_sendmessage") ,//发送弹幕消息

    CheckUpdateVersion("m_software_update"),//版本更新检测

    ;
    public String name;
    private RpcEvent(String name) {
        this.name = name;
    }

}


