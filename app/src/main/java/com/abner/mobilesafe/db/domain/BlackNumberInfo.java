package com.abner.mobilesafe.db.domain;

/**
 * Project   com.abner.mobilesafe.db.domain
 *
 * @Author Abner
 * Time   2016/9/9.13:15
 */
public class BlackNumberInfo {
    public String phone;
    public String mode;

    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "phone='" + phone + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
