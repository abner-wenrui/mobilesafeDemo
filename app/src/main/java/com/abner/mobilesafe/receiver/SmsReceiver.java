package com.abner.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.service.LocationService;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");

            for (Object object : pdus) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                if (messageBody.contains("#*alarm*#")){
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if (messageBody.contains("#*location*#")){
                    Intent intent1 = new Intent(context, LocationService.class);
                    context.startService(intent1);
                }
            }
        }
    }
}
