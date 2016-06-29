package com.oo58.jelly.entity;

/**
 * @author zhongxf
 * @Description
 * @Date 2016/6/16.
 */
public class RankListVo {
    private int position;//排序的位置
    private String name;//名字
    private String money;//消费的钱数
    private int sex;//性别
    private int label;//等级
    private String icon ;//头像

    public void setPosition(int position) {
        this.position = position;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public String getMoney() {
        return money;
    }

    public int getSex() {
        return sex;
    }

    public int getLabel() {
        return label;
    }
}
