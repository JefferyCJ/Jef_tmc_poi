package com.tumi.data.poi.service.task.impl;

import com.googlecode.easyec.sika.WorkData;
import com.tumi.data.poi.domain.ProductWorkDataFile;
import com.tumi.data.poi.service.product.TumiProductService;
import com.tumi.data.poi.service.scene7.Scene7ImageExtractorService;
import com.tumi.data.poi.service.task.DataExecuteTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/25 12:43 AM
 * @describe:
 */
@Service("dataExecuteTaskService")
public class DataExecuteTaskServiceImpl implements DataExecuteTaskService {

    @Resource
    private Scene7ImageExtractorService scene7ImageExtractorService;
    @Resource
    private TumiProductService tumiProductService;

    @Override
    public void dealWithProdData(List<WorkData> list, ProductWorkDataFile sucessData, ProductWorkDataFile failData) {
        tumiProductService.checkLineData0(list);
        scene7ImageExtractorService.executeSyncPicture(list, sucessData, failData);
    }
}
