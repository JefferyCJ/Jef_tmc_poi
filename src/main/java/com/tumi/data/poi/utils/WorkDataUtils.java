package com.tumi.data.poi.utils;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.converters.Date2StringConverter;
import com.googlecode.easyec.sika.converters.Object2StringConverter;
import com.googlecode.easyec.sika.mappings.ColumnEvaluatorFactory;
import com.googlecode.easyec.sika.mappings.UnknownColumnException;
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
        String value = data.getValue(new Object2StringConverter());
        if (StringUtils.isBlank(value)) return null;
        return value;
    }

    public static WorkData getData2String(List<WorkData> list, String col) {
        try {
            return ColumnEvaluatorFactory.evaluateWorkData(list, col);
        } catch (UnknownColumnException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static List<String> getData2ListString(WorkData data) {
        String value = data.getValue(new Object2StringConverter());
        if (StringUtils.isBlank(value)) return emptyList();

        List<String> result = new ArrayList<>();
        String[] parts = value.split("\\s*,\\s*");
        for (String part : parts) {
            result.add(StringUtils.trim(part));
        }

        return result;
    }


}
