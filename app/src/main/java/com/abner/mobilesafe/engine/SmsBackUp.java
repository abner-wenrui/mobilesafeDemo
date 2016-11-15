package com.abner.mobilesafe.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Project   com.abner.mobilesafe.engine
 *
 * @Author Abner
 * Time   2016/9/13.10:56
 */
public class SmsBackUp {
    private static int index = 0;
    //需要用到上下文环境，短信保存路径，进度条所在的对话框用于备份过程中的进度的更新

    public static void buckUp(Context context, String path, CallBack callBack) {
        FileOutputStream fos = null;
        Cursor cursor = null;
        try {
            //1.获取备份短信的文件
            File file = new File(path);
            //2.获取内容解析器，获取短信数据库的数据
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
            //3.文件相应的输出流
            fos = new FileOutputStream(file);
            //4.序列化数据库中读取的数据，放置到xml中
            XmlSerializer xmlSerializer = Xml.newSerializer();
            //5.给xml做相应设置
            xmlSerializer.setOutput(fos, "utf-8");


            //DTD（xml规范）
            xmlSerializer.startDocument("utf-8", true);
            xmlSerializer.startTag(null, "smss");
            //6.备份短信总数的指定
            if (callBack != null){
                callBack.setMax(cursor.getCount());
            }
            //7.读取数据库中每一行的数据写入到xml中
            while (cursor.moveToNext()) {
                xmlSerializer.startTag(null, "sms");
                xmlSerializer.startTag(null, "address");
                xmlSerializer.text(cursor.getString(0));
                xmlSerializer.endTag(null, "address");

                xmlSerializer.startTag(null, "date");
                xmlSerializer.text(cursor.getString(1));
                xmlSerializer.endTag(null, "date");

                xmlSerializer.startTag(null, "type");
                xmlSerializer.text(cursor.getString(2));
                xmlSerializer.endTag(null, "type");

                xmlSerializer.startTag(null, "body");
                xmlSerializer.text(cursor.getString(3));
                xmlSerializer.endTag(null, "body");
                xmlSerializer.endTag(null, "sms");
                Thread.sleep(500);
                if (callBack != null){
                    callBack.setProgress(++index);
                }

            }
            xmlSerializer.endTag(null, "smss");
            xmlSerializer.endDocument();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && fos != null) {
                try {
                    cursor.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public interface CallBack{
        //短信总数设置实现方法
        public void setMax(int max);
        //备份过程中短信百分比更新
        public void setProgress(int index);
    }
}