package com.tumi.data.poi.utils;

import com.googlecode.easyec.sika.WorkData;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

public class CategoryUtils {

    private CategoryUtils() {
    }

    public static void fill(List<List<WorkData>> lists, Set<String> removedCategories, String col) {
        lists.forEach(list -> {
            WorkData data = WorkDataUtils.getData2String(list, col);
            if (data != null) {
                List<String> categories = WorkDataUtils.getData2ListString(data);
                if (isNotEmpty(categories)) {
                    for (int i = 0; i < categories.size(); i++) {
                        String val = categories.get(i);
                        boolean b = removedCategories.stream()
                                .anyMatch(cate -> StringUtils.equalsIgnoreCase(val, cate));
                        if (!b) categories.remove(i--);
                    }

                    data.setValue(StringUtils.join(categories, ","));
                }
            }
        });
    }


}
