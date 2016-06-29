package com.oo58.jelly.entity;

/**
 * @author zhongxf
 * @Description 主播的Vo
 * @Date 2016/6/14.
 */
public class AnchorVo {

    private String anchorId;//主播的ID
    private String roomId;//房间的ID
    private String name;//主播的名字
    private String faceUrl;//头像的URL
    private String picUrl;//宣传图片的URL
    private String num;//观看人数
    private String title;//标题

    public String getLiveStream() {
        return liveStream;
    }

    public void setLiveStream(String liveStream) {
        this.liveStream = liveStream;
    }

    private String liveStream;//观看的流地址

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAnchorId(String anchorId) {
        this.anchorId = anchorId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getAnchorId() {
        return anchorId;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public String getNum() {
        return num;
    }
}
