package com.tumi.data.poi.domain;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/16 3:07 PM
 * @describe:
 */
public interface ProdLegacy extends Serializable {


    String getLegacysku();

    void setLegacysku(String legacysku);

    String getStockNumber();

    void setStockNumber(String stockNumber);
}
