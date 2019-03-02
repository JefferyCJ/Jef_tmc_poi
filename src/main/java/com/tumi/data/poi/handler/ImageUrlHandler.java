package com.tumi.data.poi.handler;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.WorkbookRowHandler;
import com.googlecode.easyec.sika.WorkingException;
import com.googlecode.easyec.sika.converters.Object2StringConverter;
import com.googlecode.easyec.sika.mappings.UnknownColumnException;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.googlecode.easyec.sika.event.WorkbookBlankRowListener.DEFAULT;
import static com.googlecode.easyec.sika.mappings.ColumnEvaluatorFactory.evaluateWorkData;

/**
 * @author jefferychan
 */
public class ImageUrlHandler extends WorkbookRowHandler {
    private Set<String> imageUrls = new HashSet<>();

    @Override
    public void doInit() throws WorkingException {
        setBlankRowListener(DEFAULT);
    }

    @Override
    public boolean populate(int i, List<WorkData> list) throws WorkingException {
        String url = getImageUrl(list);
        if (StringUtils.isNotBlank(url)) {
            imageUrls.add(url);
        }

        return true;
    }

    public Set<String> getImageUrls() {
        return imageUrls;
    }

    private String getImageUrl(List<WorkData> list) throws UnknownColumnException {
        String val = evaluateWorkData(list, "B").getValue(new Object2StringConverter());
        if (val != null) val = StringUtils.trim(val);
        return val;
    }
}
