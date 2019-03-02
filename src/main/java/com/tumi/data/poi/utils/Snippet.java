/*
 * 广汇汽车服务股份公司拥有完全的版权
 * 使用者必须经过许可
 *----------------------------------------------------------------------*
 * Copyright  (c) 2015 SAP AG. All rights reserved
 * Author       : SAP Custom Development
 * Description  : Snippet.java
 *----------------------------------------------------------------------*
 * Change-History: Change history
 * Developer  Date      Description
 * Longting  2015年11月11日 Short description containing Message, Note ID or CR ID
 *----------------------------------------------------------------------*
 */
package com.tumi.data.poi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * 把字符串写入到csv文件中
 *
 * @author Longting
 * @since JDK 1.8
 */
public class Snippet {

    static final Logger logger = LoggerFactory.getLogger(Snippet.class);

    public static File createCSVFile(String outPutPath, String filename, List<String> list, String encode) throws IOException {
        File csvFile = null;
        BufferedWriter csvFileOutputStream = null;
        if (list.size() > 0) {
            try {
                csvFile = new File(outPutPath + File.separator + filename + ".csv");
                File parent = csvFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                csvFile.createNewFile();

                // GB2312使正确读取分隔符","
                csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), encode), 1024);
                // 写入文件头部
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) != null) {
                        csvFileOutputStream.write(list.get(i));
                        csvFileOutputStream.newLine();
                    }
                }
                csvFileOutputStream.flush();
            } finally {
                try {
                    csvFileOutputStream.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return csvFile;
    }
}
