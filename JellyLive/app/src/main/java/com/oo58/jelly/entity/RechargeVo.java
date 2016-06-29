package com.oo58.jelly.entity;

/**
 * @author zhongxf
 * @Description 支付的列表
 * @Date 2016/6/17.
 */
public class RechargeVo {

    private double money;//钱数
    private double diamonds;//钻石数
    private double zs;//赠送数

    public void setMoney(double money) {
        this.money = money;
    }

    public void setDiamonds(double diamonds) {
        this.diamonds = diamonds;
    }

    public void setZs(double zs) {
        this.zs = zs;
    }

    public double getMoney() {
        return money;
    }

    public double getDiamonds() {
        return diamonds;
    }

    public double getZs() {
        return zs;
    }
}
