package com.oo58.jelly.entity;

/**
 * Desc:
 * Created by sunjinfang on 2016/6/21 9:28.
 */
public class Gift {
    private String id;
    private String name;
    private String icon;
    private String effect;
    private int price;
    private int alt_price;
    private int category;
    private int received_beans;
    private String description;
    private int sended_exp;
    private int received_exp;
    private int sort_index;


    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public int getAlt_price() {
        return alt_price;
    }

    public void setAlt_price(int alt_price) {
        this.alt_price = alt_price;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getSended_exp() {
        return sended_exp;
    }

    public void setSended_exp(int sended_exp) {
        this.sended_exp = sended_exp;
    }

    public int getReceived_exp() {
        return received_exp;
    }

    public void setReceived_exp(int received_exp) {
        this.received_exp = received_exp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getReceived_beans() {
        return received_beans;
    }

    public void setReceived_beans(int received_beans) {
        this.received_beans = received_beans;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSort_index() {
        return sort_index;
    }

    public void setSort_index(int sort_index) {
        this.sort_index = sort_index;
    }




}
