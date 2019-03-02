
package com.tumi.data.poi.impex;


import com.tumi.data.poi.utils.GeneralUtil;
import com.tumi.data.poi.utils.Snippet;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据导入类
 */
public abstract class CvImportCovert {
    private Set<String> noMonograms;

    protected abstract Map<Enum, String[]> getImpexHeader() throws Exception;

    public final static DecimalFormat DF0HU = new DecimalFormat("0");
    public final static DecimalFormat DF0U = new DecimalFormat("0");
    public final static DecimalFormat DF1HU = new DecimalFormat("0.0");
    public final static DecimalFormat DF6HU = new DecimalFormat("0.0000000");


    private static final Logger LOG = LoggerFactory.getLogger(CvImportCovert.class.getName());

    static {
        DF0HU.setRoundingMode(RoundingMode.HALF_UP);
        DF0U.setRoundingMode(RoundingMode.UP);
        DF1HU.setRoundingMode(RoundingMode.HALF_UP);
        DF6HU.setRoundingMode(RoundingMode.HALF_UP);
    }

    public enum COLUNMTYPE {
        NUMBER,
        STRING,
    }

    protected boolean makeImpexFile(File xlsxFile, String downloadPath) throws Exception {
        InputStream is = null;
        boolean success = true;
        try {
            is = new FileInputStream(xlsxFile);

            OPCPackage pkg = OPCPackage.open(is);
            XSSFWorkbook wb = new XSSFWorkbook(pkg);
            //循环每个sheet
            String errormessage = "";
            for (int sheetIndex = 0; sheetIndex < wb.getNumberOfSheets(); sheetIndex++) {
                try {
                    XSSFSheet st = wb.getSheetAt(sheetIndex);
                    //将所有的语句拼接到一起
                    //CvHKProductImportCovert cvHKProductImportCovert=new CvHKProductImportCovert();
                    List<String> allList = combineImpexLine(st, sheetIndex);
                    String fileName = xlsxFile.getName();
                    String nameSuffix = fileName.substring(fileName.lastIndexOf("."));
                    fileName.replace(nameSuffix, "");
                    Snippet.createCSVFile(downloadPath, xlsxFile.getName() + sheetIndex, allList, "UTF-8");
                } catch (Exception e) {
                    errormessage += "sheet" + sheetIndex + ":" + GeneralUtil.getStackMsg(e) + "\n";
                    LOG.error(e.getMessage(), e);
                    throw new Exception(errormessage);
                }
            }
            if (!errormessage.equals("")) {
                throw new Exception(errormessage);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new Exception(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOG.error(e.getMessage(), e);
                }
            }
        }
        return success;
    }


    protected String getCellDateValue(XSSFRow row, int num) {
        if (null != row) {
            Date date = row.getCell(num).getDateCellValue();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            return sdf.format(date);
        }
        return "";
    }


    public String getCellValue(XSSFRow row, int num) {
        Cell cell = row.getCell(num);
        String cellValue = StringUtils.trimToEmpty(getCellValue(cell, DF0HU, COLUNMTYPE.STRING));
        return StringUtils.equalsIgnoreCase(cellValue, "0") ? "" : cellValue;
    }

    public String getCellValue(Cell cell, DecimalFormat df, COLUNMTYPE colunmtype) {
        String result = "";
        try {
            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK && cell.getCellTypeEnum() != CellType.ERROR) {
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    return cell.getStringCellValue().trim().replaceAll(";", "；").replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\"", "“");
                }
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("m/d/yy")) {
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.format(date);
                    }
                    return df.format(cell.getNumericCellValue());
                }
                if (cell.getCellTypeEnum() == CellType.FORMULA) {
                    try {
                        return df.format(cell.getNumericCellValue());
                    } catch (IllegalStateException e) {
                        return String.valueOf(cell.getRichStringCellValue()).trim().replaceAll("\r", "").replaceAll("\t", "").replaceAll("\n", "").replaceAll(",", "，").replaceAll(";", "；").replaceAll("\"", "“");
                    }
                }
            }
            if (!colunmtype.equals(COLUNMTYPE.STRING)) {
                result = "0";
            }
        } catch (Exception e) {
            LOG.error("this row is not find data:" + cell.getRowIndex());
        }
        return result;
    }


    public boolean isNoMonogram(String importProductTypes) {
        if (StringUtils.isNotBlank(importProductTypes)) {
            String[] importProductType = importProductTypes.split(",");
            if (null != importProductType) {
                for (String productType : importProductType) {
                    if (noMonograms != null) {
                        for (String noMoProductType : noMonograms) {
                            if (StringUtils.equalsIgnoreCase(productType, noMoProductType)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public void setNoMonograms(Set<String> noMonograms) {
        this.noMonograms = noMonograms;
    }


    public abstract List<String> combineImpexLine(XSSFSheet st, int sheetIndex) throws Exception;


    protected void strings2ListString(List<String[]> data, List<String> allList) {
        for (String[] line : data) {
            StringBuffer sb = new StringBuffer();
            for (String field : line) {
                sb.append(";");
                if (field != null) {
                    sb.append(field);
                }

            }
            allList.add(sb.toString());
        }
    }

}

