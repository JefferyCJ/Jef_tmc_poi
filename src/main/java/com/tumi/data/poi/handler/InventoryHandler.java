package com.tumi.data.poi.handler;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.WorkbookRowHandler;
import com.googlecode.easyec.sika.WorkingException;
import com.googlecode.easyec.sika.converters.Object2StringConverter;
import com.googlecode.easyec.sika.mappings.UnknownColumnException;
import com.tumi.data.poi.domain.ProdLegacy;
import com.tumi.data.poi.domain.impl.ProdLegacyImpl;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.googlecode.easyec.sika.event.WorkbookBlankRowListener.DEFAULT;
import static com.googlecode.easyec.sika.mappings.ColumnEvaluatorFactory.evaluateWorkData;

/**
 * @author jefferychan
 */
public class InventoryHandler extends WorkbookRowHandler {
    private Map<String, ProdLegacy> productLegacy=new HashMap<>();
    private boolean isStock;

    public InventoryHandler(boolean isStock) {
        this.isStock = isStock;
    }

    @Override
    public void doInit() throws WorkingException {
        setBlankRowListener(DEFAULT);
    }

    @Override
    public boolean populate(int i, List<WorkData> list) throws WorkingException {
        String sku = getSku(list);
        String value = getStock(list);

        if (StringUtils.isBlank(sku)){
            return false;
        }
        ProdLegacyImpl prodLegacy = null;
        if (isStock){
            prodLegacy= new ProdLegacyImpl(null, value);
        }else {
            prodLegacy= new ProdLegacyImpl(value, null);
        }
        productLegacy.put(sku,prodLegacy);

        return true;
    }

    public Map<String, ProdLegacy> getProductLegacy() {
        return productLegacy;
    }

    private String getSku(List<WorkData> list) throws UnknownColumnException {
        String val = evaluateWorkData(list, "A").getValue(new Object2StringConverter());
        if (val != null) val = StringUtils.trim(val);
        return val;
    }

    private String getStock(List<WorkData> list) throws UnknownColumnException {
        String val = evaluateWorkData(list, "B").getValue(new Object2StringConverter());
        if (val != null) val = StringUtils.trim(val);
        return val;
    }
}
