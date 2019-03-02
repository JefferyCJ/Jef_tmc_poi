package com.tumi.data.poi.domain;

import com.googlecode.easyec.sika.WorkData;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/16 3:07 PM
 * @describe:
 */
public interface ProductWorkDataFile extends Serializable {
    void addData(List<WorkData> workDataList);

    String getFileName();

    String getFilePath();

    String getOutTemplate();

    void setOutTemplate(String outTemplate);

    void setFileName(String fileName);

    List<List<WorkData>> getWorkData();

    void setFilePath(String filePath);

    void setWorkData(List<List<WorkData>> workData);

    String getSite();

    void setSite(String site);

    boolean isResult();
}
