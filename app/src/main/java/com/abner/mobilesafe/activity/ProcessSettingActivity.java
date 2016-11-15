package com.abner.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.service.LockScreenService;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.ServiceUtil;
import com.abner.mobilesafe.utils.SpUtil;

public class ProcessSettingActivity extends AppCompatActivity {

    private CheckBox cb_show_system;
    private CheckBox cb_lock_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);

        initSystemShow();
        initLockScreenClear();
    }

    private void initLockScreenClear() {
        cb_lock_clear = (CheckBox) findViewById(R.id.cb_lock_clear);
        //回显之前的选中状态
        boolean isRunning = ServiceUtil.isRunning(this, "com.abner.mobilesafe.service.LockScreenService");
        cb_lock_clear.setChecked(isRunning);
        if (isRunning){
            cb_lock_clear.setText("锁屏清理已开启");
        }else {
            cb_lock_clear.setText("锁屏清理已关闭");
        }
        //对选中状态进行监听
        cb_lock_clear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_lock_clear.setText("锁屏清理已开启");
                    //开启服务
                    startService(new Intent(getApplicationContext(), LockScreenService.class));
                }else {
                    cb_lock_clear.setText("锁屏清理已关闭");
                    //关闭服务
                    stopService(new Intent(getApplicationContext(),LockScreenService.class));
                }
            }
        });
    }

    private void initSystemShow() {
        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        //回显之前的存储状态
        boolean showSystem = SpUtil.getBoolean(this, ConstantValue.SHOW_SYSTEM, false);
        cb_show_system.setChecked(showSystem);
        if (showSystem){
            cb_show_system.setText("显示系统进程");
        }else {
            cb_show_system.setText("隐藏系统进程");
        }
        //对选中状态进行监听
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cb_show_system.setText("显示系统进程");
                }else {
                    cb_show_system.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM,isChecked);
            }
        });
    }
}
