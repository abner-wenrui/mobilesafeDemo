package com.abner.app2;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tv_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_location = (TextView) findViewById(R.id.location);
        //获取经纬度坐标(LocationManager)

        //1,获取位置管理者对象
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //2,通过lm获取经纬度坐标(定位方式,minTime获取经纬度坐标的最小间隔时间,minDistance移动最小间距,)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //gps状态发生切换的事件监听
            }

            @Override
            public void onProviderEnabled(String provider) {
                //GPS开启的时候的事件监听
            }

            @Override
            public void onProviderDisabled(String provider) {
                //GPS关闭的时候的事件监听

            }

            @Override
            public void onLocationChanged(Location location) {
                //经度
                double longitude = location.getLongitude();
                //纬度
                double latitude = location.getLatitude();

                //获取经纬度需要添加权限
                tv_location.setText("longitude = " + longitude + ",latitude = " + latitude);
            }
        });
    }
}
