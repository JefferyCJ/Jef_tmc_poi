package com.tumi.data.poi.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/16 2:21 PM
 * @describe:
 */
public class CheckUtils {


    //区分HK站点和CN站点文件
    public static boolean isCNProductData(String fileName) {
        if (StringUtils.containsIgnoreCase(fileName, "cn"))
            return true;
        return false;
    }


    //判断多语言字段是否为空和是否是对应语言
    public static boolean isContainChinese(String str) {
        String regex = "[\u4e00-\u9fa5]";  //汉字的Unicode取值范围
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(str);
        return match.find();
    }


    //判断性别字段
    public static boolean isGender(List<String> genders) {
        if (CollectionUtils.isEmpty(genders)) return false;
        boolean result = false;
        for (String gender : genders) {
            result = StringUtils.containsIgnoreCase(gender, "Male") || StringUtils.containsIgnoreCase(gender, "Female");
        }
        return result;
    }

    public static boolean isBoolean(String value) {
        return StringUtils.containsIgnoreCase(value, "N") || StringUtils.containsIgnoreCase(value, "Y");
    }

    //判断SKU字段
    public static boolean isSKUCode(String code) {
        return code.startsWith("0");
    }


}
