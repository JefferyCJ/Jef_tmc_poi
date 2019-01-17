package com.tumi.data.poi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tumi")
public class PoiProperties {

    private String productFile;
    private String categoryFile;

    private String resultBaseFile;
    private String resultNoPictureFile;
    private String categoryColumn;
    private String styleCodeColumn;
    private String onlineDateColumn;
    private String offlineDateColumn;

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

    public String getResultBaseFile() {
        return resultBaseFile;
    }

    public void setResultBaseFile(String resultBaseFile) {
        this.resultBaseFile = resultBaseFile;
    }

    public String getResultNoPictureFile() {
        return resultNoPictureFile;
    }

    public void setResultNoPictureFile(String resultNoPictureFile) {
        this.resultNoPictureFile = resultNoPictureFile;
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
}
