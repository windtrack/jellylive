package com.oo58.jelly.entity.room;

/**
 * Desc: 秀场移植过来的聊天消息基类
 * Created by sunjinfang on 2016/5/17 .
 */
public class ChatMessage {
	private String say;
	private String content;
	private int tid;
	private String tname;
	private String sname;
	private String icon;
	private String is_shouhu;
	private String gift_id;
	private String t_id;
	private String s_id;
	private String user_type;
	private int vip_lv;//VIP等级
	private int cost_level;//财富等级
	private int onlineNum ;
	private boolean isPrivateChat ;//私聊
	public ChatMessage(String say, String content, int tid, String tname,
					   String sname, String icon, String is_shouhu, String gift_id,
					   String t_id, String s_id, String user_type) {
		super();
		this.say = say;
		this.content = content;
		this.tid = tid;
		this.tname = tname;
		this.sname = sname;
		this.icon = icon;
		this.is_shouhu = is_shouhu;
		this.gift_id = gift_id;
		this.t_id = t_id;
		this.s_id = s_id;
		this.user_type = user_type;
	}

	public ChatMessage() {
		super();
	}

	public String getSay() {
		return say;
	}

	public void setSay(String say) {
		this.say = say;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIs_shouhu() {
		return is_shouhu;
	}

	public void setIs_shouhu(String is_shouhu) {
		this.is_shouhu = is_shouhu;
	}

	public String getGift_id() {
		return gift_id;
	}

	public void setGift_id(String gift_id) {
		this.gift_id = gift_id;
	}

	public String getT_id() {
		return t_id;
	}

	public void setT_id(String t_id) {
		this.t_id = t_id;
	}

	public String getS_id() {
		return s_id;
	}

	public void setS_id(String s_id) {
		this.s_id = s_id;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public int getVip_lv(){return vip_lv;}

	public void setVip_lv(int lev){vip_lv = lev ;}

	@Override
	public String toString() {
		return "ChatMessage [say=" + say + ", content=" + content + ", tid="
				+ tid + ", tname=" + tname + ", sname=" + sname + ", icon="
				+ icon + ", is_shouhu=" + is_shouhu + ", gift_id=" + gift_id
				+ ", t_id=" + t_id + ", s_id=" + s_id + ", user_type="
				+ user_type +"vip_lv =" + vip_lv+"onlineNum = "+ onlineNum+"]";
	}

	public int getOnlineNum() {
		return onlineNum;
	}

	public void setOnlineNum(int onlineNum) {
		this.onlineNum = onlineNum;
	}

	public int getCost_level() {
		return cost_level;
	}

	public void setCost_level(int cost_level) {
		this.cost_level = cost_level;
	}

	public boolean isPrivateChat() {
		return isPrivateChat;
	}

	public void setPrivateChat(boolean privateChat) {
		isPrivateChat = privateChat;
	}
}
