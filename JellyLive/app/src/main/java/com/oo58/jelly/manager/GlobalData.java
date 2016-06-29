package com.oo58.jelly.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.oo58.jelly.common.AppContance;
import com.oo58.jelly.util.SharedPreUtil;


/**
 * Desc:  常用的数据
 * Created by sunjinfang on 2016/5/10.
 */
public class GlobalData {

    private static ImageLoader mImageLoader; //图片加载器

    public static boolean isBeShoutUp;//是否被禁言
    /**
     * 获取图片加载器
     * @return
     */
    public static ImageLoader getmImageLoader(Context context) {
        if(mImageLoader == null){
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
        return mImageLoader;
    }


    /**
     * 获取用户id
     * @return
     */
    public static String getUID(Context context){
        return SharedPreUtil.getString(context,AppContance.OPEN_ID,"");
    }
    /**
     * 获取用户登录秘钥
     * @return
     */
    public static String getUSecert(Context context){
        return SharedPreUtil.getString(context,AppContance.USER_SECRET,"");
    }
    /**
     * 获取用户登录秘钥
     * @return
     */
    public static String getOpenKey(Context context){
        return SharedPreUtil.getString(context,AppContance.USER_OPENKEY,"");
    }
    /**
     * 获取用户名称
     * @return
     */
    public static String getUName(Context context){
        return SharedPreUtil.getString(context,AppContance.NICKNAME,"");
    }

    /**
     * 获取头像
     * @param context
     * @return
     */
    public static String getIcon(Context context){
        return SharedPreUtil.getString(context,AppContance.USER_ICON,"");
    }

    /**
     * 性别
     * @param context
     * @return
     */
    public static int getGender(Context context){
        return SharedPreUtil.getInt(context,AppContance.GENDER);
    }

    /**
     * 签名
     * @param context
     * @return
     */
    public static String getSign(Context context){
        return SharedPreUtil.getString(context,AppContance.USER_SIGN);
    }

    /**
     * 获取用户时间戳
     * @return
     */
    public static String getUTimeStamp(Context context){
        return SharedPreUtil.getString(context,AppContance.TIMESTAMP,"");
    }

    /**
     * 判断主播是否在线
     * @return
     */
    public static boolean checkLogin(Context context){
        return SharedPreUtil.getBoolean(context,AppContance.USER_LOGIN,false);
    }

    /**
     * 是否可自动登录
     * @param context
     * @return
     */
    public static boolean checkAutoLogin(Context context){
        return SharedPreUtil.getBoolean(context,AppContance.USER_AUTO_LOGIN,false);
    }
    /**
     * 获取用户金钱
     * @param context
     * @return
     */
    public static String getUBeans(Context context){
        return SharedPreUtil.getString(context,AppContance.USER_BEANS);
    }

    /**
     * 判断自己是不是主播
     * @param context
     * @return
     */
    public static boolean isAnchor(Context context){
        String type = SharedPreUtil.getString(context,AppContance.USER_TYPE);
        return type.equals("anchor") ;
    }


}
