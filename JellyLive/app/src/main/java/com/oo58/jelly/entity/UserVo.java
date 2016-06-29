package com.oo58.jelly.entity;

import com.oo58.jelly.util.Util;

/**
 * @author zhongxf
 * @Description 观看直播的关注的实体类
 * @Date 2016/6/15.
 */
public class UserVo {
    private String uid;//id
    private String name;//名称
    private String icon;//头像地址  获取数据的时候 拼接成完整地址
    private String sign;//签名
    private int gender;//性别
    private int viplev;//vip等级
    private int costlev;//财富等级
    private int receivelev;//明星等级
    private String userType;//用户类型  主播  普通用户

    private int costBeans;//花费的乐币
    private int receivedBeans;//收到的乐币
    private String beans;//乐币
    private String heart;//红豆
    private int followCount;//关注数
    private int fansCount;//粉丝数
    private int isGetMsg;//是否接受开播推送消息  0:接受  1：不接受
    private int age;//年龄
    private String homeTown;//地区
    private String provice;//地区
    private String city;//地区
    private int affectiveState;//情感状态
    private boolean isFollow;//是否关注
    private boolean isBlacked;//是否加入黑名单
    private boolean isHelper; //是否是管理员
    private boolean isShoutUp;//是否被禁言
    private boolean isOnline;//是否在线

    public void setAge(int age) {
        this.age = age;
    }

    public String getProvice() {
        return provice;
    }

    public void setProvice(String provice) {
        this.provice = provice;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isAnchor(){
        return userType.equals("anchor") ;
    }


    public int getReceivedBeans() {
        return receivedBeans;
    }

    public void setReceivedBeans(int receivedBeans) {
        this.receivedBeans = receivedBeans;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getCostlev() {
        return costlev;
    }

    public void setCostlev(int costlev) {
        this.costlev = costlev;
    }

    public int getReceivelev() {
        return receivelev;
    }

    public void setReceivelev(int receivelev) {
        this.receivelev = receivelev;
    }

    public boolean isShoutUp() {
        return isShoutUp;
    }

    public void setShoutUp(boolean shoutUp) {
        isShoutUp = shoutUp;
    }

    public boolean isHelper() {
        return isHelper;
    }

    public void setHelper(boolean helper) {
        isHelper = helper;
    }

    public boolean isBlacked() {
        return isBlacked;
    }

    public void setBlacked(boolean blacked) {
        isBlacked = blacked;
    }

    public void setIsGetMsg(int isGetMsg) {
        this.isGetMsg = isGetMsg;
    }

    public int getIsGetMsg() {
        return isGetMsg;
    }

    public int getAge() {
        return age;
    }

    public void setAge(String date) {
        int age = Util.getAge(date);
        this.age = age;
    }


    public String getHomeTown() {
        return homeTown;
    }

    public void setHomeTown(String homeTown) {
        this.homeTown = homeTown;
    }

    public int getAffectiveState() {
        return affectiveState;
    }

    public void setAffectiveState(int affectiveState) {
        this.affectiveState = affectiveState;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }

    public int getCostBeans() {
        return costBeans;
    }

    public void setCostBeans(int costBeans) {
        this.costBeans = costBeans;
    }

    public String getBeans() {
        return beans;
    }

    public void setBeans(String beans) {
        this.beans = beans;
    }


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getViplev() {
        return viplev;
    }

    public void setViplev(int lev) {
        this.viplev = lev;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFollow() {
        return isFollow;
    }

    public void setFollow(boolean follow) {
        isFollow = follow;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
