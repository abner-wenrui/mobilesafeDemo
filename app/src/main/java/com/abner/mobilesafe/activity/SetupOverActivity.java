package com.abner.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;

public class SetupOverActivity extends AppCompatActivity {

    private TextView safe_number;
    private TextView tv_reset_setup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean setup_over = SpUtil.getBoolean(this, ConstantValue.SETUP_OVER, false);
        if (setup_over) {
            //密码输入成功，并且4个导航界面设置完成-----》停留在设置完成功能列表界面
            setContentView(R.layout.activity_setup_over);


            initUI();



        }else {
            //密码输入成功，四个导航界面没有设置完成----》跳转到导航界面第一个
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
            //开启了一个界面之后，关闭功能列表界面
            finish();
        }
    }

    private void initUI() {
        safe_number = (TextView) findViewById(R.id.safe_number);
        tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);

        String contact_phone = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        safe_number.setText(contact_phone);

        tv_reset_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Setup1Activity.class);
                startActivity(intent);
            }
        });

    }
}
