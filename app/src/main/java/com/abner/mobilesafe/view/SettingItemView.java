package com.abner.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abner.mobilesafe.R;

/**
 * Project   com.abner.mobilesafe.view
 *
 * @Author Abner
 * Time   2016/8/26.16:36
 */
public class SettingItemView extends RelativeLayout {

    private CheckBox cb_box;
    private TextView mTv_des;
    private static final String tag = "SettingItemView";
    private static final String NAME_SPACE = "http://schemas.android.com/apk/res/com.abner.mobilesafe";
    private String mDestitle;
    private String mDesoff;
    private String mDeson;

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //xml --> view  将设置界面的一个条目转换成view对象
        View.inflate(getContext(), R.layout.setting_item_view, this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        mTv_des = (TextView) findViewById(R.id.tv_des);
        cb_box = (CheckBox) findViewById(R.id.cb_box);

        //获取自定义以及原生属性的操作，写在此处，AttributeSet attrs对象中获取

        initAttrs(attrs);
        tv_title.setText(mDestitle);
    }

    /**
     * @param attrs 构造方法中维护好的属性集合
     *              返回属性集合中自定义属性属性值
     */

    private void initAttrs(AttributeSet attrs) {
        //       Log.i(tag,"attrs.getAttributeCount()"+attrs.getAttributeCount());

        //获取属性名称以及属性值
        /*for (int i = 0; i<attrs.getAttributeCount(); i++){
            Log.i(tag,"Name:"+attrs.getAttributeName(i));
            Log.i(tag,"Value"+attrs.getAttributeValue(i));
            Log.i(tag,"=================================");
        }*/
        //通过名空间+属性名称获取属性值
        mDestitle = attrs.getAttributeValue(NAME_SPACE, "destitle");
        mDesoff = attrs.getAttributeValue(NAME_SPACE, "desoff");
        mDeson = attrs.getAttributeValue(NAME_SPACE, "deson");

    }

    /**
     * 判断是否开启的方法
     *
     * @return 返回当前SettingItemView是否是选中状态，true开启  false关闭
     */
    public boolean isCheck() {
        //有checkbox的选中结果，决定当前条目是否开启
        return cb_box.isChecked();
    }

    /**
     * @param isCheck 是否作为开启的变量，由点击过程中去做传递
     */
    public void setCheck(boolean isCheck) {
        //当前的条目在选择过程中，cb_box的选中状态也跟随（isCheck）变化
        cb_box.setChecked(isCheck);
        if (isCheck) {
            //开启
            mTv_des.setText(mDeson);
        } else {
            //开启
            mTv_des.setText(mDesoff);
        }
    }

    public SettingItemView(Context context) {
        this(context, null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

}
