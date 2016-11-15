package com.abner.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {


    /**
     * 给指定的字符串按照md5方式进行加密
     * @param psd 需要加密的密码
     */
    public static String encode(String psd) {
        try {
            //加盐
            psd = psd+"mobilesafe";
            //1.制定算法的加密类型
            MessageDigest digest = MessageDigest.getInstance("MD5");
            //2.将需要加密的字符串中转成byte类型的数组，然后进行随机哈希的过程
            byte[] bs = digest.digest(psd.getBytes());
            //3.循环遍历bs，然后让其生成32位字符串，固定写法
            //拼接字符串
            StringBuffer buffer = new StringBuffer();
            for (byte b : bs) {
                int i = b & 0xff;
                //int 类型的i需要转换成16进制字符
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                buffer.append(hexString);
            }
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
