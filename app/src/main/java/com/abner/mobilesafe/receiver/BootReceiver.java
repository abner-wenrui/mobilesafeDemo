package com.abner.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;

public class BootReceiver extends BroadcastReceiver {
    private String tag = "BootReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        //一旦监听到开机广播，就需要发送短信给指定号码

        String sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();

        if (!simSerialNumber.equals(sim_number)) {
 //           SmsManager sm = SmsManager.getDefault();

//            String safe_number = SpUtil.getString(context, ConstantValue.CONTACT_PHONE, "");
//            sm.sendTextMessage(safe_number, null, "sim Change!!!", null, null);
        }

        Log.i(tag, "接收到了开关机广播，可在此处做sim卡是否切换的判断..........");

    }
}
