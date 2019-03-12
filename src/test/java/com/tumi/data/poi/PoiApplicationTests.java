package com.tumi.data.poi;

import com.googlecode.easyec.sika.WorkbookReader;
import com.googlecode.easyec.sika.WorkbookWriter;
import com.googlecode.easyec.sika.ss.ExcelFactory;
import com.tumi.data.poi.callback.DefaultWorkbookCallback;
import com.tumi.data.poi.handler.DefaultWorkbookHandler;
import com.tumi.data.poi.handler.PrdCategoryHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PoiApplicationTests {


    @Test
    public void parseExcel() throws Exception {
        InputStream in1 = load("/Users/jefferychan/Desktop/removed_categories.xlsx");
        InputStream in2 = load("/Users/jefferychan/Desktop/20181129.xlsx");

        PrdCategoryHandler handler1 = new PrdCategoryHandler("HK");
        WorkbookReader r1 = new WorkbookReader();
        r1.add(handler1);

        DefaultWorkbookHandler handler2 = new DefaultWorkbookHandler();
        WorkbookReader r2 = new WorkbookReader();
        r2.add(handler2);

        ExcelFactory.getInstance().read(in1, r1);
        ExcelFactory.getInstance().read(in2, r2);

//        CategoryUtils.fill(
//                handler2.getRecords(),
//                handler1.getCategories(),
//                "T",
//                true
//        );

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


    @Test
    public void testZip() throws Exception {
        File file = new File("/Users/jefferychan/Desktop/poi");
        List<File> zipFiles = null;
        if (file.isDirectory()) {
            zipFiles = Arrays.asList(file.listFiles());
        }
        List<String> logs = new ArrayList<>();
        for (File zipFile : zipFiles) {
            if (zipFile.getName().endsWith(".zip")) {
                zipFileRead(zipFile.getPath(), logs);
            }
        }
    }


    /**
     * @Description: (读取Zip信息 ， 获得zip中所有的目录文件信息)
     */
    private void zipFileRead(String file, List<String> logs) throws Exception {
        // 获得zip信息
        ZipFile zipFile = new ZipFile(file);
        @SuppressWarnings("unchecked")
        Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile
            .entries();
        while (enu.hasMoreElements()) {
            ZipEntry zipElement = (ZipEntry) enu.nextElement();
            if (!zipElement.isDirectory() && !StringUtils.startsWithIgnoreCase(zipElement.getName(), "__MACOSX")
                && !StringUtils.startsWithIgnoreCase(zipElement.getName(), ".")
                && !StringUtils.startsWithIgnoreCase(zipElement.getName(), "/")) {

                InputStream read = zipFile.getInputStream(zipElement);
                String fileName = zipElement.getName();
                logs.add("process file:[" + zipElement.getName() + "]");
                if (fileName != null && fileName.indexOf(".") != -1) {
                    unZipFile(zipElement, read, logs);
                }
            }
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Description: (找到文件并读取解压到指定目录)
     */
    private void unZipFile(ZipEntry ze, InputStream read, List<String> logs) throws Exception {
        // 如果只读取图片，自行判断就OK.
        String fileName = ze.getName();
        logs.add("process file:[" + ze.getName() + "]");

        // 判断文件是否符合要求或者是指定的某一类型
        // 指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）
        File file = new File(fileName);
//        if (!file.exists()) {
//            File rootDirectoryFile = new File(file.getParent());
//            // 创建目录
//            if (!rootDirectoryFile.exists()) {
//                boolean ifSuccess = rootDirectoryFile.mkdirs();
//                if (ifSuccess) {
//                    System.out.println("文件夹创建成功!");
//                } else {
//                    System.out.println("文件创建失败!");
//                }
//            }
//            // 创建文件
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        // 写入文件
//        BufferedOutputStream write = new BufferedOutputStream(
//            new FileOutputStream(file));
//        int cha = 0;
//        while ((cha = read.read()) != -1) {
//            write.write(cha);
//        }
//        // 要注意IO流关闭的先后顺序
//        write.flush();
//        write.close();
//        read.close();

    }
}
