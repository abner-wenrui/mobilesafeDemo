package com.abner.mobilesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getRawX() - e2.getRawX() > 10) {
                    //下一页
                    showNextPage();
                }
                if (e1.getRawX() - e2.getRawX() < 100) {
                    //上一页
                    showPrePage();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

    }

    //抽象方法，定义跳转到下一页的方法
    public abstract void showNextPage();
    //定义到上一页的方法
    public abstract void showPrePage();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    //统一处理每一个界面中的上一页下一页按钮
    public void nextPage(View view){
        showNextPage();
    }
    public void prePage(View view){
        showPrePage();
    }
}