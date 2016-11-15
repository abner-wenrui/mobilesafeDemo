package com.abner.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Project   com.abner.mobilesafe.db
 *
 * @Author Abner
 * Time   2016/9/9.10:37
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    public BlackNumberOpenHelper(Context context) {
        super(context, "blacknumber.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据库中的表
        db.execSQL("create table blacknumber(_id integer primary key autoincrement, phone varcjar(20), mode varchar(5));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
