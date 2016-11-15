package com.abner.mobilesafe.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.abner.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactListActivity extends AppCompatActivity {

    private ListView lv_contact;
    private String tag = "ContactListActivity";
    private List<HashMap<String, String>> contextList = new ArrayList<HashMap<String, String>>();
    private MyAdapter mAdapter;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //8.填充数据适配器
            mAdapter = new MyAdapter();
            lv_contact.setAdapter(mAdapter);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        initUI();

        initData();
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contextList.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            return contextList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.listview_contact_item, null);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);

//            tv_name.setText(contextList.get(position).get("name"));
//            tv_number.setText(contextList.get(position).get("number"));
            tv_name.setText(getItem(position).get("name"));
            tv_number.setText(getItem(position).get("number"));
            return view;
        }
    }

    //获取系统联系人数据
    private void initData() {
        //因为读取系统联系人可能是一个耗时操作，放置到了子线程中处理
        new Thread() {
            @Override
            public void run() {
                //1.获取内容解析者对象
                ContentResolver contentResolver = getContentResolver();
                contextList.clear();
                //2.查询系统联系人数据库表的过程（读取联系人权限）
                Cursor cursor = contentResolver.query(Uri.parse("content://com.android.contacts/raw_contacts"), new String[]{"contact_id"}, null, null, null);
                //3.循环游标，直到没有数据为止
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    //4.根据用户唯一性id值，查询data表和mimetype表生成的视图，获取data以及mimetype字段
                    Cursor indexCursor = contentResolver.query(Uri.parse("content://com.android.contacts/data"), new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{id}, null);
                    //循环获取每一个联系人的电话号码及姓名数据类型
                    HashMap<String, String> hashMap = new HashMap<>();
                    while (indexCursor.moveToNext()) {
                        String date = indexCursor.getString(0);
                        String type = indexCursor.getString(1);
                        //区分类型去给hashmap填充数据
                        if (type.equals("vnd.android.cursor.item/name")) {
                            //数据非空判断
                            if (!TextUtils.isEmpty(date)) {
                                hashMap.put("name", date);
                            }
                        } else if (type.equals("vnd.android.cursor.item/phone_v2")) {
                            if (!TextUtils.isEmpty(date)) {
                                hashMap.put("number", date);
                            }
                        }
                    }
                    indexCursor.close();

                    contextList.add(hashMap);
                }
                cursor.close();
                //消息机制.发送一个空消息，告诉主线程可以使用子线程已经填充好的数据集合
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void initUI() {
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.获取点中条目的索引，指向集合中的对象
                if (mAdapter!=null) {
                    //2.获取当前条目指向集合对应的电话号码
                    String number = mAdapter.getItem(position).get("number");
                    //3.此电话号码需要给第三个导航界面使用
                    //4.在结束此界面回到上一个导航界面的时候需要将数据返回回去
                    Intent intent = new Intent().putExtra("number", number);
                    setResult(0,intent);
                    finish();
                }else {

                }
            }
        });
    }
}
