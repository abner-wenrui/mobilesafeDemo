package com.abner.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Project   com.abner.mobilesafe.view
 * 能够获取焦点的自定义TextView
 * @Author Abner
 * Time   2016/8/25.22:46
 */
public class FocusTextView extends TextView {

    //使用在通过java代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }
    //由系统调用（带属性，+上下文方法）
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //由系统调用（带属性+上下文环境+布局文件中定义样式文件构造方法）
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //重写获取焦点的方法，由系统调用调用的时候就能默认获取焦点
    @Override
    public boolean isFocused() {
        return true;
    }
}
