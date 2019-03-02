package com.tumi.data.poi.handler;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.WorkbookRowHandler;
import com.googlecode.easyec.sika.WorkingException;
import com.googlecode.easyec.sika.data.DefaultWorkData;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.easyec.sika.event.WorkbookBlankRowListener.DEFAULT;

/**
 * @author jefferychan
 */
public class DefaultWorkbookHandler extends WorkbookRowHandler {

    private List<List<WorkData>> records = new ArrayList<>();

    @Override
    public void doInit() throws WorkingException {
        setBlankRowListener(DEFAULT);
    }

    @Override
    public boolean populate(int i, List<WorkData> list) throws WorkingException {
        records.add(copy(list));

        return true;
    }

    public List<List<WorkData>> getRecords() {
        return records;
    }

    private List<WorkData> copy(List<WorkData> list) {
        List<WorkData> copy = new ArrayList<>();

        list.forEach(data -> {
            copy.add(new DefaultWorkData(data.getValue()));
        });

        return copy;
    }
}
