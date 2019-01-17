package com.tumi.data.poi;

import com.googlecode.easyec.sika.WorkbookReader;
import com.googlecode.easyec.sika.WorkbookWriter;
import com.googlecode.easyec.sika.ss.ExcelFactory;
import com.tumi.data.poi.callback.DefaultWorkbookCallback;
import com.tumi.data.poi.handler.DefaultWorkbookHandler;
import com.tumi.data.poi.handler.PrdCategoryHandler;
import com.tumi.data.poi.utils.CategoryUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PoiApplicationTests {




    @Test
    public void parseExcel() throws Exception {
        InputStream in1 = load("/Users/jefferychan/Desktop/removed_categories.xlsx");
        InputStream in2 = load("/Users/jefferychan/Desktop/20181129.xlsx");

        PrdCategoryHandler handler1 = new PrdCategoryHandler();
        WorkbookReader r1 = new WorkbookReader();
        r1.add(handler1);

        DefaultWorkbookHandler handler2 = new DefaultWorkbookHandler();
        WorkbookReader r2 = new WorkbookReader();
        r2.add(handler2);

        ExcelFactory.getInstance().read(in1, r1);
        ExcelFactory.getInstance().read(in2, r2);

        CategoryUtils.fill(
            handler2.getRecords(),
            handler1.getCategories(),
            "T"
        );

        WorkbookWriter w1 = new WorkbookWriter();
        w1.add(new DefaultWorkbookCallback(handler2.getRecords()));

        InputStream in3 = load("template/output.xlsx");
        byte[] bs = ExcelFactory.getInstance().write(in3, w1);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream("/Users/jefferychan/Desktop/a.xlsx");
            fos.write(bs);
            fos.flush();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private InputStream load(String file) throws IOException {
        return new ClassPathResource(file).getInputStream();
    }
}
