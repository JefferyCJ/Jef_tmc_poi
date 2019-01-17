package com.tumi.data.poi.domain.impl;

import com.googlecode.easyec.sika.WorkData;
import com.tumi.data.poi.domain.ProductWorkDataFile;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 2:35 PM
 * @describe:
 */
public class ProductWorkDataFileImpl implements ProductWorkDataFile {
    private String fileName;
    private List<List<WorkData>> workData = new ArrayList<>();

    public ProductWorkDataFileImpl() {
    }

    @Override
    public void addData(List<WorkData> workDataList) {
        if (CollectionUtils.isNotEmpty(workDataList)) {
            workData.add(workDataList);
        }
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public List<List<WorkData>> getWorkData() {
        return workData;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void setWorkData(List<List<WorkData>> workData) {
        this.workData = workData;
    }
}
