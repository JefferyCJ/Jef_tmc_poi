package com.tumi.data.poi.service.stream;

import com.googlecode.easyec.sika.WorkData;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 11:28 AM
 * @describe:
 */
public interface FileOpService {

    /**
     * download excel file
     *
     * @param file
     * @param template
     * @param records
     * @throws Exception
     */
    void prodFileDownload(File file, String template, List<List<WorkData>> records) throws Exception;

    /**
     * download impex file
     *
     * @param xlsxFile
     * @param downloadPath
     * @param site
     * @param noMonograms
     * @throws Exception
     */
    void impexFileDownload(File xlsxFile, String downloadPath, String site, Set<String> noMonograms) throws Exception;

    /**
     * @param content
     * @param fileName
     * @throws IOException
     */
    void htmlFileDownload(String content, String fileName) throws IOException;

    /**
     * move file to history
     *
     * @param filePath
     * @param dir
     */
    void moveFileToHistory(String filePath, File dir);

    /**
     * @param filepath
     * @return
     */
    List<File> scanFiles(String filepath);
}
