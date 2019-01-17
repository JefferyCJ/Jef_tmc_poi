package com.tumi.data.poi.service.stream;

import com.googlecode.easyec.sika.WorkData;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 11:28 AM
 * @describe:
 */
public interface ResultFileDealService {
    /** download file
     * @param records
     * @param filePath
     * @throws Exception
     */
    void fileDownload(List<List<WorkData>> records, String filePath) throws Exception;
}
