package com.abner.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.abner.mobilesafe.db.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Project   com.abner.mobilesafe.engine
 *
 * @Author Abner
 * Time   2016/9/13.17:17
 */
public class AppInfoProvider {

    /**
     * 返回当前手机所有的应用相关信息（名称，包名，图标，（内存，sd卡），（系统，用户））
     * @param context 获取包的管理者的上下文环境
     * @return   包含手机安装应用相关信息的对象
     */
    public static List<AppInfo> getAppInfoList(Context context) {
        ArrayList<AppInfo> appInfoList = new ArrayList<>();
        //1.包的管理者对象
        PackageManager packageManager = context.getPackageManager();
        //2.获取安装在手机上应用相关信息的集合
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);
        //3.循环遍历应用信息的集合
        for (PackageInfo packageInfo : packageInfoList) {
            AppInfo appInfo = new AppInfo();
            //4.获取应用的包名
            appInfo.setPackageName(packageInfo.packageName);
            //应用名称
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            appInfo.setIcon(applicationInfo.loadIcon(packageManager));
            appInfo.setName(applicationInfo.loadLabel(packageManager).toString());
            //判断是否为系统应用（每一个手机上的应用对应的flag都不一致）
            if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
                //系统应用
                appInfo.setSystem(true);
            }else {
                //非系统应用
                appInfo.setSystem(false);
            }
            //判断是否是sd卡中的安装的应用
            if ((applicationInfo.flags & applicationInfo.FLAG_EXTERNAL_STORAGE)==applicationInfo.FLAG_EXTERNAL_STORAGE){
                //sd卡中的应用
                appInfo.setSdCard(true);
            }else {
                //非sd卡中的应用
                appInfo.setSdCard(false);
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;

    }
}
