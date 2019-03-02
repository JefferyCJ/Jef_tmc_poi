package com.tumi.data.poi.impex;


import org.slf4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;


/**
 * 商品导入转化类
 */
public class CvCNProductImportCovert extends CvImportCovert {

    private static final Logger logger = LoggerFactory.getLogger(CvCNProductImportCovert.class);

    private static enum IMPEXTYPE {
        HeaderImpex,
        ApparelProductImpex,
        ApparelStyleVariantProductImpex,
        ApparelSizeVariantCNProductImpex
    }


    public CvCNProductImportCovert(File xlsxFile, String downloadPath, Set<String> noMonograms) throws Exception {
        super.setNoMonograms(noMonograms);
        makeImpexFile(xlsxFile, downloadPath);
    }

    @Override
    protected Map<Enum, String[]> getImpexHeader() throws Exception {
        final Map<Enum, String[]> m = new HashMap<Enum, String[]>();
        m.put(CvCNProductImportCovert.IMPEXTYPE.HeaderImpex, new String[]{
                "$productCatalog=cvacceleratorplusProductCatalog",
                "$productCatalogName=CV AccPlus Apparel Product Catalog",
                "$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]",
                "$prices=europe1prices[translator=de.hybris.platform.europe1.jalo.impex.Europe1PricesTranslator]",
                "$baseProduct=baseProduct(code, $catalogVersion)",
                "$approved=approvalstatus(code)[default='approved']",
                "$taxGroup=Europe1PriceFactory_PTG(code)[default=us-sales-tax-full]",
                "$lang=zh",
                "$systemName=TumiClassification;",
                "$systemVersion=1.0;",
                "$catVer=catalogVersion(version[default='Staged'],catalog(id[default='cvacceleratorplusProductCatalog']));",
                "$clSysVer=catalogVersion(version[default='$systemVersion'],catalog(id[default='$systemName']));",
                "$YCL=system='$systemName',version='$systemVersion',translator=de.hybris.platform.catalog.jalo.classification.impex.ClassificationAttributeTranslator;"
        });


        m.put(CvCNProductImportCovert.IMPEXTYPE.ApparelProductImpex, new String[]{
                "INSERT_UPDATE ApparelProduct;code[unique=true];supercategories(code,$catalogVersion)[mode=append];$catalogVersion"
        });

        m.put(CvCNProductImportCovert.IMPEXTYPE.ApparelStyleVariantProductImpex, new String[]{
                "INSERT_UPDATE ApparelStyleVariantProduct;code[unique=true];name[lang=zh];supercategories(code,$catalogVersion)[mode=append]" +
                        ";newArrival;badge[lang=zh]" +
                        ";@levelThreeType[$YCL][lang=zh]" +
                        ";@genders[$YCL]" +
                        ";approvalstatus(code)[default='approved'];$catalogVersion"
        });

        m.put(CvCNProductImportCovert.IMPEXTYPE.ApparelSizeVariantCNProductImpex, new String[]{
                "INSERT_UPDATE ApparelSizeVariantProduct;code[unique=true];$baseProduct;onlineDate[dateformat=MM-dd-yyyy];offlineDate[dateformat=MM-dd-yyyy]" +
                        ";name[lang=zh]" +
                        ";keywords(keyword,$catalogVersion)[lang=zh][translator=com.crossview.hybris.cvaccplus.core.impex.translator.ProductKeywordTranslator]" +
                        ";@suitorSection[$YCL];@primaryMaterial[$YCL][lang=zh]" +
                        ";@primaryMaterialContent[$YCL][lang=zh];@genders[$YCL]" +
                        ";productFeatureIcons(code)[mode=append];supercategories(code,$catalogVersion)[mode=append]" +
                        ";@removableLock[$YCL][default=false]" +
                        ";@levelThreeType[$YCL][lang=zh]" +
                        ";description[lang=zh]" +
                        ";@handleDropLengthInches[$YCL];@handleDropLengthCms[$YCL]" +
                        ";@shoulderStrapTotalLengthInches[$YCL];@shoulderStrapTotalLengthCms[$YCL]" +
                        ";@liningMaterial[$YCL][lang=zh];@liningMaterialContent[$YCL][lang=zh]" +
                        ";@pouchMaterialContent[$YCL][lang=zh]" +
                        ";@pouchLiningContent[$YCL][lang=zh]" +
                        ";@exteriorFeatures[$YCL][lang=zh]" +
                        ";@interiorFeatures[$YCL][lang=zh]" +
                        ";@addABagType[$YCL][lang=zh];newArrival" +
                        ";badge[lang=zh]" +
                        ";monogramable;monogramPatch;monogramLuggageTag" +
                        ";approvalstatus(code)[default='approved'];$catalogVersion"
        });

        return m;
    }


    @Override
    public List<String> combineImpexLine(XSSFSheet sheet, int sheetIndex) throws Exception {

        List<String> allList = new LinkedList<String>();
        if (sheetIndex == 0) {
            allList.addAll(Arrays.asList(getImpexHeader().get(CvCNProductImportCovert.IMPEXTYPE.HeaderImpex)));
        }
        combineApparelSizeVariantProductImpexLine(sheet, sheetIndex, allList);
        combineApparelProductImpexLine(sheet, sheetIndex, allList);
        combineApparelStyleVariantProductImpexLine(sheet, sheetIndex, allList);

        return allList;

    }

    private void combineApparelSizeVariantProductImpexLine(XSSFSheet sheet, int sheetIndex, List<String> allList) throws Exception {

        allList.add(getImpexHeader().get(CvCNProductImportCovert.IMPEXTYPE.ApparelSizeVariantCNProductImpex)[0]);
        List<String[]> data = new ArrayList<String[]>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                int totalCell = row.getLastCellNum();
                if (totalCell > 49) {
                    String[] line = new String[33];
                    try {
//                    line[0] = getCellValue(row, 0); //sku legacy
                        line[0] = getCellValue(row, 3);//skuVarlantCode
                        line[1] = getCellValue(row, 4);//styleCode>>code
                        //line[1] = getCellValue(row, 5);//baseCode>>$baseProduct
                        line[2] = getCellDateValue(row, 6);//onlineDate
                        line[3] = getCellDateValue(row, 7);//offlenDate
                        line[4] = getCellValue(row, 9);//name cn
                        line[5] = getCellValue(row, 11);//keyword cn
                        line[6] = getCellValue(row, 12);//suitorSectio
                        line[7] = getCellValue(row, 14);//primary Material cn
                        line[8] = getCellValue(row, 16);//primary Materlalcontent cn
                        line[9] = getCellValue(row, 17);// genders
                        line[10] = getCellValue(row, 18);//productFeatureIcons
                        line[11] = getCellValue(row, 19);//supercategories
//                    line[16] = getCellValue(row, 20);//producttype en
//                    line[17] = getCellValue(row, 21);//productType cn
                        line[12] = getCellValue(row, 22);//removablelock
                        line[13] = getCellValue(row, 24);//levelThreeType
                        line[14] = getCellValue(row, 26);//description cn
                        line[15] = getCellValue(row, 27);// handleDropLengthInches
                        line[16] = getCellValue(row, 28);// handleDropLengthCms
                        line[17] = getCellValue(row, 29);//shoulderStrapTotalLengthInches
                        line[18] = getCellValue(row, 30);//shoulderStrapTotalLengthCms
                        line[19] = getCellValue(row, 32);// liningMateria cn
                        line[20] = getCellValue(row, 34);// liningMaterl Content cn
                        line[21] = getCellValue(row, 36);//productMaterlConeter Cn **
                        line[22] = getCellValue(row, 38);//proching Content cn**
                        line[23] = getCellValue(row, 40);//exteriorFeatures cn
                        line[24] = getCellValue(row, 42);//interiorFeatures cn
                        line[25] = getCellValue(row, 44);//addABagType cn
                        line[26] = getCellValue(row, 45);//new Arravel
                        line[27] = getCellValue(row, 47);//badge cn
                        boolean noMonogram = isNoMonogram(getCellValue(row, 20));
                        line[28] = noMonogram ? "N" : getCellValue(row, 48);//monogramable
                        line[29] = noMonogram ? "N" : getCellValue(row, 49);//monogramPatch
                        line[30] = noMonogram ? "N" : getCellValue(row, 50);//monogramluggagetag
                        line[31] = "approved";//approval status
                    } catch (Exception e) {
                        logger.error("this row is not find data");
                        continue;
                    }
                    data.add(line);
                }
            }
        }
        strings2ListString(data, allList);
    }


    private void combineApparelProductImpexLine(XSSFSheet sheet, int sheetIndex, List<String> allList) throws Exception {

        allList.add(getImpexHeader().get(CvCNProductImportCovert.IMPEXTYPE.ApparelProductImpex)[0]);

        List<String[]> data = new ArrayList<String[]>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                int totalCell = row.getLastCellNum();
                if (totalCell > 20) {
                    String[] line = new String[3];
                    try {
                        //baseProduct
                        line[0] = getCellValue(row, 5);
                        //super categories
                        line[1] = getCellValue(row, 19);
                    } catch (Exception e) {
                        logger.error("this row is not find data");
                        continue;
                    }
                    data.add(line);
                }

            }

        }
        strings2ListString(data, allList);

    }


    private void combineApparelStyleVariantProductImpexLine(XSSFSheet sheet, int sheetIndex, List<String> allList) throws Exception {

        allList.add(getImpexHeader().get(CvCNProductImportCovert.IMPEXTYPE.ApparelStyleVariantProductImpex)[0]);
        List<String[]> data = new ArrayList<String[]>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                int totalCell = row.getLastCellNum();
                if (totalCell > 20) {
                    String[] line = new String[18];
                    try {
                        line[0] = getCellValue(row, 4);//baseProductse
                        line[1] = getCellValue(row, 9);//name cn
                        line[2] = getCellValue(row, 19);//supercategories
                        line[3] = getCellValue(row, 45);//newArrival
                        line[4] = getCellValue(row, 47);//badge cn
                        line[5] = getCellValue(row, 24);//levelThreeType
                        line[6] = getCellValue(row, 17);// genders
                        line[7] = "approved";//approval status
                    } catch (Exception e) {
                        logger.error("this row is not find data");
                        continue;
                    }
                    data.add(line);
                }

            }

        }

        strings2ListString(data, allList);
    }


    //创建目录
    private void mkdir(String dataFeedFileDir, String sapFileDir, String historyDir) {
        File file1 = new File(dataFeedFileDir);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        File file2 = new File(sapFileDir);
        if (!file2.exists()) {
            file2.mkdirs();
        }
        File file3 = new File(historyDir);
        if (!file3.exists()) {
            file3.mkdirs();
        }
    }

}
