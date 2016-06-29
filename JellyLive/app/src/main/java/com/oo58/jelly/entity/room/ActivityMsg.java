package com.oo58.jelly.entity.room;

/**
 * 封装的消息体
 * @author sunjinfang
 */
public class ActivityMsg {
	private int tid; //消息头  用来判断是哪条消息
	private String msg;	//消息体
	private int onlineNum ;
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getOnlineNum() {
		return onlineNum;
	}
	public void setOnlineNum(int onlineNum) {
		this.onlineNum = onlineNum;
	}
	@Override
	public String toString() {
		return "ActivityMsg [tid=" + tid + ", msg=" + msg + "]";
	}


}
