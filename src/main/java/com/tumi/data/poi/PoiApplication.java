package com.tumi.data.poi;

import com.tumi.data.poi.config.PoiProperties;
import com.tumi.data.poi.domain.ProductWorkDataFile;
import com.tumi.data.poi.service.product.TumiProductService;
import com.tumi.data.poi.service.stream.FileOpService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(PoiProperties.class)
@EnableAsync
public class PoiApplication implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(PoiApplication.class);
    @Resource
    private TumiProductService tumiProductService;
    @Resource
    private FileOpService fileOpService;

    public static void main(String[] args) {
        SpringApplication.run(PoiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();
        List<ProductWorkDataFile> workDataFileList = tumiProductService.refactoring();
        for (ProductWorkDataFile dataFile : workDataFileList) {
            LOG.info("check result file [" + dataFile.getFileName() + "] data num is [" + CollectionUtils.size(dataFile.getWorkData()) + "]");
            fileOpService.fileDownload(dataFile.getWorkData(), dataFile.getFileName());
        }
        LOG.info("Elapsed time: [" + (System.currentTimeMillis() - start) + "]");
    }

}
