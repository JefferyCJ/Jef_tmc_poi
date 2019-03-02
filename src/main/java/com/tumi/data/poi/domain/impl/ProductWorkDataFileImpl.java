package com.tumi.data.poi.domain.impl;

import com.googlecode.easyec.sika.WorkData;
import com.tumi.data.poi.domain.ProductWorkDataFile;
import com.tumi.data.poi.utils.DateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 2:35 PM
 * @describe:
 */
public class ProductWorkDataFileImpl implements ProductWorkDataFile {
    private String site;
    private String filePath;
    private String fileName;
    private String outTemplate;
    private List<List<WorkData>> workData = new ArrayList<>();


    public ProductWorkDataFileImpl(String site) {
        this.site = site;
    }

    @Override
    public void addData(List<WorkData> workDataList) {
        if (CollectionUtils.isNotEmpty(workDataList)) {
            workData.add(workDataList);
        }
    }

    @Override
    public String getFileName() {
        return site + "-" + fileName + "-" + DateUtils.getDate2String("yyyy-MM-dd", new Date())+"-"+System.currentTimeMillis();
    }

    @Override
    public String getFilePath() {
        return filePath;
    }

    @Override
    public String getOutTemplate() {
        return outTemplate;
    }

    @Override
    public void setOutTemplate(String outTemplate) {
        this.outTemplate = outTemplate;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<List<WorkData>> getWorkData() {
        return workData;
    }

    @Override
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void setWorkData(List<List<WorkData>> workData) {
        this.workData = workData;
    }

    @Override
    public String getSite() {
        return site;
    }

    @Override
    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public boolean isResult() {
        return StringUtils.containsIgnoreCase(fileName, "result");
    }
}
