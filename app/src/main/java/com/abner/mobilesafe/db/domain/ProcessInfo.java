package com.abner.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

/**
 * Project   com.abner.mobilesafe.db.domain
 *
 * @Author Abner
 * Time   2016/9/19.10:47
 */
public class ProcessInfo {
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable icon;
    public long memSize;
    public boolean isCheck;
    public boolean isSystem;
    public String packageName;
}
