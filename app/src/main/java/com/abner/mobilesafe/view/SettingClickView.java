package com.abner.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abner.mobilesafe.R;

/**
 * Project   com.abner.mobilesafe.view
 *
 * @Author Abner
 * Time   2016/9/7.11:54
 */
public class SettingClickView extends RelativeLayout {

    private TextView tv_des;
    private TextView tv_title;

    public SettingClickView(Context context) {
        this(context, null);
    }

    public SettingClickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.setting_click_view, this);

        tv_des = (TextView) findViewById(R.id.tv_des);
        tv_title = (TextView) findViewById(R.id.tv_title);

    }

    /**
     * @param title  设置标题内容
     */
    public void setTitle(String title){
        tv_title.setText(title);
    }

    /**
     * @param des  设置描述内容
     */
    public void setDes(String des){
        tv_des.setText(des);
    }
}
