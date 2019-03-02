package com.tumi.data.poi.impex;


import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;


/**
 * 商品导入转化类
 */
public class CvHKProductImportCovert extends CvImportCovert {
    private static final Logger logger = LoggerFactory.getLogger(CvHKProductImportCovert.class);

    private static enum IMPEXTYPE {
        HeaderImpex,
        ApparelProductImpex,
        ApparelStyleVariantProductImpex,
        ApparelSizeVariantHkProductImpex
    }

    public CvHKProductImportCovert(File xlsxFile, String downloadPath, Set<String> noMonograms) throws Exception {
        super.setNoMonograms(noMonograms);
        makeImpexFile(xlsxFile, downloadPath);
    }

    @Override
    protected Map<Enum, String[]> getImpexHeader() throws Exception {
        final Map<Enum, String[]> m = new HashMap<Enum, String[]>();
        m.put(IMPEXTYPE.HeaderImpex, new String[]{
                "$productCatalog=cvacceleratorplusHKProductCatalog",
                "$productCatalogName=CV AccPlus Apparel Product Catalog",
                "$catalogVersion=catalogversion(catalog(id[default=$productCatalog]),version[default='Staged'])[unique=true,default=$productCatalog:Staged]",
                "$prices=europe1prices[translator=de.hybris.platform.europe1.jalo.impex.Europe1PricesTranslator]",
                "$baseProduct=baseProduct(code, $catalogVersion)",
                "$approved=approvalstatus(code)[default='approved']",
                "$taxGroup=Europe1PriceFactory_PTG(code)[default=hk-sales-tax-full]",
                "$lang=en",
                "$systemName=TumiClassification;",
                "$systemVersion=1.0;",
                "$catVer=catalogVersion(version[default='Staged'],catalog(id[default=$productCatalog]));",
                "$clSysVer=catalogVersion(version[default='$systemVersion'],catalog(id[default='$systemName']));",
                "$YCL=system='$systemName',version='$systemVersion',translator=de.hybris.platform.catalog.jalo.classification.impex.ClassificationAttributeTranslator;"
        });


        m.put(IMPEXTYPE.ApparelProductImpex, new String[]{
                "INSERT_UPDATE ApparelProduct;code[unique=true];supercategories(code,$catalogVersion)[mode=append];$catalogVersion"
        });

        m.put(IMPEXTYPE.ApparelStyleVariantProductImpex, new String[]{
                "INSERT_UPDATE ApparelStyleVariantProduct;code[unique=true];name[lang=en];name[lang=zh_TW];supercategories(code,$catalogVersion)[mode=append]" +
                        ";newArrival;badge[lang=en];badge[lang=zh_TW]" +
                        ";@levelThreeType[$YCL][lang=en];@levelThreeType[$YCL][lang=zh_TW]" +
                        ";@genders[$YCL]" +
                        ";approvalstatus(code)[default='approved'];$catalogVersion"
        });

        m.put(IMPEXTYPE.ApparelSizeVariantHkProductImpex, new String[]{
                "INSERT_UPDATE ApparelSizeVariantProduct;code[unique=true];$baseProduct;onlineDate[dateformat=MM-dd-yyyy];offlineDate[dateformat=MM-dd-yyyy]" +
                        ";name[lang=en];name[lang=zh_TW]" +
                        ";keywords(keyword,$catalogVersion)[lang=zh_TW][translator=com.crossview.hybris.cvaccplus.core.impex.translator.ProductKeywordTranslator]" +
                        ";keywords(keyword,$catalogVersion)[lang=en][translator=com.crossview.hybris.cvaccplus.core.impex.translator.ProductKeywordTranslator]" +
                        ";@suitorSection[$YCL];@primaryMaterial[$YCL][lang=en];@primaryMaterial[$YCL][lang=zh_TW]" +
                        ";@primaryMaterialContent[$YCL][lang=en];@primaryMaterialContent[$YCL][lang=zh_TW];@genders[$YCL]" +
                        ";productFeatureIcons(code)[mode=append];supercategories(code,$catalogVersion)[mode=append]" +
                        ";@removableLock[$YCL][default=false]" +
                        ";@levelThreeType[$YCL][lang=en];@levelThreeType[$YCL][lang=zh_TW]" +
                        ";description[lang=en];description[lang=zh_TW]" +
                        ";@handleDropLengthInches[$YCL];@handleDropLengthCms[$YCL]" +
                        ";@shoulderStrapTotalLengthInches[$YCL];@shoulderStrapTotalLengthCms[$YCL]" +
                        ";@liningMaterial[$YCL][lang=en];@liningMaterial[$YCL][lang=zh_TW];@liningMaterialContent[$YCL][lang=en];@liningMaterialContent[$YCL][lang=zh_TW]" +
                        ";@pouchMaterialContent[$YCL][lang=en];@pouchMaterialContent[$YCL][lang=zh_TW]" +
                        ";@pouchLiningContent[$YCL][lang=en];@pouchLiningContent[$YCL][lang=zh_TW]" +
                        ";@exteriorFeatures[$YCL][lang=en];@exteriorFeatures[$YCL][lang=zh_TW]" +
                        ";@interiorFeatures[$YCL][lang=en];@interiorFeatures[$YCL][lang=zh_TW]" +
                        ";@addABagType[$YCL][lang=en];@addABagType[$YCL][lang=zh_TW];newArrival" +
                        ";badge[lang=en];badge[lang=zh_TW];monogramable;monogramPatch;monogramLuggageTag" +
                        ";approvalstatus(code)[default='approved'];$catalogVersion"
        });

        return m;
    }


    @Override
    public List<String> combineImpexLine(XSSFSheet sheet, int sheetIndex) throws Exception {

        List<String> allList = new LinkedList<String>();
        if (sheetIndex == 0) {
            allList.addAll(Arrays.asList(getImpexHeader().get(IMPEXTYPE.HeaderImpex)));
        }
        combineApparelSizeVariantProductImpexLine(sheet, sheetIndex, allList);
        combineApparelProductImpexLine(sheet, sheetIndex, allList);
        combineApparelStyleVariantProductImpexLine(sheet, sheetIndex, allList);


        return allList;

    }

    private void combineApparelSizeVariantProductImpexLine(XSSFSheet sheet, int sheetIndex, List<String> allList) throws Exception {

        allList.add(getImpexHeader().get(IMPEXTYPE.ApparelSizeVariantHkProductImpex)[0]);
        List<String[]> data = new ArrayList<String[]>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                int totalCell = row.getLastCellNum();
                if (totalCell > 49) {
                    String[] line = new String[46];
//                    line[0] = getCellValue(row, 0); //sku legacy
                    line[0] = getCellValue(row, 3);//skuVarlantCode
                    line[1] = getCellValue(row, 4);//styleCode>>code
                    //line[1] = getCellValue(row, 5);//baseCode>>$baseProduct
                    line[2] = getCellDateValue(row, 6);//onlineDate
                    line[3] = getCellDateValue(row, 7);//offlenDate
                    line[4] = getCellValue(row, 8);//Name en
                    line[5] = getCellValue(row, 9);//name cn

                    line[6] = getCellValue(row, 10);//keyword cn
                    line[7] = getCellValue(row, 11);//keyword en2
                    line[8] = getCellValue(row, 12);//suitorSectio

                    line[9] = getCellValue(row, 13);//primarymater en
                    line[10] = getCellValue(row, 14);//primary Material cn

                    line[11] = getCellValue(row, 15);// primaryMaterlalContent en
                    line[12] = getCellValue(row, 16);//primary Materlalcontent cn
                    line[13] = getCellValue(row, 17);// genders

                    line[14] = getCellValue(row, 18);//productFeatureIcons
                    line[15] = getCellValue(row, 19);//supercategories

//                    line[16] = getCellValue(row, 20);//producttype en
//                    line[17] = getCellValue(row, 21);//productType cn
                    line[16] = getCellValue(row, 22);//removablelock
                    line[17] = getCellValue(row, 23);//levelThreeType en
                    line[18] = getCellValue(row, 24);//levelThreeType = productType cn

                    line[19] = getCellValue(row, 25);// Description en
                    line[20] = getCellValue(row, 26);//description cn

                    line[21] = getCellValue(row, 27);// handleDropLengthInches
                    line[22] = getCellValue(row, 28);// handleDropLengthCms

                    line[23] = getCellValue(row, 29);//shoulderStrapTotalLengthInches
                    line[24] = getCellValue(row, 30);//shoulderStrapTotalLengthCms

                    line[25] = getCellValue(row, 31);// liningMateria en
                    line[26] = getCellValue(row, 32);// liningMateria cn
                    line[27] = getCellValue(row, 33);// LiningMateral Content en
                    line[28] = getCellValue(row, 34);// liningMaterl Content cn

                    line[29] = getCellValue(row, 35);//productMateralContent en**
                    line[30] = getCellValue(row, 36);//productMaterlConeter Cn **
                    line[31] = getCellValue(row, 37);//pruching COntent en**
                    line[32] = getCellValue(row, 38);//proching Content cn**

                    line[33] = getCellValue(row, 39);//exteriorFeatures en
                    line[34] = getCellValue(row, 40);//exteriorFeatures cn
                    line[35] = getCellValue(row, 41);//interiorFeatures en
                    line[36] = getCellValue(row, 42);//interiorFeatures cn
                    line[37] = getCellValue(row, 43);// addABagType en
                    line[38] = getCellValue(row, 44);//addABagType cn
                    line[39] = getCellValue(row, 45);//new Arravel
                    line[40] = getCellValue(row, 46);//badge en
                    line[41] = getCellValue(row, 47);//badge cn
                    boolean noMonogram = isNoMonogram(getCellValue(row, 20));
                    line[42] = noMonogram ? "N" : getCellValue(row, 48);//monogramable
                    line[43] = noMonogram ? "N" : getCellValue(row, 49);//monogramPatch
                    line[44] = noMonogram ? "N" : getCellValue(row, 50);//monogramluggagetag

                    line[45] = "approved";//approval status

                    data.add(line);
                }

            }

        }
        strings2ListString(data, allList);
    }


    private void combineApparelProductImpexLine(XSSFSheet sheet, int sheetIndex, List<String> allList) throws Exception {

        allList.add(getImpexHeader().get(IMPEXTYPE.ApparelProductImpex)[0]);
        List<String[]> data = new ArrayList<String[]>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                int totalCell = row.getLastCellNum();
                if (totalCell > 20) {
                    String[] line = new String[3];
                    //baseproduct
                    line[0] = getCellValue(row, 5);
                    //supercategories
                    line[1] = getCellValue(row, 19);
                    data.add(line);
                }

            }

        }

        strings2ListString(data, allList);

    }


    private void combineApparelStyleVariantProductImpexLine(XSSFSheet sheet, int sheetIndex, List<String> allList) throws Exception {

        allList.add(getImpexHeader().get(IMPEXTYPE.ApparelStyleVariantProductImpex)[0]);
        List<String[]> data = new ArrayList<String[]>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            if (row != null) {
                int totalCell = row.getLastCellNum();
                if (totalCell > 20) {
                    String[] line = new String[18];
                    line[0] = getCellValue(row, 4);//baseProductse
                    line[1] = getCellValue(row, 8);//name en
                    line[2] = getCellValue(row, 9);//name cn
                    line[3] = getCellValue(row, 19);//supercategories
                    line[4] = getCellValue(row, 45);//newArrival
                    line[5] = getCellValue(row, 46);//badge en
                    line[6] = getCellValue(row, 47);//badge cn
                    line[7] = getCellValue(row, 23);//levelThreeType en
                    line[8] = getCellValue(row, 24);//levelThreeType = productType zh_TW
                    line[9] = getCellValue(row, 17);// genders
                    line[10] = "approved";//approval status
                    data.add(line);
                }

            }

        }

        strings2ListString(data, allList);

    }

}
