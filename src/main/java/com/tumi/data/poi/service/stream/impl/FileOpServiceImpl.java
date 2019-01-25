package com.tumi.data.poi.service.stream.impl;

import com.googlecode.easyec.sika.WorkData;
import com.googlecode.easyec.sika.WorkbookWriter;
import com.googlecode.easyec.sika.ss.ExcelFactory;
import com.tumi.data.poi.callback.DefaultWorkbookCallback;
import com.tumi.data.poi.service.stream.FileOpService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 11:29 AM
 * @describe:
 */
@Service("fileOpService")
public class FileOpServiceImpl implements FileOpService {
    @Override
    public void fileDownload(List<List<WorkData>> records, String filePath) throws Exception {
        WorkbookWriter w1 = new WorkbookWriter();
        w1.add(new DefaultWorkbookCallback(records));

        InputStream in3 = loadFromClasspath("template/output.xlsx");
        byte[] bs = ExcelFactory.getInstance().write(in3, w1);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(bs);
            fos.flush();
        } finally {
            IOUtils.closeQuietly(fos);
        }
    }

    private InputStream loadFromClasspath(String file) throws IOException {
        return new ClassPathResource(file).getInputStream();
    }
}
