/*    
 * 广汇汽车服务股份公司拥有完全的版权   
 * 使用者必须经过许可
 *----------------------------------------------------------------------*
 * Copyright  (c) 2015 SAP AG. All rights reserved
 * Author       : SAP Custom Development
 * Description  : GeneralUtil.java
 *----------------------------------------------------------------------*
 * Change-History: Change history
 * Developer  Date      Description
 * Longting  2015年11月11日 Short description containing Message, Note ID or CR ID
 *----------------------------------------------------------------------*  
 */
package com.tumi.data.poi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 常用的一些工具方法
 *
 * @author Longting
 * @since JDK 1.8
 */
public class GeneralUtil {
    public final static String YYYYMMDD = "yyyyMMdd";


    public final static String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";


    private static final Logger LOG = LoggerFactory.getLogger(GeneralUtil.class);

    /**
     * 获取字符串编码
     * @param str
     * @return
     */
    public static String getEncoding(String str) {
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是GB2312
                String s = encode;
                return s;      //是的话，返回“GB2312“，以下代码同理
            }
        } catch (Exception exception) {
        }
        encode = "ISO-8859-1";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是ISO-8859-1
                String s1 = encode;
                return s1;
            }
        } catch (Exception exception1) {
        }
        encode = "UTF-8";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {   //判断是不是UTF-8
                String s2 = encode;
                return s2;
            }
        } catch (Exception exception2) {
        }
        encode = "GBK";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {      //判断是不是GBK
                String s3 = encode;
                return s3;
            }
        } catch (Exception exception3) {
        }
        return "";
    }

    /**
     * 用指定字符串替换空字符串
     * @param str
     * @param obj
     * @return
     * @author Longting
     * @since JDK 1.8
     */
    public static String getString(final String str, final String obj) {
        if (str == null || str.length() == 0) {
            return obj;
        }
        return str;
    }


    /**
     * 截取list
     * @param list
     * @param b
     * @param <T>
     * @return
     */
    public static <T> List<List<T>> cutList(final List<T> list, final int b) {
        final List<T> tempList = new ArrayList<T>(list);
        final List<List<T>> newList = new ArrayList<List<T>>();
        List<T> subList = tempList.subList(0, tempList.size() > b ? b : tempList.size());
        while (!subList.isEmpty()) {
            newList.add(new ArrayList<T>(subList));
            subList.clear();
            subList = tempList.subList(0, tempList.size() > b ? b : tempList.size());
        }
        return newList;
    }

    /**
     * MD5加密字符串
     * @param s
     * @return
     */
    public final static String MD5(final String s) {
        final char hexDigits[] =
                {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            final byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            final MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            final byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            final int j = md.length;
            final char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                final byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (final Exception e) {
            LOG.error(e.getMessage());
            return null;
        }
    }

    /**
     * 连接list
     * @param first
     * @param second
     * @param <T>
     * @return
     */
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }



    /**
     * 获取异常信息
     * @param e
     * @return
     */
    public static String getStackMsg(Exception e) {
        StringBuffer sb = new StringBuffer();
        sb.append(e.getMessage());
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }


    /**
     * 读取文件创建时间
     *
     * @throws ParseException
     */
    public static Date getCreateTime(File file) throws ParseException {
        return new Date(file.lastModified());
    }

    /**
     * 删除所有文件
     * @param path
     * @return
     */
    public static boolean delAllFile(String path) {
        LOG.info("................................................................................."+path);
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        if (tempList.length==0){
            boolean delete = file.delete();
            LOG.info("................................................................................."+delete);
            flag = true;
        }

        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    //删除文件夹
//param folderPath 文件夹完整绝对路径
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); //删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); //删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 获取文件的编码
     * @param file
     * @return
     */
    public static String getCharset(File file) {
        String charset = "GBK"; // 默认编码
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1]
                    == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1]
                    == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    //单独出现BF以下的，也算是GBK
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)// 双字节 (0xC0 - 0xDF)
                            // (0x80 -
                            // 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                        // 也有可能出错，但是几率较小
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
                System.out.println(loc + " " + Integer.toHexString(read));
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }
}
