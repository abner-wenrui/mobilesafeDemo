package com.abner.mobilesafe.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.db.dao.BlackNumberDao;
import com.abner.mobilesafe.db.domain.BlackNumberInfo;
import com.abner.mobilesafe.utils.ToastUtil;

import java.util.List;
import java.util.Random;

public class BlackNumberActivity extends AppCompatActivity {

    private Button btn_add;
    private ListView lv_blacknumber;
    private BlackNumberDao mBlackNumberDao;
    private List<BlackNumberInfo> mBlackNumberList;

    private MyAdapter mMyAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mMyAdapter == null) {
                mMyAdapter = new MyAdapter();
                lv_blacknumber.setAdapter(mMyAdapter);
            } else {
                mMyAdapter.notifyDataSetChanged();
            }
        }
    };
    private int mode = 1;
    private boolean mIsload = false;
    private int mCounnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_number);

        initUI();
        initData();
    }

    private void initData() {
        //获取数据库中部分的号码
        new Thread() {
            @Override
            public void run() {
                mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
                mBlackNumberList = mBlackNumberDao.find(0);
                mCounnt = mBlackNumberDao.getCounnt();
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        btn_add = (Button) findViewById(R.id.btn_add);
        lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        lv_blacknumber.setOnScrollListener(new AbsListView.OnScrollListener() {
            //滚动过程中，状态发生改变调用的方法
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mBlackNumberList != null) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lv_blacknumber.getLastVisiblePosition() >= mBlackNumberList.size() - 1 && !mIsload) {
                        mIsload = true;
                        if (mCounnt > mBlackNumberList.size()) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mBlackNumberDao = BlackNumberDao.getInstance(getApplicationContext());
                                    List<BlackNumberInfo> moreData = mBlackNumberDao.find(mBlackNumberList.size());
                                    mBlackNumberList.addAll(moreData);
                                    mHandler.sendEmptyMessage(0);
                                }
                            }).start();
                        }
                        mIsload = false;
                    }
                }
            }

            //滚动过程中调用的方法
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        dialog.setView(view, 0, 0, 0, 0);

        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        Button btn_submit = (Button) view.findViewById(R.id.btn_submit);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);


        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.iv_sms:
                        mode = 1;
                        break;
                    case R.id.iv_phone:
                        mode = 2;
                        break;
                    case R.id.iv_all:
                        mode = 3;
                        break;
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_phone.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    //数据库插入当前拦截的号码
                    mBlackNumberDao.insert(phone, mode + "");

                    for (int i = 0; i < 100; i++) {
                        if (i < 10) {
                            mBlackNumberDao.insert("19836399000" + i, 1 + new Random().nextInt(3) + "");
                        } else {
                            mBlackNumberDao.insert("1983639900" + i, 1 + new Random().nextInt(3) + "");

                        }
                    }

                    //让数据库和集合保持同步（1重新读数据库 2手动向集合中添加一个对象（插入数据构建的对象））
                    BlackNumberInfo blackNumberInfo1 = new BlackNumberInfo();
                    blackNumberInfo1.setPhone(phone);
                    blackNumberInfo1.setMode(mode + "");
                    //将对象插入到集合的最顶部
                    mBlackNumberList.add(0, blackNumberInfo1);
                    //通知数据适配器刷新
                    if (mMyAdapter != null) {
                        mMyAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();

                } else {
                    ToastUtil.show(getApplicationContext(), "请输入要拦截的号码");
                }
            }
        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mBlackNumberList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlackNumberList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.list_view_blacknumber_item, null);
                holder = new ViewHolder();
                holder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
                holder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
                holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBlackNumberDao.delete(mBlackNumberList.get(position).getPhone());
                    mBlackNumberList.remove(position);
                    if (mMyAdapter != null)
                        mMyAdapter.notifyDataSetChanged();
                }
            });

            holder.tv_phone.setText(mBlackNumberList.get(position).getPhone());
            int mode = Integer.parseInt(mBlackNumberList.get(position).getMode());

            switch (mode) {
                case 1:
                    holder.tv_mode.setText("拦截短信");
                    break;
                case 2:
                    holder.tv_mode.setText("拦截电话");
                    break;
                case 3:
                    holder.tv_mode.setText("拦截所有");
                    break;
            }

            return convertView;
        }
    }

    private static class ViewHolder {
        public ImageView iv_delete;
        public TextView tv_phone;
        public TextView tv_mode;
    }
}
