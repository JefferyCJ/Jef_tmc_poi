package com.tumi.data.poi.service.product.impl;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.WorkbookReader;
import com.googlecode.easyec.sika.ss.ExcelFactory;
import com.tumi.data.poi.config.PoiProperties;
import com.tumi.data.poi.domain.ProductWorkDataFile;
import com.tumi.data.poi.domain.impl.ProductWorkDataFileImpl;
import com.tumi.data.poi.handler.DefaultWorkbookHandler;
import com.tumi.data.poi.handler.NoMonogramsProdTypeHandler;
import com.tumi.data.poi.handler.PrdCategoryHandler;
import com.tumi.data.poi.service.product.TumiProductService;
import com.tumi.data.poi.service.scene7.Scene7ImageExtractorService;
import com.tumi.data.poi.service.stream.FileOpService;
import com.tumi.data.poi.service.task.DataExecuteTaskService;
import com.tumi.data.poi.utils.CategoryUtils;
import com.tumi.data.poi.utils.CheckUtils;
import com.tumi.data.poi.utils.DateUtils;
import com.tumi.data.poi.utils.WorkDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 11:47 AM
 * @describe:
 */
@Service("tumiProductService")
public class TumiProductServiceImpl implements TumiProductService {
    private static final Logger logger = LoggerFactory.getLogger(TumiProductServiceImpl.class);

    @Resource
    private PoiProperties poiProperties;
    @Resource
    private DataExecuteTaskService dataExecuteTaskService;
    @Resource
    private Scene7ImageExtractorService scene7ImageExtractorService;
    @Resource
    private FileOpService fileOpService;


    @Override
    public void scanProductFile(String filePath) throws Exception {
        List<ProductWorkDataFile> workDataFileList = new ArrayList<>();
        List<File> productFiles = fileOpService.scanFiles(filePath);
        for (File prodDir : productFiles) {
            if (prodDir.isFile()) {
                try {
                    workDataFileList.addAll(this.refactorFile(prodDir));
                } catch (Exception e) {
                    logger.error(e.getMessage());
                } finally {
                    fileOpService.moveFileToHistory(filePath, prodDir);
                }
            }
        }
        List<File> noMonogramFiles = fileOpService.scanFiles(poiProperties.getNoMonogramsFile());
        Set<String> noMonogramsWorkDatas = new HashSet<>();
        for (File noMonogramFile : noMonogramFiles) {
            if (noMonogramFile.isFile()) {
                noMonogramsWorkDatas.addAll(getNoMonogramsWorkDatas(noMonogramFile));
            }
        }

        for (int i = 0; i < CollectionUtils.size(workDataFileList); i++) {
            ProductWorkDataFile dataFile = workDataFileList.get(i);
            logger.info("check result file 【" + dataFile.getFileName() + "】 data num is 【" + CollectionUtils.size(dataFile.getWorkData()) + "】");
            File dir = new File(dataFile.getFilePath() + File.separator + dataFile.getFileName() + "—v" + i + ".xlsx");
            File parent = dir.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            fileOpService.prodFileDownload(dir, dataFile.getOutTemplate(), dataFile.getWorkData());
            if (dataFile.isResult()) {
                logger.info("begin generate impex...");
                String impexDownloadPath = poiProperties.getImpexPath(dataFile.getSite());
                logger.info("download product's impex data path is【" + impexDownloadPath + "】");
                fileOpService.impexFileDownload(dir, impexDownloadPath, dataFile.getSite(), noMonogramsWorkDatas);
            }
        }
    }

    @Override
    public List<ProductWorkDataFile> refactorFile(File file) throws Exception {
        String site = StringUtils.containsIgnoreCase(file.getName(), "CN") ? TumiProductService.CN : TumiProductService.HK;
        logger.info("check the goods of 【" + site + "】 site ");

        List<List<WorkData>> prodWorkData = getWorkDatas(file);
        logger.info("start check products, product num : 【" + CollectionUtils.size(prodWorkData) + "】");
        String categoryFile = poiProperties.getCategoryFile();
        int begin = categoryFile.lastIndexOf("-") + 1;
        int end = categoryFile.lastIndexOf(".");
        String categoryFileName = categoryFile.substring(begin, end);
        categoryFile = StringUtils.replace(categoryFile, categoryFileName, site.toUpperCase());

        Set<String> categoryWorkDatas = getCategoryWorkDatas(new File(categoryFile), site);

        logger.info("start to convert product's data...");
        for (List<WorkData> dataList : prodWorkData) {
            logger.info("begin check product's scene7 image");
            scene7ImageExtractorService.executeSyncPicture(dataList);

            logger.info("begin check product's category");
            CategoryUtils.fill(
                dataList, categoryWorkDatas,
                poiProperties.getCategoryColumn(),
                true
            );

            logger.info("begin check online date&offline date...");
            this.checkLineDate(dataList);

            logger.info("begin replace work data...");
            this.replaceLineData(dataList);

            logger.info("begin check boolean data...");
            this.checkBoolData(dataList);

            logger.info("begin check gender data...");
            this.checkGender(dataList);

            if (StringUtils.equalsIgnoreCase(site, TumiProductService.HK)) {
                logger.info("begin check multiple language");
                this.checkMultipleLanguage(dataList);
            }
        }
        Thread.sleep(1000);

        List<ProductWorkDataFile> dataFiles = refactorWorkData(prodWorkData, site);
        return dataFiles;
    }


    @Override
    public void checkLineDate(List<WorkData> list) {
        //处理日期逻辑
        WorkData onlineDate = WorkDataUtils.getData2String(list, poiProperties.getOnlineDateColumn());
        WorkData offlineDate = WorkDataUtils.getData2String(list, poiProperties.getOfflineDateColumn());
        if (null != onlineDate && null != offlineDate) {
            String onlineString = WorkDataUtils.getDate2String(onlineDate);
            String offlineString = WorkDataUtils.getDate2String(offlineDate);
            if (StringUtils.isBlank(onlineString) || StringUtils.isBlank(offlineString)) {
                logger.error("The launch date of the product seems to be wrong");
                WorkDataUtils.appendErrorLabel(list, "column 【" + poiProperties.getOnlineDateColumn() + "&" + poiProperties.getOfflineDateColumn() + "】" +
                    " date is error");

            } else {
                onlineDate.setValue(DateUtils.getStringToDate(onlineString));
                offlineDate.setValue(DateUtils.getStringToDate(offlineString));
            }
        } else {
            WorkDataUtils.appendErrorLabel(list, "column 【" + poiProperties.getOnlineDateColumn() + "&" + poiProperties.getOfflineDateColumn() + "】" +
                " not found data");
        }
        logger.info("begin check online date&offline date...");

    }

    @Override
    public void replaceLineData(List<WorkData> list) {
        List<String> columns = WorkDataUtils.getString2ListString(poiProperties.getReplaceDataColumn());
        for (String column : columns) {
            WorkData data = WorkDataUtils.getData2String(list, column);
            WorkDataUtils.replaceLineData(data);
        }
    }

    @Override
    public void checkBoolData(List<WorkData> list) {
        List<String> columns = WorkDataUtils.getString2ListString(poiProperties.getBoolDataColumn());
        for (String column : columns) {
            WorkData data = WorkDataUtils.getData2String(list, column);
            String value = WorkDataUtils.getData2String(data);
            if (StringUtils.isNotBlank(value)) {
                boolean b = CheckUtils.isBoolean(value);
                if (!b) {
                    WorkDataUtils.appendErrorLabel(list, "column 【" + column + "】 isn't boolean");
                }
            }

        }
    }

    @Override
    public void checkGender(List<WorkData> list) {
        String label = WorkDataUtils.getDataCovertString(list, poiProperties.getGendersColumn());
        if (StringUtils.isBlank(label)) {
            WorkDataUtils.appendErrorLabel(list, "column 【" + poiProperties.getGendersColumn() + "】 is empty");
        }
        List<String> genders = WorkDataUtils.getString2ListString(label);
        boolean b = CheckUtils.isGender(genders);
        if (!b) {
            WorkDataUtils.appendErrorLabel(list, "column 【" + poiProperties.getGendersColumn() + "】  gender is not standard format");
        }

    }

    @Override
    public void checkMultipleLanguage(List<WorkData> list) {
        List<String> multipleColumns = WorkDataUtils.getString2ListString(poiProperties.getMultipleLanguageColumn());
        for (String multipleColumn : multipleColumns) {
            List<String> columns = WorkDataUtils.getString2_ListString(multipleColumn);
            if (CollectionUtils.size(columns) == 2) {

                String en = columns.get(0);
                String columnEn = WorkDataUtils.getDataCovertString(list, en);
                String zh = columns.get(1);
                String columnZh = WorkDataUtils.getDataCovertString(list, zh);
                if (StringUtils.isNotBlank(columnEn) != StringUtils.isNotBlank(columnZh)) {
                    WorkDataUtils.appendErrorLabel(list, "column 【" + multipleColumn + "】,There are differences between Chinese and English.");
                }

                if (StringUtils.isNotBlank(columnEn) && CheckUtils.isContainChinese(columnEn)) {
                    WorkDataUtils.appendErrorLabel(list, "column 【" + multipleColumn + "】,The English column contains Chinese.");
                }
            }
        }
    }

    @Override
    public void scanCheckProductTypeFile(String filePath) throws Exception {
        List<List<WorkData>> prodWorkData = new ArrayList<>();
        List<File> productFiles = fileOpService.scanFiles(filePath);
        for (File prodDir : productFiles) {
            if (prodDir.isFile()) {
                try {
                    prodWorkData.addAll(getWorkDatas(prodDir));
                } catch (Exception e) {
                    logger.error(e.getMessage());
                } finally {
                    fileOpService.moveFileToHistory(filePath, prodDir);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(prodWorkData)){

        }
    }

    private List<ProductWorkDataFile> refactorWorkData(List<List<WorkData>> prodWorkDatas, String site) {
        List<ProductWorkDataFile> dataFiles = new ArrayList<>();
        ProductWorkDataFile resultData = new ProductWorkDataFileImpl(site);
        ProductWorkDataFile errorData = new ProductWorkDataFileImpl(site);

        for (List<WorkData> workDatas : prodWorkDatas) {
            if (isErrorData(workDatas)) {
                errorData.addData(workDatas);
            } else {
                resultData.addData(workDatas);
            }
        }
        if (CollectionUtils.isNotEmpty(resultData.getWorkData())) {
            dataFiles.add(resultData);
            logger.info("result data product's num : 【" + CollectionUtils.size(resultData.getWorkData()) + "】");
            resultData.setFileName("result");
            resultData.setOutTemplate("template/output.xlsx");
            resultData.setFilePath(poiProperties.getResultFile());
        }

        if (CollectionUtils.isNotEmpty(errorData.getWorkData())) {
            dataFiles.add(errorData);
            logger.info("error data product's num : 【" + CollectionUtils.size(errorData.getWorkData()) + "】");
            errorData.setFileName("error");
            errorData.setOutTemplate("template/errorout.xlsx");
            errorData.setFilePath(poiProperties.getErrorFile());
        }

        return dataFiles;

    }

    private boolean isErrorData(List<WorkData> workDatas) {
        WorkData data = WorkDataUtils.getData2String(workDatas, poiProperties.getErrorReasonColumn());
        if (null == data) {
            return false;
        }
        if (Objects.isNull(data.getValue())) {
            return false;
        }

        if (StringUtils.isBlank(String.valueOf(data.getValue()))) {
            return false;
        }
        return true;
    }

    private Set<String> getCategoryWorkDatas(File file, String site) throws Exception {
        InputStream in = loadFromLocal(file);
        PrdCategoryHandler handler = new PrdCategoryHandler(site);
        WorkbookReader reader = new WorkbookReader();
        reader.add(handler);
        ExcelFactory.getInstance().read(in, reader);
        return handler.getCategories();
    }

    private Set<String> getNoMonogramsWorkDatas(File file) throws Exception {
        InputStream in = loadFromLocal(file);
        NoMonogramsProdTypeHandler handler = new NoMonogramsProdTypeHandler();
        WorkbookReader reader = new WorkbookReader();
        reader.add(handler);
        ExcelFactory.getInstance().read(in, reader);
        return handler.getCategories();
    }

    private List<List<WorkData>> getWorkDatas(File file) throws Exception {
        InputStream in = loadFromLocal(file);
        DefaultWorkbookHandler handler = new DefaultWorkbookHandler();
        WorkbookReader reader = new WorkbookReader();
        reader.add(handler);
        ExcelFactory.getInstance().read(in, reader);
        return handler.getRecords();
    }

    @Override
    public InputStream loadFromLocal(File file) throws IOException {
        return new FileInputStream(file);
    }

}
