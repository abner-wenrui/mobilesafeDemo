package com.abner.mobilesafe.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.db.domain.ProcessInfo;
import com.abner.mobilesafe.engine.ProcessInfoProvider;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;
import com.abner.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ProcessManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_process_count;
    private TextView tv_memory_info;
    private ListView lv_process_list;
    private Button btn_all;
    private Button btn_reverse;
    private Button btn_clean;
    private Button btn_setting;
    private int mProcessCount;
    private List<ProcessInfo> mProcessInfoList;
    private ArrayList<ProcessInfo> mSystemProcessList;
    private ArrayList<ProcessInfo> mCustemerProcessList;
    private MyAdapter mMyAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mMyAdapter = new MyAdapter();
            lv_process_list.setAdapter(mMyAdapter);
            if (tv_des != null && mCustemerProcessList != null) {
                String title = String.format("用户进程(" + mCustemerProcessList.size() + ")");
                tv_des.setText(title);
            }
        }
    };
    private String tag = "ProcessManagerActivity";
    private TextView tv_des;
    private ProcessInfo mProcessInfo;
    private String mStrTitalSpace;
    private long mAvailSpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_manager);

        initUI();
        initTitleData();
        initListData();
    }

    private void initListData() {
        getData();
    }

    private void getData() {
        new Thread() {
            @Override
            public void run() {
                mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
                mSystemProcessList = new ArrayList<ProcessInfo>();
                mCustemerProcessList = new ArrayList<ProcessInfo>();
                for (ProcessInfo info : mProcessInfoList) {
                    if (info.isSystem()) {
                        mSystemProcessList.add(info);
                    } else {
                        mCustemerProcessList.add(info);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initTitleData() {
        mProcessCount = ProcessInfoProvider.getProcessCont(this);
        tv_process_count.setText("进程总数:" + mProcessCount);
        mAvailSpace = ProcessInfoProvider.getAvailSpace(this);
        String strAvailSpace = Formatter.formatFileSize(this, mAvailSpace);
        long totalSpace = ProcessInfoProvider.getTotalSpace(this);
        mStrTitalSpace = Formatter.formatFileSize(this, totalSpace);
        String title = String.format(("剩余/可用  " + strAvailSpace + "/" + mStrTitalSpace));
        tv_memory_info.setText(title);
    }

    private void initUI() {
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);

        lv_process_list = (ListView) findViewById(R.id.lv_process_list);
        lv_process_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustemerProcessList != null && mSystemProcessList != null) {
                    if (firstVisibleItem > mCustemerProcessList.size() + 1) {
                        String title = String.format("系统进程(" + mCustemerProcessList.size() + ")");
                        tv_des.setText(title);
                    } else {
                        String title = String.format("用户进程(" + mCustemerProcessList.size() + ")");
                        tv_des.setText(title);
                    }
                }
            }
        });

        lv_process_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustemerProcessList.size() + 1) {
                    return;
                } else {
                    if (position < mCustemerProcessList.size() + 1) {
                        mProcessInfo = mCustemerProcessList.get(position - 1);
                    } else {
                        mProcessInfo = mSystemProcessList.get(position - 2 - mCustemerProcessList.size());
                    }
                    if (mProcessInfo != null) {
                        if (!mProcessInfo.getPackageName().equals(getPackageName())) {
                            mProcessInfo.setCheck(!mProcessInfo.isCheck());
                            CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
                            cb_box.setChecked(mProcessInfo.isCheck());
                        }
                    }
                }
            }
        });

        btn_all = (Button) findViewById(R.id.btn_all);
        btn_reverse = (Button) findViewById(R.id.btn_reverse);
        btn_clean = (Button) findViewById(R.id.btn_clear);
        btn_setting = ((Button) findViewById(R.id.btn_setting));
        tv_des = (TextView) findViewById(R.id.tv_des);


        btn_all.setOnClickListener(this);
        btn_reverse.setOnClickListener(this);
        btn_clean.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_all:
                selectAll();
                break;
            case R.id.btn_reverse:
                reverseAll();
                break;
            case R.id.btn_clear:
                clearAll();
                break;
            case R.id.btn_setting:
                setting();
                break;
        }
    }

    private void setting() {
        startActivityForResult(new Intent(this, ProcessSettingActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mMyAdapter != null) {
            mMyAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void clearAll() {
        ArrayList<ProcessInfo> killProcessList = new ArrayList<>();
        for (ProcessInfo processInfo : mCustemerProcessList) {
            if (processInfo.getPackageName().equals(getPackageName()))
                continue;
            if (processInfo.isCheck())
                killProcessList.add(processInfo);
        }
        for (ProcessInfo processInfo : mSystemProcessList) {
            if (processInfo.isCheck())
                killProcessList.add(processInfo);
        }
        long totalReleaseSpace = 0;
        for (ProcessInfo processInfo : killProcessList) {
            if (mCustemerProcessList.contains(processInfo)) {
                mCustemerProcessList.remove(processInfo);
            }
            if (mSystemProcessList.contains(processInfo)) {
                mSystemProcessList.remove(processInfo);
            }
            ProcessInfoProvider.killProcess(this, processInfo);
            totalReleaseSpace += processInfo.getMemSize();
        }
        mProcessCount -= killProcessList.size();
        tv_process_count.setText("进程总数:" + mProcessCount);
        tv_memory_info.setText("剩余/可用  " + Formatter.formatFileSize(this, (mAvailSpace + totalReleaseSpace)) + "/" + mStrTitalSpace);
        //  String title = String.format(("杀死了" + killProcessList.size() + "个进程，释放了" + Formatter.formatFileSize(this, totalReleaseSpace) + "空间"));
        String title = String.format("杀死了%d个进程，释放了%s空间", killProcessList.size(), Formatter.formatFileSize(this, totalReleaseSpace));
        ToastUtil.show(this, title);

        if (mMyAdapter != null) {
            mMyAdapter.notifyDataSetChanged();
        }
    }

    private void reverseAll() {
        for (ProcessInfo processInfo : mCustemerProcessList) {
            if (processInfo.getPackageName().equals(getPackageName()))
                continue;
            processInfo.setCheck(!processInfo.isCheck());
        }
        for (ProcessInfo processInfo : mSystemProcessList) {
            processInfo.setCheck(!processInfo.isCheck());
        }
        if (mMyAdapter != null) {
            mMyAdapter.notifyDataSetChanged();
        }
    }

    private void selectAll() {
        //1.将所有的集合中的对象上isChick字段设置为true，代表全选，排除当前应用
        for (ProcessInfo processInfo : mCustemerProcessList) {
            if (processInfo.getPackageName().equals(getPackageName()))
                continue;
            processInfo.setCheck(true);
        }
        for (ProcessInfo processInfo : mSystemProcessList) {
            processInfo.setCheck(true);
        }
        if (mMyAdapter != null) {
            mMyAdapter.notifyDataSetChanged();
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustemerProcessList.size() + 1) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getCount() {
            if (SpUtil.getBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM, false)) {
                return mCustemerProcessList.size() + mSystemProcessList.size() + 2;
            } else {
                return mCustemerProcessList.size() + 1;
            }
        }

        @Override
        public ProcessInfo getItem(int position) {
            if (position == 0 || position == mCustemerProcessList.size() + 1) {
                return null;
            } else {
                if (position < mCustemerProcessList.size() + 1) {
                    return mCustemerProcessList.get(position - 1);
                } else {
                    return mSystemProcessList.get(position - mCustemerProcessList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                //展示灰色纯文本条目
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                    convertView.setTag(holder);
                } else {
                    holder = ((ViewTitleHolder) convertView.getTag());
                }
                if (position == 0) {
                    String title = String.format("用户进程(" + mCustemerProcessList.size() + ")");
                    holder.tv_title.setText(title);
                } else {
                    String title = String.format("系统进程(" + mSystemProcessList.size() + ")");
                    holder.tv_title.setText(title);
                }
                return convertView;
            } else {
                //展示图片+文本
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = ((ImageView) convertView.findViewById(R.id.iv_icon));
                    holder.tv_name = ((TextView) convertView.findViewById(R.id.tv_name));
                    holder.tv_memory_info = ((TextView) convertView.findViewById(R.id.tv_memory_info));
                    holder.cb_box = ((CheckBox) convertView.findViewById(R.id.cb_box));
                    convertView.setTag(holder);
                } else {
                    holder = ((ViewHolder) convertView.getTag());
                }
                holder.iv_icon.setBackgroundDrawable(getItem(position).getIcon());
                holder.tv_name.setText(getItem(position).getName());
                String memSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).getMemSize());
                holder.tv_memory_info.setText(memSize);
                if (getItem(position).packageName.equals(getPackageName())) {
                    holder.cb_box.setVisibility(View.GONE);
                } else {
                    holder.cb_box.setVisibility(View.VISIBLE);
                }
                holder.cb_box.setChecked(getItem(position).isCheck());
                return convertView;
            }
        }
    }

    private static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memory_info;
        CheckBox cb_box;
    }

    private static class ViewTitleHolder {
        TextView tv_title;
    }
}
