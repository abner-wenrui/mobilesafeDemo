package com.abner.mobilesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;

public class ToastLocationActivity extends AppCompatActivity {

    private Button btn_top;
    private Button btn_bottom;
    private ImageView iv_drag;
    private int mStartX;
    private int mStartY;
    private WindowManager mWM;
    private int mWidth;
    private int mHeight;
    private long[] mHits = new long[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_location);

        initUI();
    }

    private void initUI() {
        btn_top = (Button) findViewById(R.id.btn_top);
        btn_bottom = (Button) findViewById(R.id.btn_bottom);
        iv_drag = (ImageView) findViewById(R.id.iv_drag);

        mWM = ((WindowManager) getSystemService(WINDOW_SERVICE));
        mWidth = mWM.getDefaultDisplay().getWidth();
        mHeight = mWM.getDefaultDisplay().getHeight();

        int location_X = SpUtil.getInt(this, ConstantValue.LOCATION_X, 0);
        int location_Y = SpUtil.getInt(this, ConstantValue.LOCATION_Y, 0);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = location_X;
        layoutParams.topMargin = location_Y;
        iv_drag.setLayoutParams(layoutParams);
        if (location_Y > mHeight / 2) {
            btn_bottom.setVisibility(View.INVISIBLE);
            btn_top.setVisibility(View.VISIBLE);
        }
        iv_drag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = System.currentTimeMillis();
                if (mHits[0] > (System.currentTimeMillis() - 500)) {
                    int left = mWidth / 2 - iv_drag.getWidth() / 2;
                    int right = mWidth / 2 + iv_drag.getWidth() / 2;
                    int top = mHeight / 2 - iv_drag.getHeight() / 2;
                    int bottom = mHeight / 2 + iv_drag.getHeight() / 2;
                    iv_drag.layout(left, top, right, bottom);

                    SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                    SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
                }
            }
        });

        iv_drag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) event.getX();
                        mStartY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getX();
                        int moveY = (int) event.getY();
                        int disX = moveX - mStartX;
                        int disY = moveY - mStartY;
                        mStartX = moveX;
                        mStartY = moveY;

                        int left = iv_drag.getLeft() + disX;
                        int right = iv_drag.getRight() + disX;
                        int top = iv_drag.getTop() + disY;
                        int bottom = iv_drag.getBottom() + disY;

                        if (left < 0)
                            return true;
                        if (right > mWidth)
                            return true;
                        if (top < 0)
                            return true;
                        if (bottom > mHeight - 62)
                            return true;

                        if (top > mHeight / 2) {
                            btn_bottom.setVisibility(View.INVISIBLE);
                            btn_top.setVisibility(View.VISIBLE);
                        } else {

                            btn_bottom.setVisibility(View.VISIBLE);
                            btn_top.setVisibility(View.INVISIBLE);
                        }

                        iv_drag.layout(left, top, right, bottom);

                        mStartX = (int) event.getX();
                        mStartY = (int) event.getY();

                        break;
                    case MotionEvent.ACTION_UP:
                        //存储移动到的位置
                        SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_X, iv_drag.getLeft());
                        SpUtil.putInt(getApplicationContext(), ConstantValue.LOCATION_Y, iv_drag.getTop());
                        break;
                }

                return false;
            }
        });

    }
}
