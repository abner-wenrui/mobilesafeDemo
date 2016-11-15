package com.abner.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Project   com.abner.mobilesafe.utils
 *
 * @Author Abner
 * Time   2016/8/25.15:37
 */
public class ToastUtil {
    public static void show(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
