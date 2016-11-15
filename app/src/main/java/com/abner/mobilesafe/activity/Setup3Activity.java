package com.abner.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;
import com.abner.mobilesafe.utils.ToastUtil;

public class Setup3Activity extends BaseSetupActivity {

    private EditText mEt_phone_number;
    private Button btn_select_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);

        initUI();
    }

    @Override
    public void showNextPage() {
        String contact_number = mEt_phone_number.getText().toString();
        if (!TextUtils.isEmpty(contact_number)) {
            Intent intent = new Intent(this, Setup4Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);

            SpUtil.putString(this,ConstantValue.CONTACT_PHONE,contact_number);
        }else {
            ToastUtil.show(this,"请输入电话号码");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    private void initUI() {
        mEt_phone_number = (EditText) findViewById(R.id.et_phone_number);
        btn_select_number = (Button) findViewById(R.id.btn_select_number);

        String contact_number = SpUtil.getString(this, ConstantValue.CONTACT_PHONE, "");
        mEt_phone_number.setText(contact_number);

        btn_select_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ContactListActivity.class);
                startActivityForResult(intent,0);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null) {
            //返回当前界面的时候，接受结果的方法
            String number = data.getStringExtra("number");
            //将特殊字符过滤
            number = number.replace("-", "").replace(" ", "").trim();
            mEt_phone_number.setText(number);

  //          SpUtil.putString(getApplicationContext(), ConstantValue.CONTACT_PHONE, number);
        }



        super.onActivityResult(requestCode, resultCode, data);
    }

}
