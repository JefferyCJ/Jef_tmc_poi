package com.tumi.data.poi.service.task;

import com.googlecode.easyec.sika.WorkData;
import com.tumi.data.poi.domain.ProductWorkDataFile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/25 12:42 AM
 * @describe:
 */
public interface DataExecuteTaskService {
    /**
     * 处理WorkData
     *
     * @param list
     * @param sucessData
     * @param failData
     */
    void dealWithProdData(List<WorkData> list, ProductWorkDataFile sucessData, ProductWorkDataFile failData);
}
