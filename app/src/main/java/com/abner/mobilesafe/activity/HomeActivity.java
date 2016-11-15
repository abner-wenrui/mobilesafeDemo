package com.abner.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.Md5Util;
import com.abner.mobilesafe.utils.SpUtil;
import com.abner.mobilesafe.utils.ToastUtil;

public class HomeActivity extends AppCompatActivity {

    private GridView mGv_home;
    private int[] mMipmapIds;
    private String[] mTitleStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();

        //初始化数据方法
        initDate();
    }

    private void initDate() {
        //准备数据（图片，文字）
        mTitleStr = new String[]{
                "手机防盗", "通信卫士", "软件管理", "进程管理", "流量统计", "软件杀毒", "缓存清理", "高级工具", "设置中心"
        };

        mMipmapIds = new int[]{
                R.mipmap.home_safe, R.mipmap.home_callmsgsafe,
                R.mipmap.home_apps, R.mipmap.home_taskmanager,
                R.mipmap.home_netmanager, R.mipmap.home_trojan,
                R.mipmap.home_sysoptimize, R.mipmap.home_tools, R.mipmap.home_settings
        };
        //九宫格空间设置数据适配器（和ListView数据适配器差不多）
        mGv_home.setAdapter(new MyAdapter());
        mGv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //开启对话框
                        showDialog();
                        break;
                    case 1:
                        Intent intent1 = new Intent(getApplicationContext(), BlackNumberActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(getApplicationContext(), AppManagerActivity.class);
                        startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(getApplicationContext(), ProcessManagerActivity.class);
                        startActivity(intent3);
                        break;
                    case 7:
                        Intent intent7 = new Intent(getApplicationContext(), AToolActivity.class);
                        startActivity(intent7);
                        break;

                    case 8:
                        Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void showDialog() {
        //判断本地是否存储密码（sp）
        String psd = SpUtil.getString(this, ConstantValue.MOBILE_SAFE_PSD, "");
        if (TextUtils.isEmpty(psd)) {
            //1.初始设置密码对话框
            showSetPsdDialog();
        } else {
            //2.确认密码对话框
            showConfirmPsdDialog();
        }
    }

    /**
     * 确认密码的对话框
     */
    private void showConfirmPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
        dialog.setView(view,0,0,0,0);
        dialog.show();

        Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_input_psd = (EditText) view.findViewById(R.id.et_input_psd);
                String input_psd = et_input_psd.getText().toString();
                if (!TextUtils.isEmpty(input_psd)){
                    String psd = SpUtil.getString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, "");
                    if (psd.equals(Md5Util.encode(input_psd))){
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }else {
                        ToastUtil.show(getApplicationContext(),"密码不正确");
                    }
                }else {
                    ToastUtil.show(getApplicationContext(),"请输入密码");

                }
            }
        });
    }

    /**
     * 设置密码的对话框
     */
    private void showSetPsdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        final View view = View.inflate(this, R.layout.dialog_set_psd, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();

        Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
                EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
                String psd = et_set_psd.getText().toString();
                String confirmPsd = et_confirm_psd.getText().toString();

                if (!TextUtils.isEmpty(psd) && !TextUtils.isEmpty(confirmPsd)) {
                    if (psd.equals(confirmPsd)) {
                        Intent intent = new Intent(getApplicationContext(), SetupOverActivity.class);
                        startActivity(intent);
                        dialog.dismiss();

                        SpUtil.putString(getApplicationContext(), ConstantValue.MOBILE_SAFE_PSD, Md5Util.encode(psd));

                    } else {
                        ToastUtil.show(getApplicationContext(), "两次输入密码不一致");
                    }
                } else {
                    ToastUtil.show(getApplicationContext(), "请输入密码");
                }
            }
        });

    }

    private void initUI() {
        mGv_home = (GridView) findViewById(R.id.gv_home);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mTitleStr.length;
        }

        @Override
        public Object getItem(int position) {
            return mTitleStr[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_titile);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_title.setText(mTitleStr[position]);
            iv_icon.setBackgroundResource(mMipmapIds[position]);
            return view;
        }
    }
}
