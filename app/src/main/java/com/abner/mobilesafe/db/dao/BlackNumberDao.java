package com.abner.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.abner.mobilesafe.db.BlackNumberOpenHelper;
import com.abner.mobilesafe.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Project   com.abner.mobilesafe.db.dao
 *
 * @Author Abner
 * Time   2016/9/9.10:41
 */
public class BlackNumberDao {

    private final BlackNumberOpenHelper mBlackNumberOpenHelper;

    //BlackNumberDao单例模式
    //1.私有化构造方法
    private BlackNumberDao(Context context) {
        //创建数据库及其表结构
        mBlackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }

    //2.声明一个当前类的对象
    private static BlackNumberDao blackNumberDao = null;

    //3.提供一个静态方法，如果当前类的对象为空，创建一个新的
    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null) {
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
     * 增加一个条目
     *
     * @param phone 拦截电话号码
     * @param mode  拦截类型（1：短信   2：电话    3：短信和电话）
     */
    public void insert(String phone, String mode) {
        //开启数据库，准备做写入操作
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("phone", phone);
        contentValues.put("mode", mode);
        db.insert("blacknumber", null, contentValues);
        db.close();
    }

    /**
     * 从数据库中删除电话号码
     *
     * @param phone 电话号码
     */
    public void delete(String phone) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        db.delete("blacknumber", "phone = ?", new String[]{phone});
        db.close();
    }

    /**
     * 根据电话号码去更新拦截模式
     *
     * @param phone 更新拦截模式的电话号码
     * @param mode  要更新为的模式（1：短信   2：电话    3：短信和电话）
     */
    public void update(String phone, String mode) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);
        db.update("blacknumber", contentValues, "mode = ?", new String[]{mode});
        db.close();
    }

    /**
     * @return 查询到的所有的号码，以及拦截类型所在的集合
     */
    public List<BlackNumberInfo> findAll() {
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        ArrayList<BlackNumberInfo> blackNumberInfoList = new ArrayList<>();
        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setPhone(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfoList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfoList;
    }

    /**
     * 每次查询20条数据
     *
     * @param index index 查询的索引值
     */
    public List<BlackNumberInfo> find(int index) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20", new String[]{index + ""});
        ArrayList<BlackNumberInfo> blackNumberInfoList = new ArrayList<>();
        while (cursor.moveToNext()) {
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setPhone(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getString(1));
            blackNumberInfoList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberInfoList;
    }

    /**
     * 数据库中总数据的条目
     *
     * @return 返回0 代表没数据或者异常
     */
    public int getCounnt() {
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    public int getMode(String phone) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getReadableDatabase();
        int mode = 0;
        Cursor cursor = db.query("blacknumber", new String[]{"moede"}, "phone=?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

}
