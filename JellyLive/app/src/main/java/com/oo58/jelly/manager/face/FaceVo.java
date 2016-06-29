package com.oo58.jelly.manager.face;

/**
 * @author zhongxf
 * @Description 表情的实体
 * @Date 2016/6/25.
 */
public class FaceVo {
    private String text;//表情的文字
    private int resId;//表情的资源ID

    public void setText(String text) {
        this.text = text;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getText() {
        return text;
    }

    public int getResId() {
        return resId;
    }
}
