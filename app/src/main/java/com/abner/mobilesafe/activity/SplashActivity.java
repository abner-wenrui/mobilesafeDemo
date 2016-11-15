package com.abner.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abner.mobilesafe.R;
import com.abner.mobilesafe.utils.ConstantValue;
import com.abner.mobilesafe.utils.SpUtil;
import com.abner.mobilesafe.utils.StreamUtils;
import com.abner.mobilesafe.utils.ToastUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class SplashActivity extends AppCompatActivity {

    private static final int URL_ERROR = 102;
    protected static final int UPDATE_VERSION = 100;
    private static final int IO_ERROR = 103;
    private static final int JSON_ERROR = 104;
    private static final int ENTER_HOME = 101;
    private TextView tv_version_name;
    private int mLocalVersionCode;
    private static final String tag = "SplashActivity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    //弹出对话框提示用户更新
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    //进入应用主界面，activity跳转过程
                    enterHome();
                    break;
                case URL_ERROR:
                    //url地址错误
                    ToastUtil.show(getApplicationContext(), "url异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(getApplicationContext(), "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(getApplicationContext(), "json解析异常");
                    enterHome();
                    break;
            }
        }
    };
    private String mVersionDescribe;
    private String mDownloadUrl;
    private RelativeLayout mRl_root;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

    /**
     * 弹出对话框提示用户更新
     */
    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置左上角图标
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        //设置描述内容
        builder.setMessage(mVersionDescribe);
        //积极按钮，立即更新
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk，apk连接地址，dowloadUrl
                downloadApk();
            }
        });
        //消极按钮，以后更新
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框进入主界面
                enterHome();
            }
        });
        //点击取消事件监听
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 下载apk
     */
    private void downloadApk() {
        //apk的下载链接地址，放置apk所在的路径

        //判断sd卡是否可用是否挂上
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //获取sd路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe74.apk";
            //发送请求获取apk并且放置到指定路径
            HttpUtils httpUtils = new HttpUtils();
            //发送请求传递参数
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    Log.i(tag, "下载成功");
                    File file = responseInfo.result;
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    Log.i(tag, "下载失败");
                }

                @Override
                public void onStart() {
                    //开始下载
                    Log.i(tag, "刚刚开始下载");
                    super.onStart();
                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    //下载中
                    Log.i(tag, "下载中.......");
                    Log.i(tag, "total = " + total);
                    Log.i(tag, "current = " + current);
                }
            });

        }
    }

    /**
     * 安装·APK
     *
     * @param
     */
    private void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);
    }

    //开启一个activity后，返回结果调用的方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 进入应用主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //开启一个新的界面后把导航界面关闭（导航界面只可见一次）
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始化UI
        initUI();
        //初始化数据
        initDate();
        //初始化动画
        initAnimation();
        //初始化数据库
        initDB();
        //生成快捷方式
        if (!SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)) {
            initShortCut();
        }
    }

    private void initShortCut() {
        //1.给intent维护图标，名称
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //维护图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, android.graphics.BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //维护名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马卫士");
        //2.点击快捷方式跳转到的activity
        Intent shortCutIntent = new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        //3.发送广播
        sendBroadcast(intent);
        SpUtil.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
    }

    private void initDB() {
        //归属地数据库拷贝过程
        initAddressDB("address.db");
        //常用号码数据库的copy过程
        initAddressDB("commonnum.db");
    }

    /**
     * 拷贝数据库至files文件夹下
     *
     * @param dbName 数据库名称
     */
    private void initAddressDB(String dbName) {
        //1.在files文件夹下创建同名dbName数据库文件过程
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()) {
            return;
        }

        InputStream stream = null;
        FileOutputStream fos = null;
        //2.输入流读取第三方资产目录下的文件
        try {
            stream = getAssets().open(dbName);
            //3。将读取的内容写入到指定文件夹的文件中去
            fos = new FileOutputStream(file);
            //4.每次的读取内容大小
            byte[] bs = new byte[1024];
            int temp = -1;
            while ((temp = stream.read(bs)) != -1) {
                fos.write(bs, 0, temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null && fos != null) {
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    //界面动画
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        mRl_root.startAnimation(alphaAnimation);
    }

    /**
     * 获取数据
     *
     * @return
     */
    private void initDate() {
        //1.获取应用版本名称
        tv_version_name.setText("版本名称:" + getVersionName());
        //本地版本号和服务器版本号对比是否有更新，如果有更新提示用户下载
        //2.检测获取本地版本号
        mLocalVersionCode = getVersionCode();
        //获取服务器版本号（客户端发请求，服务端给响应，（json,xml））
        //json中内容包含：
        /*更新版本的版本名称
        * 新版本的描述信息
        * 服务端版本号
        * 新版本apk的下载地址*/
        if (SpUtil.getBoolean(getApplicationContext(), ConstantValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
            //enterHome();
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, 1000);

        }

    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {

                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();

                //发送请求获取数据,参数则为请求jaon的连接地址
                //http://192.168.199.217:8080/update74.json  测试阶段用这个不好
                //仅限于模拟器访问电脑的tomcat

                try {
                    //封装url地址
                    URL url = new URL("http://10.0.2.2:8080/update74.json");
                    //开启一个链接
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    //设置常见请求参数（请求头）
                    httpURLConnection.setConnectTimeout(2000);  //请求超时
                    httpURLConnection.setReadTimeout(10 * 1000);  //读取超时
                    httpURLConnection.setRequestMethod("GET");  //请求方式
                    //4.获取请求成功响应码
                    if (httpURLConnection.getResponseCode() == 200) {
                        //5.以流的形式，将数据取下来
                        InputStream is = httpURLConnection.getInputStream();
                        //6.将流转换成字符串（经常用到，要用工具类封装）
                        String json = StreamUtils.stream2String(is);
                        Log.i(tag, json);
                        //json的解析
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        mVersionDescribe = jsonObject.getString("versionDescribe");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        Log.i(tag, versionName);
                        Log.i(tag, mVersionDescribe);
                        Log.i(tag, versionCode);
                        Log.i(tag, mDownloadUrl);
                        //8.比对版本号（服务器版本号大于本地版本号，提示用户更新）
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            msg.what = UPDATE_VERSION;
                        } else {
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    //指定睡眠时间，请求网络的时长不超过4秒则不做处理
                    //请求网络的时长小于4秒，强制让其睡眠4秒
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 + startTime - endTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(msg);
                }


            }
        }.start();
    }

    /**
     * 返回版本号
     *
     * @return 非0代表获取成功
     */
    private int getVersionCode() {

        //获取包管理者对象 packageManager
        PackageManager packageManager = getPackageManager();
        //从包的管理者对象中获取包的基本信息
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 初始化UI
     *
     * @return
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        mRl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }

    /**
     * 获取版本名称
     *
     * @return 应用版本名称   返回null代表有异常
     */
    public String getVersionName() {
        //1.包管理者对象paclageManager
        PackageManager packageManager = getPackageManager();
        //2.从报的管理者对象中获取指定报名的基本信息（版本名称，版本号）,传0代表获取基本信息
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
