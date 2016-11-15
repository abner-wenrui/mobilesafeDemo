package com.abner.mobilesafe.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.db.domain.AppInfo;
import com.abner.mobilesafe.engine.AppInfoProvider;
import com.abner.mobilesafe.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity implements View.OnClickListener {


    private List<AppInfo> mAppInfoList;
    private ListView lv_app_list;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MyAdapter myAdapter = new MyAdapter();
            lv_app_list.setAdapter(myAdapter);
            if (tv_des != null && mCustomerInfoList != null) {
                tv_des.setText("用户应用");
            }
        }
    };
    private ArrayList<AppInfo> mCustomerInfoList;
    private ArrayList<AppInfo> mSystemInfoList;
    private String tag = "AppManagerActivity";
    private TextView tv_des;
    private AppInfo mAppInfo;
    private PopupWindow mPopupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        initTitle();
        initList();
    }

    private void initList() {
        lv_app_list = (ListView) findViewById(R.id.lv_app_list);
        tv_des = (TextView) findViewById(R.id.tv_des);
        new Thread() {
            @Override
            public void run() {
                mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
                mCustomerInfoList = new ArrayList<>();
                mSystemInfoList = new ArrayList<>();
                for (AppInfo appInfo : mAppInfoList) {
                    if (appInfo.isSystem) {
                        mSystemInfoList.add(appInfo);
                    } else {
                        mCustomerInfoList.add(appInfo);
                    }
                }
                mHandler.sendEmptyMessage(0);
            }
        }.start();
        lv_app_list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mCustomerInfoList != null && mSystemInfoList != null) {
                    if (firstVisibleItem >= mCustomerInfoList.size() + 1) {
                        //滚动到了系统条目
                        tv_des.setText("系统应用");
                    } else {
                        tv_des.setText("用户应用");
                    }
                }
            }
        });

        lv_app_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0 || position == mCustomerInfoList.size() + 1) {
                    return;
                } else {
                    if (position < mCustomerInfoList.size() + 1) {
                        mAppInfo = mCustomerInfoList.get(position - 1);
                    } else {
                        mAppInfo = mSystemInfoList.get(position - mCustomerInfoList.size() - 2);
                    }
                    showPopupWindow(view);
                }
            }
        });
    }

    private void showPopupWindow(View view) {
        View popupView = View.inflate(this, R.layout.popupwindow_layout, null);
        TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
        TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
        TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);

        tv_uninstall.setOnClickListener(this);
        tv_start.setOnClickListener(this);
        tv_share.setOnClickListener(this);
        //透明动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setFillAfter(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(500);
        scaleAnimation.setFillAfter(true);
        //动画集合set
        AnimationSet animationSet = new AnimationSet(true);
        //添加两个动画
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(scaleAnimation);

        popupView.startAnimation(animationSet);

        //创建窗体对象，指定宽高
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        //设置一个透明背景
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        //3指定窗体的位置
        mPopupWindow.showAsDropDown(view, 150, -view.getHeight());
    }

    private void initTitle() {
        //1.获取磁盘可用空间,磁盘路径
        String path = Environment.getDataDirectory().getAbsolutePath();
        //2.获取sd卡可用空间
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //3.获取以上两个路径下文件夹的可用时间
        String memoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(path));
        String sdMemoryAvailSpace = Formatter.formatFileSize(this, getAvailSpace(sdPath));

        TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
        TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
        tv_memory.setText("磁盘可用空间:" + memoryAvailSpace);
        tv_sd_memory.setText("sd可用空间:" + sdMemoryAvailSpace);
    }

    private long getAvailSpace(String path) {
        //获取可用磁盘大小类
        StatFs statFs = new StatFs(path);
        //获取可用区块的个数
        long count = statFs.getAvailableBlocks();
        //获取区块的大小
        long size = statFs.getBlockSize();
        return count * size;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_uninstall:
                if (mAppInfo.isSystem()){
                    ToastUtil.show(getApplicationContext(),"此应用不能卸载");
                }else {
                    Intent intent = new Intent("android.intent.action.DELETE");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                    startActivity(intent);
                }
                break;
            case R.id.tv_start:
                PackageManager PM = getPackageManager();
                Intent launchIntentForPackage = PM.getLaunchIntentForPackage(mAppInfo.getPackageName());
                if (launchIntentForPackage == null){
                    ToastUtil.show(getApplicationContext(),"此应用不能被开启");
                }else {
                    startActivity(launchIntentForPackage);
                }
                break;
            case R.id.tv_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"分享一个应用，应用名称为:"+mAppInfo.getName());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }
        if (mPopupWindow != null){
            mPopupWindow.dismiss();
        }
    }

    private class MyAdapter extends BaseAdapter {
        //获取数据适配器中条目类型的总数，修改成两种（纯文本，图片+文字）
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        //指定索引指向的条目类型，条目类型状态码指定（0，（复用系统），1）
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mCustomerInfoList.size() + 1) {
                //返回0，代表纯文本的状态码
                return 0;
            } else {
                //返回1代表图片+文本的状态码
                return 1;
            }
        }

        //添加两个描述条目
        @Override
        public int getCount() {
            return mCustomerInfoList.size() + mSystemInfoList.size() + 2;
        }

        @Override
        public AppInfo getItem(int position) {
            if (position == 0 || position == mCustomerInfoList.size() + 1) {
                return null;
            } else {
                if (position < mCustomerInfoList.size() + 1) {
                    return mCustomerInfoList.get(position - 1);
                } else {
                    //返回系统应用对应条目的对象
                    return mSystemInfoList.get(position - mCustomerInfoList.size() - 2);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (getItemViewType(position) == 0) {
                //返回纯文本条目
                ViewTitleHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
                    holder = new ViewTitleHolder();
                    holder.tv_title = ((TextView) convertView.findViewById(R.id.tv_title));
                    convertView.setTag(holder);
                } else {
                    holder = ((ViewTitleHolder) convertView.getTag());
                }
                if (position == 0) {
                    holder.tv_title.setText("手机应用(" + mCustomerInfoList.size() + ")");
                } else {
                    holder.tv_title.setText("系统应用(" + mSystemInfoList.size() + ")");
                }

                return convertView;
            } else {
                //展示图片＋文字条目
                ViewHolder holder = null;
                if (convertView == null) {
                    convertView = View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
                    holder = new ViewHolder();
                    holder.iv_icon = ((ImageView) convertView.findViewById(R.id.iv_icon));
                    holder.tv_name = ((TextView) convertView.findViewById(R.id.tv_name));
                    holder.tv_path = ((TextView) convertView.findViewById(R.id.tv_path));
                    convertView.setTag(holder);
                } else {
                    holder = ((ViewHolder) convertView.getTag());
                }
                holder.iv_icon.setBackground(getItem(position).getIcon());
                holder.tv_name.setText(getItem(position).getName());
                if (getItem(position).isSystem) {
                    holder.tv_path.setText("手机内存");
                } else {
                    holder.tv_path.setText("外部存储");
                }
                return convertView;
            }
        }
    }

    private static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_path;
    }

    private static class ViewTitleHolder {
        TextView tv_title;
    }
}
