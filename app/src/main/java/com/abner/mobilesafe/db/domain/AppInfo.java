package com.abner.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

/**
 * Project   com.abner.mobilesafe.db.domain
 *
 * @Author Abner
 * Time   2016/9/13.18:46
 */
public class AppInfo {
    public String name;
    public String packageName;
    public Drawable icon;
    public boolean isSdCard;
    public boolean isSystem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSdCard() {
        return isSdCard;
    }

    public void setSdCard(boolean sdCard) {
        isSdCard = sdCard;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }
}
