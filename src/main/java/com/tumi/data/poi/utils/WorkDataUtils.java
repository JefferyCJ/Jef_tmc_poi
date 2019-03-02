package com.tumi.data.poi.utils;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.converters.Date2StringConverter;
import com.googlecode.easyec.sika.converters.Object2StringConverter;
import com.googlecode.easyec.sika.data.DefaultWorkData;
import com.googlecode.easyec.sika.mappings.ColumnEvaluatorFactory;
import com.googlecode.easyec.sika.mappings.UnknownColumnException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/16 2:21 PM
 * @describe:
 */
public class WorkDataUtils {


    public static String getDate2String(WorkData data) {
        String result = null;
        if (null != data) {
            WorkData.WorkDataType workDataType = data.getWorkDataType();
            if (null != workDataType && workDataType.equals(WorkData.WorkDataType.NUMBER)) {
                result = DateUtils.number2DateString((Double) data.getValue(), "MM-dd-yyyy");
            } else if (null != workDataType && workDataType.equals(WorkData.WorkDataType.DATE)) {
                result = data.getValue(new Date2StringConverter("MM-dd-yyyy"));
            } else if (null != workDataType && workDataType.equals(WorkData.WorkDataType.STRING)) {
                result = data.getValue(new Object2StringConverter());
            }
        }
        return result;
    }


    public static String getData2String(WorkData data) {
        if (null == data) return null;
        String value = data.getValue(new Object2StringConverter());
        if (StringUtils.isBlank(value)) return null;
        return value;
    }

    public static WorkData getData2String(List<WorkData> list, String col) {
        try {
            createData(list);
            return ColumnEvaluatorFactory.evaluateWorkData(list, col);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDataCovertString(List<WorkData> list, String col) {
        try {
            WorkData workData = ColumnEvaluatorFactory.evaluateWorkData(list, col);
            return getData2String(workData);
        } catch (UnknownColumnException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static List<String> getData2ListString(WorkData data) {
        String value = data.getValue(new Object2StringConverter());
        if (StringUtils.isBlank(value)) return emptyList();

        return getString2ListString(value);
    }

    public static List<String> getString2ListString(String value) {
        String regex = "\\s*,\\s*";
        return getString2ListString(value, regex);
    }

    public static List<String> getString2_ListString(String value) {
        String regex = "\\s*_\\s*";
        return getString2ListString(value, regex);
    }

    private static List<String> getString2ListString(String value, String regex) {
        List<String> result = new ArrayList<>();
        String[] parts = value.split(regex);
        for (String part : parts) {
            result.add(StringUtils.trim(part));
        }

        return result;
    }

    public static void appendErrorLabel(List<WorkData> workData, String label) {
        WorkData data = WorkDataUtils.getData2String(workData, "BA");
        StringBuffer result = new StringBuffer();
        if (data != null) {
            if (StringUtils.isNotBlank(String.valueOf(data.getValue()))) {
                result.append(data.getValue()).append(",");
            }
            result.append("【").append(label).append("】");
            data.setValue(result.toString());
        } else {
            createData(workData);
            appendErrorLabel(workData, label);
        }

    }

    private static void createData(List<WorkData> workData) {
        int size = CollectionUtils.size(workData);
        while (size++ < 54) {
            WorkData defaultWorkData = new DefaultWorkData();
            defaultWorkData.setValue("");
            workData.add(defaultWorkData);
        }
    }

    /**
     * delete blank space
     * Replace the comma
     * Replace the "0"
     *
     * @param workData
     */
    public static void replaceLineData(WorkData workData) {
        String data = WorkDataUtils.getData2String(workData);
        if (StringUtils.isNotBlank(data)) {
            data = data.trim().replaceAll("，", ",");
        }
        if (StringUtils.equalsIgnoreCase(data, "0")) {
            workData.setValue("");
        }
        workData.setValue(data);
    }


}
