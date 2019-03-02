package com.tumi.data.poi.utils;

import com.googlecode.easyec.sika.WorkData;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * @author jefferychan
 */
public class CategoryUtils {

    public static void fill(List<WorkData> list, Set<String> baseCategories, String col, boolean isCheck) {
        WorkData data = WorkDataUtils.getData2String(list, col);
        if (data != null) {
            List<String> categories = WorkDataUtils.getData2ListString(data);
            if (isNotEmpty(categories)) {
                for (int i = 0; i < categories.size(); i++) {
                    String val = categories.get(i);
                    boolean b = baseCategories.stream()
                        .anyMatch(cate -> StringUtils.equalsIgnoreCase(val, cate));
                    if (!b) {
                        if (!isCheck) {
                            categories.remove(i--);
                        } else {
                            WorkDataUtils.appendErrorLabel(list, "column 【" + col + "】 can't check: [" + val + "]");
                        }
                    }
                }
                data.setValue(StringUtils.join(categories, ","));
            } else {
                String lable = "not found any categories";
                WorkDataUtils.appendErrorLabel(list, lable);
            }
        }
    }


}
