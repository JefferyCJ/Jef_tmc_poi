package com.tumi.data.poi;

import com.tumi.data.poi.config.HtmlProperties;
import com.tumi.data.poi.config.PoiProperties;
import com.tumi.data.poi.config.XmlProperties;
import com.tumi.data.poi.service.html.HtmlService;
import com.tumi.data.poi.service.product.TumiProductService;
import com.tumi.data.poi.service.stream.FileOpService;
import com.tumi.data.poi.service.xml.XmlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author jefferychan
 */
@SpringBootApplication
@EnableConfigurationProperties(PoiProperties.class)
@EnableAsync
public class PoiApplication implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(PoiApplication.class);
    @Resource
    private TumiProductService tumiProductService;

    @Resource
    private HtmlService htmlService;

    @Resource
    private XmlService xmlService;

    @Resource
    private PoiProperties poiProperties;

    @Resource
    private HtmlProperties htmlProperties;

    @Resource
    private XmlProperties xmlProperties;

    @Resource
    private FileOpService fileOpService;

    public static void main(String[] args) {
        SpringApplication.run(PoiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        performInventoryXml();
//        performProduct();
//        performHtml();
    }

    private void performProduct() throws Exception {
        long start = System.currentTimeMillis();
        tumiProductService.scanProductFile(poiProperties.getProductFile());
        logger.info("scan produt files, elapsed time: 【" + (System.currentTimeMillis() - start) + "】");
    }

    private void performHtml() {
        long start = System.currentTimeMillis();
        Set<String> imageUrls = htmlService.scanImageUrlFile(htmlProperties.getSiteUrlFile());
        htmlService.scanHtmlFile(htmlProperties.getSiteHtmlFile(), imageUrls);
        logger.info("scan html files, elapsed time: 【" + (System.currentTimeMillis() - start) + "】");
    }

    private void performInventoryXml() throws Exception {
        long start = System.currentTimeMillis();
        xmlService.scanInventoryFile(xmlProperties.getInventorySftpFile());
        logger.info("scan xml files, elapsed time: 【" + (System.currentTimeMillis() - start) + "】");
    }

    private void performCheckProductType() throws Exception {
        long start = System.currentTimeMillis();
        xmlService.scanInventoryFile(xmlProperties.getInventorySftpFile());
        logger.info("scan xml files, elapsed time: 【" + (System.currentTimeMillis() - start) + "】");
    }


}
