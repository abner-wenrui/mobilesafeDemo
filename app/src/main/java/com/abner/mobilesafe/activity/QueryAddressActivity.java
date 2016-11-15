package com.abner.mobilesafe.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.engine.AddressDao;

public class QueryAddressActivity extends AppCompatActivity {

    private TextView tv_query_result;
    private String mAddress;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.i(tag,"222222222222223333"+mAddress);
            tv_query_result.setText(mAddress);
        }
    };
    private String tag = "QueryAddressActivity";
    private EditText et_phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_address);

        initUI();
    }

    private void initUI() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        Button btn_query = (Button) findViewById(R.id.btn_query);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);

        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)){
                    query(phone);
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                    et_phone.startAnimation(animation);

                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(2000);

                }
            }
        });
        //实时查询（坚挺输入框文本变换）
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String phone = et_phone.getText().toString().trim();
                query(phone);
            }
        });
    }


    /**
     * 查询电话号码
     * @param phone 电话号码
     */
    private void query(final String phone) {
        new Thread(){
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(phone);
                //消息机制，告知主线程查询结束，可以去使用查询结果
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }
}
