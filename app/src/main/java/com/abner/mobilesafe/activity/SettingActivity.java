package com.abner.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.service.AddressService;
import com.abner.mobilesafe.service.BlackNumberService;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.ServiceUtil;
import com.abner.mobilesafe.utils.SpUtil;
import com.abner.mobilesafe.view.SettingClickView;
import com.abner.mobilesafe.view.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    private String[] mToastStyleDes;
    private int mToastStyle;
    private SettingClickView scv_toast_style;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initUpdate();
        initAddress();
        initToastStyle();
        initLocation();
        initBlacknumber();
    }

    //拦截黑名单短信还有电话的方法
    private void initBlacknumber() {
        final SettingItemView siv_blacknumber = (SettingItemView) findViewById(R.id.siv_blacknamber);
        final boolean isRunning = ServiceUtil.isRunning(this, "com.abner.mobilesafe.service.BlackNumberService");
        siv_blacknumber.setCheck(isRunning);
        siv_blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = siv_blacknumber.isCheck();
                siv_blacknumber.setCheck(!check);
                if (!check) {
                    startService(new Intent(getApplicationContext(), BlackNumberService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), BlackNumberService.class));
                }
            }
        });
    }

    /**
     * 设置Toast位置的方法（双击居中）
     */
    private void initLocation() {
        SettingClickView scv_location = (SettingClickView) findViewById(R.id.scv_location);
        scv_location.setTitle("归属地提示框的位置");
        scv_location.setDes("设置归属地提示框的位置");
        scv_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ToastLocationActivity.class));
            }
        });
    }

    /**
     * 设置Toast的颜色
     */
    private void initToastStyle() {
        scv_toast_style = (SettingClickView) findViewById(R.id.scv_toast_style);
        scv_toast_style.setTitle("设置归属地显示风格");
        //1.创建文字所在的描述String类型的数组
        mToastStyleDes = new String[]{"透明", "橙色", "蓝色", "灰色", "绿色"};
        //2.从SP中获取样式显示的索引值（int）用于获取描述文字
        mToastStyle = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
        //3通过索引获取字符串数组中的文字，显示给描述内容控件
        scv_toast_style.setDes(mToastStyleDes[mToastStyle]);
        //4监听点击事件，弹出对话框
        scv_toast_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastStyleDialog();
            }
        });
    }

    //展示Toast样式设置的的对话框
    private void showToastStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("选择归属地样式");

        builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpUtil.putInt(getApplicationContext(), ConstantValue.TOAST_STYLE, which);
                dialog.dismiss();
                scv_toast_style.setDes(mToastStyleDes[which]);
            }
        });
        //消极按钮
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * 是否显示电话号码归属地的方法
     */
    private void initAddress() {
        final SettingItemView siv_address = (SettingItemView) findViewById(R.id.siv_address);
        boolean running = ServiceUtil.isRunning(this, "com.abner.mobilesafe.service.AddressService");
        siv_address.setCheck(running);
        siv_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果之前未选中变为选中
                //如果之前选中变为未选中

                //获取之前的选中状态
                boolean ischick = siv_address.isCheck();
                //将原有状态取反
                siv_address.setCheck(!ischick);
                //            SpUtil.putBoolean(getApplicationContext(),ConstantValue.,!ischick);
                if (!ischick) {
                    startService(new Intent(getApplicationContext(), AddressService.class));
                } else {
                    stopService(new Intent(getApplicationContext(), AddressService.class));
                }
            }
        });
    }

    /**
     * 是否开启软件更新
     */
    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
        //获取已有的开关用作显示
        boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        //是否选中根据上一次存储的结果去做决定
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果之前选中变为未选中
                //如果之前未选中变成选中

                //获取之前的选中状态
                boolean isCheck = siv_update.isCheck();
                //将原有状态取反
                siv_update.setCheck(!isCheck);
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, !isCheck);
            }
        });
    }
}
