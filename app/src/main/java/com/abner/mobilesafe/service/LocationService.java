package com.abner.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //获取手机的经纬度坐标
        //1.获取位置管理者对象
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        //2.以最优的方式获取坐标
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = lm.getBestProvider(criteria, true);

        lm.requestLocationUpdates(bestProvider, 0, 1, new MyLocationListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //经度
            double longitude = location.getLongitude();
            //纬度
            double latitude = location.getLatitude();

            SmsManager sm = SmsManager.getDefault();

            String safe_number = SpUtil.getString(getApplicationContext(), ConstantValue.CONTACT_PHONE, "");
            sm.sendTextMessage(safe_number, null, "longitude!!!"+location+"latidute:"+latitude, null, null);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
