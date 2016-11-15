package com.abner.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.engine.SmsBackUp;

import java.io.File;

public class AToolActivity extends AppCompatActivity {

    private ProgressBar pb_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);


        //电话归属地查询的方法
        initPhoneAddress();
        //备份短信
        initSmsBackUp();
        //常用号码查询
        initCommonNumberQuery();
    }

    private void initCommonNumberQuery() {
        TextView tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
        tv_commonnumber_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
            }
        });
    }

    //短信备份
    private void initSmsBackUp() {
        TextView tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackUpDialog();
            }
        });
    }


    private void showSmsBackUpDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("短信备份");
        //指定进度条样式为水平
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //展示进度条进度
        progressDialog.show();
        //短信获取
        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms74.xml";
                SmsBackUp.buckUp(getApplicationContext(), path, new SmsBackUp.CallBack() {
                    //又开发者决定用对话框还是进度条
                    @Override
                    public void setMax(int max) {
                        progressDialog.setMax(max);
                        pb_bar.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        pb_bar.setProgress(index);
                        progressDialog.setProgress(index);
                    }
                });
            }
        }).start();
        progressDialog.dismiss();
    }

    private void initPhoneAddress() {
        TextView tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QueryAddressActivity.class);
                startActivity(intent);
            }
        });
    }
}
