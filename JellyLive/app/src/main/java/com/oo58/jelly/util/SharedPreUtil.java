package com.oo58.jelly.util;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Desc: SharedPreferences 存储
 * Created by sunjinfang on 2016/6/2 .
 */
public class SharedPreUtil {

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SharedPreUtil.class.getSimpleName(), 0);
    }

    private static SharedPreferences.Editor getEdit(Context context) {
        return getSharedPreferences(context).edit();
    }

    /**
     * 保存数据
     * @param context
     * @param key
     * @param object
     */
    public static void put(Context context, String key, Object object) {
        if(context == null){
            return ;
        }
        SharedPreferences.Editor editor = getEdit(context);
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else if (object instanceof String[]) {
            StringBuilder datas = new StringBuilder();
            String[] data = (String[]) object;

            for (int i = 0; i < data.length; ++i) {
                if (i != 0) {
                    datas.append(":");
                }
                datas.append(data[i]);
            }
            editor.putString(key, datas.toString());
        }
        editor.commit();
    }

    /***************************************************************************
     *  以下为取数据
     *****************************************************************************
     */
    public static String getString(Context context, String key,String defaultObject) {
        return getSharedPreferences(context).getString(key, defaultObject);
    }

    public static String getString(Context context, String key) {
        return getSharedPreferences(context).getString(key, "");
    }

    public static int getInt(Context context, String key,int defaultVaule) {
        return getSharedPreferences(context).getInt(key, defaultVaule);
    }
    public static int getInt(Context context, String key) {
        return getSharedPreferences(context).getInt(key, 0);
    }

    public static boolean getBoolean(Context context, String key ,boolean defaultVaule) {
        return getSharedPreferences(context).getBoolean(key, defaultVaule);
    }

    public static float getFloat(Context context, String key,float defaultVaule) {
        return getSharedPreferences(context).getFloat(key, defaultVaule);
    }

    public static long getLong(Context context, String key, long defaultVaule) {
        return getSharedPreferences(context).getLong(key, defaultVaule);
    }

    public static String[] getStringArray(Context context, String key) {
        return getString(context, key).split(":");
    }

    /**
     * 删除数据
     * @param context
     * @param key
     */
    public static void remove(Context context, String key) {
        SharedPreferences.Editor editor = getEdit(context);
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清空数据
     * @param context
     */
    public static void clear(Context context) {
        SharedPreferences.Editor editor = getEdit(context);
        editor.clear();
        editor.commit();
    }

    /**
     * 是否有该字段
     * @param context
     * @param key
     * @return
     */
    public static boolean contains(Context context, String key) {
        return getSharedPreferences(context).contains(key);
    }

    /**
     * 返回所有的存储数据
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context) {
        return getSharedPreferences(context).getAll();
    }
}
