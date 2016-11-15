package com.abner.mobilesafe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;
import com.abner.mobilesafe.utils.ToastUtil;
import com.abner.mobilesafe.view.SettingItemView;

public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView mSiv_sim_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        initUI();

    }

    @Override
    public void showNextPage() {
        String serialNumber = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        if (!TextUtils.isEmpty(serialNumber)) {
            Intent intent = new Intent(this, Setup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.next_in_anim,R.anim.next_out_anim);

        }else {
            ToastUtil.show(this,"请绑定sim卡");
        }
    }

    @Override
    public void showPrePage() {
        Intent intent = new Intent(this, Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_in_anim,R.anim.pre_out_anim);
    }

    private void initUI() {
        mSiv_sim_bound = (SettingItemView) findViewById(R.id.siv_sim_bound);
        //1.回显（读取已有的绑定状态，用作显示，sp中是否存储了sim卡的序列号）
        String sim_number = SpUtil.getString(this, ConstantValue.SIM_NUMBER, "");
        //2.判断sim序列卡号是否为空
        if (TextUtils.isEmpty(sim_number)) {
            mSiv_sim_bound.setCheck(false);
        } else {
            mSiv_sim_bound.setCheck(true);
        }
        mSiv_sim_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //3.获取原有状态
                boolean isChick = mSiv_sim_bound.isCheck();
                //4.将原有状态取反
                //5.状态设置给当前条目
                mSiv_sim_bound.setCheck(!isChick);
                if (!isChick) {
                    //6.存储序列卡号
                    //6.1获取sim卡序列号TelephoneyManager
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    //6.2获取sim卡的序列卡号
                    String simSerialNumber = telephonyManager.getSimSerialNumber();
                    //6.3存储
                    SpUtil.putString(getApplicationContext(),ConstantValue.SIM_NUMBER,simSerialNumber);
                } else {
                    //7.将存储序列卡号的结点，从sp中删除掉
                    SpUtil.remove(getApplicationContext(),ConstantValue.SIM_NUMBER);
                }
            }
        });
    }

}
