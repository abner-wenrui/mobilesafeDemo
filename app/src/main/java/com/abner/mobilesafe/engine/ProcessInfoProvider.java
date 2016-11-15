package com.abner.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Debug;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.db.domain.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Project   com.abner.mobilesafe.engine
 *
 * @Author Abner
 * Time   2016/9/19.9:57
 */
public class ProcessInfoProvider {

    /**
     * 获取进程总数的方法
     *
     * @param context 上下文信息
     * @return 正在运行进程总数
     */
    public static int getProcessCont(Context context) {
        //获取activityManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正遭运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //返回集合总数
        return runningAppProcesses.size();
    }

    /**
     * 获取可用运行内存
     *
     * @param context 上下文信息
     * @return 剩余可用运行内存大小 bytes
     */
    public static long getAvailSpace(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * 获取总运行内存
     *
     * @param context 上下文信息
     * @return 总运行内存大小 bytes
     */
    public static long getTotalSpace(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }

    /**
     * 获取进程信息
     * @param context 上下文
     * @return 封装好的进程信息List
     */
    public static List<ProcessInfo> getProcessInfo(Context context) {
        //1.拿activityManager管理者对象和packageManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        //2.获取正在运行进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        List<ProcessInfo> processInfoList = new ArrayList<>();
        //3.循环遍历上述集合，获取进程相关信息（名称，包名，图标，使用内存大小，是否为系统进程(状态机)）
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            ProcessInfo processInfo = new ProcessInfo();
            //4.进程的名字==包名
            processInfo.setPackageName(info.processName);
            //5.获取进程占用内存的大小(传递一个进程对应的pid数组)
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            //6.返回数组中索引位置为0的对象，为当前进程的内存信息的对象
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //7。获取已使用的大小
            long totalPrivateDirty = memoryInfo.getTotalPrivateDirty() * 1024;
            processInfo.setMemSize(totalPrivateDirty);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(info.processName, 0);
                //8.获取应用的名称
                processInfo.setName(applicationInfo.loadLabel(pm).toString());
                //9.获取应用图标
                processInfo.setIcon(applicationInfo.loadIcon(pm));
                //10.判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.setSystem(true);
                } else {
                    processInfo.setSystem(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                processInfo.setName(info.processName);
                processInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                processInfo.setSystem(true);
                e.printStackTrace();
            }
            processInfoList.add(processInfo);
        }
        return processInfoList;
    }

    /**
     * 杀死单个进程
     * @param context
     * @param processInfo
     */
    public static void killProcess(Context context, ProcessInfo processInfo) {
        //1.获取activityManager的对象
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //2.杀死指定包名进程
        am.killBackgroundProcesses(processInfo.getPackageName());
    }

    /**
     * 杀死所有进程
     */
    public static void killAll(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
            //杀死除手机卫士以外的应用
            if (info.processName.equals(context.getPackageName()))
                continue;
            am.killBackgroundProcesses(info.processName);
        }
    }
}
