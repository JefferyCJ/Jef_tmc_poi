package com.tumi.data.poi.service.stream.impl;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.WorkbookWriter;
import com.googlecode.easyec.sika.ss.ExcelFactory;
import com.tumi.data.poi.callback.DefaultWorkbookCallback;
import com.tumi.data.poi.impex.CvCNProductImportCovert;
import com.tumi.data.poi.impex.CvHKProductImportCovert;
import com.tumi.data.poi.service.html.HtmlService;
import com.tumi.data.poi.service.product.TumiProductService;
import com.tumi.data.poi.service.product.impl.TumiProductServiceImpl;
import com.tumi.data.poi.service.stream.FileOpService;
import com.tumi.data.poi.utils.DateUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 11:29 AM
 * @describe:
 */
@Service("fileOpService")
public class FileOpServiceImpl implements FileOpService {
    private static final Logger logger = LoggerFactory.getLogger(TumiProductServiceImpl.class);


    @Override
    public void prodFileDownload(File file, String template, List<List<WorkData>> records) throws Exception {
        WorkbookWriter w = new WorkbookWriter();
        w.add(new DefaultWorkbookCallback(records));
        InputStream in = loadFromClasspath(template);

        byte[] bs = ExcelFactory.getInstance().write(in, w);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bs);
            fos.flush();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    @Override
    public void impexFileDownload(File xlsxFile, String downloadPath, String site, Set<String> noMonograms) throws Exception {
        if (StringUtils.equalsIgnoreCase(site, TumiProductService.CN)) {
            new CvCNProductImportCovert(xlsxFile, downloadPath, noMonograms);
        } else {
            new CvHKProductImportCovert(xlsxFile, downloadPath, noMonograms);
        }
    }

    @Override
    public void htmlFileDownload(String conetnt, String fileName) throws IOException {
        File file = new File(fileName);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        BufferedWriter bufferedWriter = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(fileName), HtmlService.ENCODE));
        bufferedWriter.write(conetnt);
        bufferedWriter.newLine();// 换行
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    @Override
    public void moveFileToHistory(String filePath, File dir) {
        File historyDir = new File(filePath, "history");
        if (!historyDir.exists()) historyDir.mkdirs();
        dir.renameTo(new File(historyDir, dir.getName() + "_" + DateUtils.getDate2String("yyyyMMdd", new Date())));
        logger.info("File has been moved to 【" + dir.getAbsolutePath() + "】, file: 【" + dir.getName() + "】.");
    }

    @Override
    public List<File> scanFiles(String filepath) {
        File file = new File(filepath);
        List<File> dirs = null;
        if (file.isDirectory()) {
            dirs = Arrays.asList(file.listFiles());
        }
        return dirs;
    }


    private InputStream loadFromClasspath(String file) throws IOException {
        return new ClassPathResource(file).getInputStream();
    }
}
