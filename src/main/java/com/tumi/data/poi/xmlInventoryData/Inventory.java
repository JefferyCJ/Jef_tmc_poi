package com.tumi.data.poi.xmlInventoryData;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jinpeng
 */
@XmlRootElement(name ="inventory")
public class Inventory {
private InventoryList inventoryList;

    @XmlElement(name = "inventory-list")
    public InventoryList getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(InventoryList inventoryList) {
        this.inventoryList = inventoryList;
    }
}
