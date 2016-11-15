package com.abner.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.abner.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BlackNumberService extends Service {

    private InnerSmsReceive mInnerSmsReceive;
    private TelephonyManager mTM;
    private MyPhoneStateListener mMyPhoneStateListener;
    private BlackNumberDao mDao;

    public BlackNumberService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        //拦截短信
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provide.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);
        mInnerSmsReceive = new InnerSmsReceive();
        registerReceiver(mInnerSmsReceive,intentFilter);


        //监听电话的状态
        //1.电话管理者对象
        mTM = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE));
        //2.监听电话的状态
        mMyPhoneStateListener = new MyPhoneStateListener();

       mTM.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mInnerSmsReceive != null){
            unregisterReceiver(mInnerSmsReceive);
        }
        super.onDestroy();
    }

    private class InnerSmsReceive extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信的内容，获取发送短信的电话号码，如果此号码在黑名单中且拦截模式为1或者3的话进行拦截
            //1.获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //2.循环遍历短信的过程
            for (Object object : objects) {
                //获取短信对象

            }
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    break;
            }
            //挂断电话放到aidl中去了
            endCall(incomingNumber);
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void endCall(String phone) {
        mDao = BlackNumberDao.getInstance(getApplicationContext());
        int mode = mDao.getMode(phone);
        if (mode == 2 || mode == 3){
            //拦截电话
      //      ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
            //ServiceManage此类对android开发者隐藏，所以需要反射调用

            try {
                //1.获取ServiceManage的字节码
                Class<?> clazz = Class.forName("android.os.ServiceManager");
                //2.获取方法
                Method clazzMethod = clazz.getMethod("getService", String.class);
                //3.反射调用此方法
                IBinder iBinder = (IBinder) clazzMethod.invoke(null, Context.TELEPHONY_SERVICE);
                //4.调用获取aidl文件对象方法

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //       ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
    }

}