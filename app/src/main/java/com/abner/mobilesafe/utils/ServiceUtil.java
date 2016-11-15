package com.abner.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Project   com.abner.mobilesafe.utils
 *
 * @Author Abner
 * Time   2016/9/7.8:57
 */
public class ServiceUtil {

    private static ActivityManager mAM;

    public static boolean isRunning(Context ctx, String serviceName){
        //获取服务管理者，获取所有正在运行的服务，寻找我们的服务
        mAM = ((ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE));

        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(1000);
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices ) {
            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                return true;
            }
        }
        return false;
    }
}
