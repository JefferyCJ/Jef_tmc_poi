package com.tumi.data.poi.service.product;

import com.googlecode.easyec.sika.WorkData;
import com.tumi.data.poi.domain.ProductWorkDataFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 11:46 AM
 * @describe:
 */
public interface TumiProductService {
    /**
     * HK site
     */
    String HK = "HK";

    /**
     * CN site
     */
    String CN = "CN";

    /**
     * scanning product filepath
     *
     * @param filePath
     * @return
     */
    void scanProductFile(String filePath) throws Exception;

    /**
     * @param file
     * @return
     * @throws Exception
     */
    List<ProductWorkDataFile> refactorFile(File file) throws Exception;

    /**
     * check online date&offline date fields
     *
     * @param list
     */
    void checkLineDate(List<WorkData> list);

    /**
     * replace data's ","&" "
     *
     * @param list
     */
    void replaceLineData(List<WorkData> list);

    /**
     * check boolean field
     *
     * @param list
     */
    void checkBoolData(List<WorkData> list);

    /**
     * check gender field
     *
     * @param list
     */
    void checkGender(List<WorkData> list);

    /**
     * check multiple language field
     *
     * @param list
     */
    void checkMultipleLanguage(List<WorkData> list);

    InputStream loadFromLocal(File file) throws IOException;
}
