package com.abner.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Project   com.abner.mobilesafe.engine
 *
 * @Author Abner
 * Time   2016/9/6.10:14
 */
public class AddressDao {
    //访问指定数据库
    static String path = "data/data/com.abner.mobilesafe/files/address.db";
    private static String tag = "AddressDao";

    /**
     * 传递一个电话号码，开启数据库连接，进行访问，返回一个归属地
     *
     * @param phone 要查询的电话号码
     */
    public static String getAddress(String phone) {
        String mAddress = "未知号码";
        String regularExpression = "^1[3-8]\\d{9}";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if (phone.matches(regularExpression)) {
            phone = phone.substring(0, 7);
            //开启数据库链接表

            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id=?", new String[]{phone}, null, null, null);

            if (cursor.moveToNext()) {
                String outkey = cursor.getString(0);

                Cursor indexCursor = db.query("data2", new String[]{"location"}, "id = ?", new String[]{outkey}, null, null, null);
                if (indexCursor.moveToNext()) {
                    mAddress = indexCursor.getString(0);
                }
            }
        } else {
            int length = phone.length();
            switch (length) {
                case 3:
                    mAddress = "报警电话";
                    break;
                case 4:
                    mAddress = "模拟器";
                    break;
                case 5:
                    mAddress = "服务电话";
                    break;
                case 7:
                    mAddress = "本地电话";
                    break;
                case 8:
                    mAddress = "本地电话";
                    break;
                case 11:
                    String area = phone.substring(1, 3);
                    Cursor cursor = db.query("data2", new String[]{"location"}, area, new String[]{area}, null, null, null);
                    if (cursor.moveToNext()){
                        mAddress = cursor.getString(0);
                    }
                    break;
                case 12:
                    String area1 = phone.substring(1, 3);
                    Cursor cursor1 = db.query("data2", new String[]{"location"}, area1, new String[]{area1}, null, null, null);
                    if (cursor1.moveToNext()){
                        mAddress = cursor1.getString(0);
                    }
                    break;
            }
        }

        return mAddress;
    }
}
