package com.tumi.data.poi;

import com.tumi.data.poi.utils.CheckUtils;
import org.junit.Assert;
import org.junit.Test;


public class ProductImpexTests {




    @Test
    public void covertImpex() throws Exception {
//        CvHKProductImportCovert cvHKProductImportCovert=new CvHKProductImportCovert();
//        cvHKProductImportCovert.getFile();


    }


    @Test
    public void calculationBytes() {
        String data="Just In Case® Travel Backpack";
        int a= data.getBytes().length;
        Assert.assertTrue(data.length() == a);
    }


    @Test
    public void isChinese() {
        String data="妝國際";
        boolean s=CheckUtils.isContainChinese(data);
        Assert.assertTrue(s);
    }

}
