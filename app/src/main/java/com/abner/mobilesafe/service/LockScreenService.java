package com.abner.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.abner.mobilesafe.engine.ProcessInfoProvider;

public class LockScreenService extends Service {

    private IntentFilter mIntentFilter;
    private InnerReceiver mInnerReceiver;

    public LockScreenService() {
    }

    @Override
    public void onCreate() {
        mIntentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,mIntentFilter);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mInnerReceiver != null){
            unregisterReceiver(mInnerReceiver);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class InnerReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            ProcessInfoProvider.killAll(context);
        }
    }
}
