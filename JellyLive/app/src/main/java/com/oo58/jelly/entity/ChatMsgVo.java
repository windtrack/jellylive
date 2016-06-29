package com.oo58.jelly.entity;

/**
 * @author zhongxf
 * @Description 实际显示的消息
 * @Date 2016/6/16.
 */
public class ChatMsgVo {
    private String userName;//发送的用户的昵称
    private String vipLabel;//vip的等级
    private String content;//消息体
    private String icon;//头像
    private String name;//名称

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setVipLabel(String vipLabel) {
        this.vipLabel = vipLabel;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserName() {
        return userName;
    }

    public String getVipLabel() {
        return vipLabel;
    }

    public String getContent() {
        return content;
    }
}
