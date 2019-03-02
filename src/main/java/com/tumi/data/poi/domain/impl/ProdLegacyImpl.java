package com.tumi.data.poi.domain.impl;

import com.tumi.data.poi.domain.ProdLegacy;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 2:35 PM
 * @describe:
 */
public class ProdLegacyImpl implements ProdLegacy {

    private String legacysku;
    private String stockNumber;


    public ProdLegacyImpl(String legacysku, String stockNumber) {
        this.legacysku = legacysku;
        this.stockNumber = stockNumber;
    }

    @Override
    public String getLegacysku() {
        return legacysku;
    }

    @Override
    public void setLegacysku(String legacysku) {
        this.legacysku = legacysku;
    }

    @Override
    public String getStockNumber() {
        return stockNumber;
    }

    @Override
    public void setStockNumber(String stockNumber) {
        this.stockNumber = stockNumber;
    }
}
