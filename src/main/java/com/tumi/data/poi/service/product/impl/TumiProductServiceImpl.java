package com.tumi.data.poi.service.product.impl;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.WorkbookReader;
import com.googlecode.easyec.sika.ss.ExcelFactory;
import com.tumi.data.poi.config.PoiProperties;
import com.tumi.data.poi.domain.ProductWorkDataFile;
import com.tumi.data.poi.domain.impl.ProductWorkDataFileImpl;
import com.tumi.data.poi.handler.DefaultWorkbookHandler;
import com.tumi.data.poi.handler.PrdCategoryHandler;
import com.tumi.data.poi.service.product.TumiProductService;
import com.tumi.data.poi.service.task.DataExecuteTaskService;
import com.tumi.data.poi.utils.CategoryUtils;
import com.tumi.data.poi.utils.WorkDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 11:47 AM
 * @describe:
 */
@Service("tumiProductService")
public class TumiProductServiceImpl implements TumiProductService {
    private static final Logger LOG = LoggerFactory.getLogger(TumiProductServiceImpl.class);

    @Resource
    private PoiProperties poiProperties;
    @Resource
    private DataExecuteTaskService dataExecuteTaskService;
    @Resource
    private TaskExecutor taskExecutor;


    @Override
    public List<ProductWorkDataFile> refactoring() throws Exception {
        ProductWorkDataFile baseData = this.checkCategories();
        if (null == baseData) {
            LOG.error("not find any data after check category");
            return null;
        }
        return this.buildWorkDataFiles(baseData);
    }

    @Override
    public ProductWorkDataFile checkCategories() throws Exception {
        ProductWorkDataFile workDataFile = new ProductWorkDataFileImpl();
        workDataFile.setFileName(poiProperties.getProductFile());
        InputStream in1 = loadFromLocal(poiProperties.getCategoryFile());
        InputStream in2 = loadFromLocal(poiProperties.getProductFile());

        PrdCategoryHandler handler1 = new PrdCategoryHandler();
        WorkbookReader r1 = new WorkbookReader();
        r1.add(handler1);

        DefaultWorkbookHandler handler2 = new DefaultWorkbookHandler();
        WorkbookReader r2 = new WorkbookReader();
        r2.add(handler2);

        ExcelFactory.getInstance().read(in1, r1);
        ExcelFactory.getInstance().read(in2, r2);
        LOG.info("begin check product's category list");
        CategoryUtils.fill(
                handler2.getRecords(),
                handler1.getCategories(),
                poiProperties.getCategoryColumn()
        );
        LOG.info("category list check end...");
        workDataFile.setWorkData(handler2.getRecords());
        return workDataFile;
    }

    @Override
    public List<ProductWorkDataFile> buildWorkDataFiles(ProductWorkDataFile dataFile) {
        LOG.info("product num is:[" + CollectionUtils.size(dataFile.getWorkData()) + "]");
        List<ProductWorkDataFile> workDataFileList = new ArrayList<>();

        ProductWorkDataFile sucessData = new ProductWorkDataFileImpl();
        sucessData.setFileName(poiProperties.getResultBaseFile());
        ProductWorkDataFile failData = new ProductWorkDataFileImpl();
        failData.setFileName(poiProperties.getResultNoPictureFile());
        LOG.info("begin check product's data...");
        forEachData(dataFile, sucessData, failData);
        LOG.info("check  product's data end...");

        if (CollectionUtils.isNotEmpty(sucessData.getWorkData())) {
            workDataFileList.add(sucessData);
        }
        if (CollectionUtils.isNotEmpty(sucessData.getWorkData())) {
            LOG.info("not find picture product num is [" + CollectionUtils.size(sucessData.getWorkData()) + "]");
            workDataFileList.add(sucessData);
        }
        return workDataFileList;
    }


    @Override
    public void checkLineData0(List<WorkData> list) {
        list.forEach(_l -> {
            //处理字符串为0的逻辑

            String data = WorkDataUtils.getData2String(_l);
            if (StringUtils.equalsIgnoreCase(data, "0")) {
                _l.setValue("");
            }
            //替换全角逗号为半角
            if (StringUtils.isNotBlank(data)) {
                _l.setValue(data.replaceAll("，", ","));
            }
        });
    }

    @Override
    public void checkLineDate(List<List<WorkData>> workDataList) {
        LOG.info("begin check online date&offline date...");
        workDataList.forEach(list -> {
            //处理日期逻辑
            WorkData onlineDate = WorkDataUtils.getData2String(list, poiProperties.getOnlineDateColumn());
            WorkData offlineDate = WorkDataUtils.getData2String(list, poiProperties.getOfflineDateColumn());
            if (null != onlineDate) {
                String onlineString = WorkDataUtils.getDate2String(onlineDate);
                String offlineString = WorkDataUtils.getDate2String(offlineDate);
                if (StringUtils.isBlank(onlineString) || StringUtils.isBlank(offlineString)) {
                    LOG.error("The launch date of the product seems to be wrong,styleCode is:["
                            + WorkDataUtils.getData2String(
                            WorkDataUtils.getData2String(list, poiProperties.getStyleCodeColumn())) + "]");
                }
                onlineDate.setValue(onlineString);
                offlineDate.setValue(offlineString);
            }

        });
    }

    private void forEachData(ProductWorkDataFile dataFile, ProductWorkDataFile sucessData, ProductWorkDataFile failData) {
        dataFile.getWorkData().forEach(list -> {
            dataExecuteTaskService.dealWithProdData(list, sucessData, failData);
        });
    }

    private InputStream loadFromLocal(String file) throws IOException {
        return new FileInputStream(file);
    }

}
