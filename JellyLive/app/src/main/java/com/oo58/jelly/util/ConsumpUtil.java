package com.oo58.jelly.util;

import java.math.BigDecimal;

/**
 * @author: zhongxf
 * @Description: 平台所有的消费金额的常量类
 * @date: 2016年4月19日
 */
public class ConsumpUtil {

	// 卡牌价格
	public final static String COPPER_CARD = "10000";// 通牌价格
	public final static String SLIVER_CARD = "25000";// 银牌价格
	public final static String GOLD_CARD = "50000";// 金牌价格

	// 点歌的价格
	public final static String RED_STAR_PRICE = "50000"; // 红星主播点歌价格
	public final static String DIAMONDS_PRICE = "100000";// 钻石主播点歌价格
	public final static String CROWN_PRICE = "150000";// 皇冠主播点歌价格

	/**
	 * @author zhongxf
	 * @Description：判断两个字符串类型的数字的大小
	 * @param： arg0 数字1 arg1：数字2
	 * @return 如果arg0大于arg1 返回1 arg0小于arg1 返回-1 arg0等于arg1返回0
	 */
	public static int compareTo(String arg0, String arg1) {
		BigDecimal b1 = new BigDecimal(arg0);
		BigDecimal b2 = new BigDecimal(arg1);
		return b1.compareTo(b2);
	}

	

	
	public static BigDecimal parseBigDecimal(String arg0) {
		BigDecimal b1 = new BigDecimal(arg0);
		return b1;
	}
	
	
	public static boolean compare(String arg0, String arg1){
		BigDecimal b1 = new BigDecimal(arg0);
		BigDecimal b2 = new BigDecimal(arg1);
		return b1.compareTo(b2)>=0 ;
	}
	
	
	
	
}
