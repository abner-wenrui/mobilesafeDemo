package com.abner.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Project   com.abner.mobilesafe.utils
 *
 * @Author Abner
 * Time   2016/8/29.8:29
 */
public class SpUtil {

    private static SharedPreferences sp;

    /**
     * 写入boolean变量至sp中
     *
     * @param context 上下文环境
     * @param key     存储结点的名称
     * @param value   存储结点的值boolean
     */
    public static void putBoolean(Context context, String key, Boolean value) {
        //(存储结点文件的名称，读写方式）
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 读取boolean的标识至sp中
     *
     * @param context  上下文环境
     * @param key      存储结点的名称
     * @param defValue 没有此节点的默认值
     * @return 默认值或者此节点读取到的结果
     */
    public static boolean getBoolean(Context context, String key, Boolean defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    /**
     * 写入String变量至sp中
     *  @param context 上下文环境
     * @param key     存储站点的名称
     * @param value   存储站点的String值
     */
    public static void putString(Context context, String key, String  value) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, value).commit();
    }

    /**
     * 到sp中读取string变量
     *
     * @param context  上下文环境
     * @param key      存储节点名称
     * @param defValue 存储站点的默认String值
     * @return 默认值或此节点读取到的结果
     */
    public static String getString(Context context, String key, String defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }

    /**
     * 从sp中移除指定结点
     *
     * @param context 上下文环境
     * @param key     需要移除节点的名称
     */
    public static void remove(Context context, String key) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }

    /**
     * 到sp中读取int变量
     *
     * @param context 上下文环境
     * @param key     存储节点名称
     * @param i       存储站点的默认int值
     * @return 默认值或此节点读取到的结果
     */
    public static int getInt(Context context, String key, int i) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp.getInt(key, i);
    }

    /**
     * 写入int变量至sp中
     *
     * @param context 上下文环境
     * @param key     存储站点的名称
     * @param i       存储站点的int值
     */
    public static void putInt(Context context, String key, int i) {
        if (sp == null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, i).commit();
    }
}
