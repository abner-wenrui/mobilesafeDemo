package com.abner.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.engine.AddressDao;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;

public class AddressService extends Service {

    private TelephonyManager mTelephonyManager;
    private MyPhoneStateListener mMyPhoneStateListener;
    private String tag = "AddressService";
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private View mViewToast;
    private static String mAddress;

    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            tv_toast.setText(mAddress);
        }
    };
    private static TextView tv_toast;
    private int[] mDrawableIds;
    private int mStartX;
    private int mStartY;
    private WindowManager mWM;
    private int mWidth;
    private int mHeight;
    private InnerOutCallReceiver mInnerOutCallReceiver;

    @Override
    public void onCreate() {
        //服务一旦开启，就需要管理Toast的显示
        //电话状态的监听

        //1,电话管理者对象
        mTelephonyManager = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        //2.监听电话状态
        mMyPhoneStateListener = new MyPhoneStateListener();
        mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        //3.获取窗体对象
        mWM = ((WindowManager) getSystemService(WINDOW_SERVICE));

        mWidth = mWM.getDefaultDisplay().getWidth();
        mHeight = mWM.getDefaultDisplay().getHeight();

        //拨出电话广播过滤条件
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        //创建广播接收者
        mInnerOutCallReceiver = new InnerOutCallReceiver();
        registerReceiver(mInnerOutCallReceiver,intentFilter);
        super.onCreate();
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态没有任何活动（移除Toast）
                    Log.i(tag, "空闲了。。。");
                    //挂断电话时从窗体上移除toast
                    if (mWM != null && mViewToast != null) {
                        mWM.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态至少有个电话活动。该活动或是拨打或是通话
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //响铃（展示Toast）
                    Log.i(tag, "响铃了。。。。");
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void showToast(String incomingNumber) {


        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;   //在响铃的时候显示吐司,和电话类型一至
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        params.gravity = Gravity.LEFT + Gravity.TOP;   //指定吐司的所在  位置

        //吐司的显示效果（Toast的布局文件），将Toast挂载在windowManager窗体上
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = ((TextView) mViewToast.findViewById(R.id.tv_toast));

        //使Toast可以在窗体上移动
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        mStartX = (int) event.getX();
                        mStartY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getX();
                        int endY = (int) event.getY();

                        int disX = endX - mStartX;
                        int disY = endY - mStartY;

                        params.x = params.x + disX;
                        params.y = params.y +disY;

                        mWM.updateViewLayout(mViewToast,params);

                        if (params.x<0 )
                            params.x = 0;
                        if (params.y<0)
                            params.y = 0;
                        if ((params.x+mViewToast.getWidth())>mWidth)
                            params.x = mWidth-mViewToast.getWidth();
                        if ((params.y+mViewToast.getHeight()+50)>mHeight)
                            params.y = mHeight-50-mViewToast.getHeight();

                        mStartX = endX;
                        mStartY = endY;

                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtil.putInt(getApplicationContext(),ConstantValue.LOCATION_X,params.x);
                        SpUtil.putInt(getApplicationContext(),ConstantValue.LOCATION_Y,params.y);
                        break;
                }
                return false;
            }
        });

        //从sp中获取色值文字的索引，匹配图片，用作展示
        mDrawableIds = new int[]{R.mipmap.call_locate_white, R.mipmap.call_locate_orange,
                R.mipmap.call_locate_blue, R.mipmap.call_locate_gray,
                R.mipmap.call_locate_green};
        int toastStyleIndex = SpUtil.getInt(this, ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);

        int location_X = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_X, 0);
        int location_Y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCATION_Y, 0);

        params.x = location_X;
        params.y = location_Y;

        //在窗体上挂载一个view
        query(incomingNumber);
        mWM.addView(mViewToast, params);
    }

    private void query(final String incomingNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //销毁Toast 取消电话状态的监听（开启服务的时候监听电话的对象）
        if (mTelephonyManager != null && mMyPhoneStateListener != null) {
            mTelephonyManager.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (mInnerOutCallReceiver != null){
            unregisterReceiver(mInnerOutCallReceiver);
        }
        super.onDestroy();
    }


    private class InnerOutCallReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到此广播之后，显示自定义Toast，显示归属地号码
            //获取拨出号码的字符串
            showToast(getResultData());
        }
    }
}
