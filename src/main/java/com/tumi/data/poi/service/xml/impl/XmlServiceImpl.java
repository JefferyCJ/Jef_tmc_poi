package com.tumi.data.poi.service.xml.impl;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.WorkbookReader;
import com.googlecode.easyec.sika.data.DefaultWorkData;
import com.googlecode.easyec.sika.ss.ExcelFactory;
import com.tumi.data.poi.config.PoiProperties;
import com.tumi.data.poi.config.XmlProperties;
import com.tumi.data.poi.domain.InventoryWorkDataFile;
import com.tumi.data.poi.domain.ProdLegacy;
import com.tumi.data.poi.domain.ProductWorkDataFile;
import com.tumi.data.poi.domain.impl.InventoryWorkDataFileImpl;
import com.tumi.data.poi.domain.impl.ProdLegacyImpl;
import com.tumi.data.poi.domain.impl.ProductWorkDataFileImpl;
import com.tumi.data.poi.handler.InventoryHandler;
import com.tumi.data.poi.handler.NoMonogramsProdTypeHandler;
import com.tumi.data.poi.service.product.TumiProductService;
import com.tumi.data.poi.service.product.impl.TumiProductServiceImpl;
import com.tumi.data.poi.service.stream.FileOpService;
import com.tumi.data.poi.service.xml.XmlService;
import com.tumi.data.poi.xmlInventoryData.Inventory;
import com.tumi.data.poi.xmlInventoryData.InventoryList;
import com.tumi.data.poi.xmlInventoryData.Record;
import com.tumi.data.poi.xmlInventoryData.Records;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

/**
 * @author jefferychan
 */
@Service("XmlService")
public class XmlServiceImpl implements XmlService {

    private static final Logger logger = LoggerFactory.getLogger(XmlServiceImpl.class);
    @Resource
    private XmlProperties xmlProperties;

    @Resource
    private TumiProductService tumiProductService;

    @Resource
    private FileOpService fileOpService;

    @Override
    public void scanInventoryFile(String filePath) throws Exception {

        List<Inventory> inventories = new ArrayList<>();
        List<File> inventoryFile = fileOpService.scanFiles(filePath);
        for (File file : inventoryFile) {
            JAXBContext context = JAXBContext.newInstance(Inventory.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Inventory inventory = (Inventory) unmarshaller.unmarshal(file);
            inventories.add(inventory);
        }
        List<Record> recordList = differentRecords(inventories);
        Inventory inventory = new Inventory();
        InventoryList inventoryList = new InventoryList();
        Records records = new Records();
        inventoryList.setRecords(records);
        inventory.setInventoryList(inventoryList);
        records.setRecords(recordList);
        generateXML(Inventory.class,inventory);
    }


    //生成XML
    public  void generateXML(final Class feedClass, final Object feedObject)

    {
        try
        {
            final JAXBContext context = JAXBContext.newInstance(feedClass);
            final Marshaller marshaller = context.createMarshaller();
            final StringWriter stringWriter = new StringWriter();
            marshaller.marshal(feedObject, stringWriter);

            //final File file = createFile(fileLocation);
            String fileName=xmlProperties.getInventoryDifferentXmlFile()+"errorInventory.xml";
            File file = new File(fileName);
            final File parent = file.getParentFile();
            if (!parent.exists() && !parent.mkdirs())
            {
                logger.error("Error While creating dirs.");
            }

            FileWriter writer = null;
            try
            {
                writer = new FileWriter(file);
                writer.write(stringWriter.toString());
            }
            catch (final IOException e)
            {
                logger.error("Error while writing to file", e);
            }
            finally
            {
                try
                {
                    writer.close();
                }
                catch (final IOException e)
                {
                    logger.error("Error while closing writer", e);
                }
            }

        }
        catch (final JAXBException e)
        {
            logger.error("Error while marshalling", e);
        }
    }


    //查出有问题的库存
    private List<Record> differentRecords(List<Inventory> inventoryList) throws Exception {
        List<List<WorkData>> listList = new ArrayList<>();
        List<Record> recordList = new ArrayList<>();
        List<ProdLegacy> prodLegacies = mergeSKUAndLegacy();
        for (Inventory inventory : inventoryList) {
            InventoryList list = inventory.getInventoryList();
            if (null != list) {
                Records records = list.getRecords();
                if (null == records) {
                    continue;
                }
                List<Record> recordsRecords = records.getRecords();
                if (CollectionUtils.isNotEmpty(recordsRecords)) {
                    for (Record record : recordsRecords) {
                        boolean isDifferent = false;
                        String legacySkuCode = record.getProductId();
                        if (!StringUtils.isBlank(legacySkuCode)) {

                            for (ProdLegacy prodLegacy : prodLegacies) {
                                if (StringUtils.equalsIgnoreCase(legacySkuCode, prodLegacy.getLegacysku())) {

                                    String amount = record.getAmount();
                                    boolean numeric = StringUtils.isNumeric(amount);
                                    boolean n = StringUtils.isNumeric(prodLegacy.getStockNumber());
                                    if (numeric && n) {

                                        if (!(Integer.valueOf(amount) == Integer.valueOf(prodLegacy.getStockNumber()))) {
                                            recordList.add(record);
                                            isDifferent = true;
                                        }
                                    }
                                }
                            }

                        }
                        listList.add(getXmlWorkData(record, isDifferent));

                    }
                }
            }

        }

            String fileParh = xmlProperties.getInventoryResultExcelFile();
            String fileName = "result";
            File dir = new File(fileParh + File.separator + fileName + "—v.xlsx");
            File parent = dir.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            String templete = "template/inventory.xlsx";
//            List<WorkData> workDatas = listList.get(i);
//            InventoryWorkDataFile resultData = new InventoryWorkDataFileImpl();
//            resultData.addData(workDatas);
            logger.info("success result data num : 【" + CollectionUtils.size(listList) + "】");
            fileOpService.prodFileDownload(dir, templete, listList);


        logger.info("error result data num : 【" + CollectionUtils.size(recordList) + "】");
        return recordList;
    }

//    private  List<List<WorkData>>  getExcelWorkData(){
//
//    }

    private List<WorkData> getXmlWorkData(Record record, boolean isDifferent) {
        List<WorkData> resultList = new ArrayList<>();
        resultList.add(new DefaultWorkData(record.getProductId()));
        resultList.add(new DefaultWorkData(record.getAmount()));

        if (isDifferent) {
            resultList.add(new DefaultWorkData("E"));
        }else{
            resultList.add(new DefaultWorkData("S"));
        }
        return resultList;
    }

    public List<ProdLegacy> mergeSKUAndLegacy() throws Exception {
        List<File> stockSkuList = fileOpService.scanFiles(xmlProperties.getInventoryHybrisFile());
        Map<String, ProdLegacy> stockLegacyMap = new HashMap<>();
        for (File file : stockSkuList) {
            if (file.isFile()&&!file.isHidden()) {
                stockLegacyMap.putAll(getStockSku(file, true));
            }
        }

        List<File> prodLegacyList = fileOpService.scanFiles(xmlProperties.getProductLegacyskuFile());
        Map<String, ProdLegacy> prodLegacyMap = new HashMap<>();
        for (File file : prodLegacyList) {
            if (file.isFile()&&!file.isHidden()) {
                prodLegacyMap.putAll(getStockSku(file, false));
            }
        }

        List<ProdLegacy> prodLegacies = new ArrayList<>();
        for (Map.Entry<String, ProdLegacy> stockEntry : stockLegacyMap.entrySet()) {
            String productSku = stockEntry.getKey();
            ProdLegacy stockLegacy = stockEntry.getValue();
            if (stockLegacy==null){
                logger.info("stockLegacy is null "+ productSku);
                continue;
            }
            ProdLegacy prodLegacy = prodLegacyMap.get(productSku);
            if (prodLegacy==null){
                logger.info("ProdLegacy is null "+ productSku);
                continue;
            }
            ProdLegacy prodLegacy1=new ProdLegacyImpl(prodLegacy.getLegacysku(),stockLegacy.getStockNumber());
            prodLegacies.add(prodLegacy1);
        }
        return prodLegacies;

    }

    private Map<String, ProdLegacy> getStockSku(File file, boolean isStock) throws Exception {
        InputStream in = tumiProductService.loadFromLocal(file);
        InventoryHandler handler = new InventoryHandler(isStock);
        WorkbookReader reader = new WorkbookReader();
        reader.add(handler);
        ExcelFactory.getInstance().read(in, reader);
        return handler.getProductLegacy();
    }

}
