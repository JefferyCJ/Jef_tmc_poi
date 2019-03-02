package com.tumi.data.poi.service.html;

import java.io.File;
import java.util.Set;

public interface HtmlService {

    public static String ENCODE = "UTF-8";
    /**
     * get tumi site image url
     *
     * @param file
     * @return
     * @throws Exception
     */
    Set<String> getImageUrl(File file) throws Exception;

    /**
     * @param filePath
     * @return
     */
    Set<String> scanImageUrlFile(String filePath);

    /**
     * @param filePath
     * @param imageUrls
     */
    void scanHtmlFile(String filePath, Set<String> imageUrls);

}
