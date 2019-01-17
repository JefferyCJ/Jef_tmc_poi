package com.tumi.data.poi.service.scene7;

import com.tumi.data.poi.domain.ProductWorkDataFile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 1:57 PM
 * @describe:
 */
public interface Scene7ImageExtractorService {
    /**sync Scene7 images
     * @param workData
     * @return
     * @throws Exception
     */
    List<ProductWorkDataFile> syncImages(ProductWorkDataFile workData) throws Exception;
}

