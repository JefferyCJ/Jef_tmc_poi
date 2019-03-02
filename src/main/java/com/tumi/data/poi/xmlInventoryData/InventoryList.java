package com.tumi.data.poi.xmlInventoryData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name ="inventory-list")
public class InventoryList {
    private Records records;

    @XmlElement(name = "records")
    public Records getRecords() {
        return records;
    }

    public void setRecords(Records records) {
        this.records = records;
    }
}
