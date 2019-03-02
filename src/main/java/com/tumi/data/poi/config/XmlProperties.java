package com.tumi.data.poi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "xml")
public class XmlProperties {
    private String inventorySftpFile;
    private String inventoryHybrisFile;
    private String productLegacyskuFile;
    private String inventoryResultExcelFile;
    private String inventoryDifferentXmlFile;

    public String getInventorySftpFile() {
        return inventorySftpFile;
    }

    public void setInventorySftpFile(String inventorySftpFile) {
        this.inventorySftpFile = inventorySftpFile;
    }

    public String getInventoryHybrisFile() {
        return inventoryHybrisFile;
    }

    public void setInventoryHybrisFile(String inventoryHybrisFile) {
        this.inventoryHybrisFile = inventoryHybrisFile;
    }

    public String getProductLegacyskuFile() {
        return productLegacyskuFile;
    }

    public void setProductLegacyskuFile(String productLegacyskuFile) {
        this.productLegacyskuFile = productLegacyskuFile;
    }

    public String getInventoryResultExcelFile() {
        return inventoryResultExcelFile;
    }

    public void setInventoryResultExcelFile(String inventoryResultExcelFile) {
        this.inventoryResultExcelFile = inventoryResultExcelFile;
    }

    public String getInventoryDifferentXmlFile() {
        return inventoryDifferentXmlFile;
    }

    public void setInventoryDifferentXmlFile(String inventoryDifferentXmlFile) {
        this.inventoryDifferentXmlFile = inventoryDifferentXmlFile;
    }
}
