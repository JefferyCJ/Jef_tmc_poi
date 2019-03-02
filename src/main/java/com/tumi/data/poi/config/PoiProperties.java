package com.tumi.data.poi.config;

import com.tumi.data.poi.service.product.TumiProductService;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tumi")
public class PoiProperties {
    private String productFile;
    private String categoryFile;
    private String resultFile;
    private String errorFile;
    private String impexHK;
    private String impexCN;
    private String categoryColumn;
    private String styleCodeColumn;
    private String onlineDateColumn;
    private String offlineDateColumn;
    private String nameENColumn;
    private String nameTradCNColumn;
    private String gendersColumn;
    private String suitorSectionColumn;
    private String errorReasonColumn;
    private String replaceDataColumn;
    private String boolDataColumn;
    private String multipleLanguageColumn;
    private String noMonograms;
    private String noMonogramsFile;


    public String getSuitorSectionColumn() {
        return suitorSectionColumn;
    }

    public void setSuitorSectionColumn(String suitorSectionColumn) {
        this.suitorSectionColumn = suitorSectionColumn;
    }

    public String getGendersColumn() {
        return gendersColumn;
    }

    public void setGendersColumn(String gendersColumn) {
        this.gendersColumn = gendersColumn;
    }

    public String getNameENColumn() {
        return nameENColumn;
    }

    public void setNameENColumn(String nameENColumn) {
        this.nameENColumn = nameENColumn;
    }

    public String getNameTradCNColumn() {
        return nameTradCNColumn;
    }

    public void setNameTradCNColumn(String nameTradCNColumn) {
        this.nameTradCNColumn = nameTradCNColumn;
    }

    public String getProductFile() {
        return productFile;
    }

    public void setProductFile(String productFile) {
        this.productFile = productFile;
    }

    public String getCategoryFile() {
        return categoryFile;
    }

    public void setCategoryFile(String categoryFile) {
        this.categoryFile = categoryFile;
    }

    public String getCategoryColumn() {
        return categoryColumn;
    }

    public void setCategoryColumn(String categoryColumn) {
        this.categoryColumn = categoryColumn;
    }

    public String getStyleCodeColumn() {
        return styleCodeColumn;
    }

    public void setStyleCodeColumn(String styleCodeColumn) {
        this.styleCodeColumn = styleCodeColumn;
    }

    public String getResultFile() {
        return resultFile;
    }

    public void setResultFile(String resultFile) {
        this.resultFile = resultFile;
    }

    public String getErrorFile() {
        return errorFile;
    }

    public void setErrorFile(String errorFile) {
        this.errorFile = errorFile;
    }

    public String getImpexHK() {
        return impexHK;
    }

    public void setImpexHK(String impexHK) {
        this.impexHK = impexHK;
    }

    public String getImpexCN() {
        return impexCN;
    }

    public void setImpexCN(String impexCN) {
        this.impexCN = impexCN;
    }

    public String getOnlineDateColumn() {
        return onlineDateColumn;
    }

    public void setOnlineDateColumn(String onlineDateColumn) {
        this.onlineDateColumn = onlineDateColumn;
    }

    public String getOfflineDateColumn() {
        return offlineDateColumn;
    }

    public void setOfflineDateColumn(String offlineDateColumn) {
        this.offlineDateColumn = offlineDateColumn;
    }

    public String getErrorReasonColumn() {
        return errorReasonColumn;
    }

    public void setErrorReasonColumn(String errorReasonColumn) {
        this.errorReasonColumn = errorReasonColumn;
    }

    public String getReplaceDataColumn() {
        return replaceDataColumn;
    }

    public void setReplaceDataColumn(String replaceDataColumn) {
        this.replaceDataColumn = replaceDataColumn;
    }

    public String getBoolDataColumn() {
        return boolDataColumn;
    }

    public void setBoolDataColumn(String boolDataColumn) {
        this.boolDataColumn = boolDataColumn;
    }

    public String getMultipleLanguageColumn() {
        return multipleLanguageColumn;
    }

    public void setMultipleLanguageColumn(String multipleLanguageColumn) {
        this.multipleLanguageColumn = multipleLanguageColumn;
    }

    public String getImpexPath(String site) {
        return StringUtils.equalsIgnoreCase(site, TumiProductService.CN) ? this.getImpexCN() : this.getImpexHK();
    }

    public void setNoMonograms(String noMonograms) {
        this.noMonograms = noMonograms;
    }

    public String[] getNoMonograms() {
        String[] result = null;
        if (StringUtils.isNotBlank(noMonograms)) {
            result = noMonograms.split(",");
        }
        return result;
    }

    public String getNoMonogramsFile() {
        return noMonogramsFile;
    }

    public void setNoMonogramsFile(String noMonogramsFile) {
        this.noMonogramsFile = noMonogramsFile;
    }
}
