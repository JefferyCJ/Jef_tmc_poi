package com.tumi.data.poi.service.scene7.impl;

import com.googlecode.easyec.sika.WorkData;
import com.tumi.data.poi.config.PoiProperties;
import com.tumi.data.poi.config.Scene7Properties;
import com.tumi.data.poi.domain.ProductWorkDataFile;
import com.tumi.data.poi.domain.impl.ProductWorkDataFileImpl;
import com.tumi.data.poi.service.scene7.Scene7ImageExtractorService;
import com.tumi.data.poi.utils.WorkDataUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: JinPeng
 * @version: 2019/1/14 1:58 PM
 * @describe:
 */
@Service("scene7ImageExtractorService")
public class Scene7ImageExtractorServiceImpl implements Scene7ImageExtractorService {
    private static final Logger LOG = LoggerFactory.getLogger(Scene7ImageExtractorServiceImpl.class);

    @Resource
    private Scene7Properties scene7Properties;
    @Resource
    private PoiProperties poiProperties;
    @Resource
    private RestTemplate restTemplate;

    private List<ProductWorkDataFile> workDataFileList = new ArrayList<>();

    @Override
    public List<ProductWorkDataFile> syncImages(ProductWorkDataFile dataFile) {
        LOG.info("product num is:[" + CollectionUtils.size(dataFile.getWorkData()) + "]");
        ProductWorkDataFile productBaseWorkDataFile = new ProductWorkDataFileImpl();
        productBaseWorkDataFile.setFileName(poiProperties.getResultBaseFile());
        ProductWorkDataFile productNoPicWorkDataFile = new ProductWorkDataFileImpl();
        productNoPicWorkDataFile.setFileName(poiProperties.getResultNoPictureFile());
        LOG.info("begin check product's scene7 images...");
        dataFile.getWorkData().forEach(list -> {
            WorkData data = WorkDataUtils.getData2String(list, poiProperties.getStyleCodeColumn());
            String styleVariantCode = WorkDataUtils.getData2String(data);
            if (StringUtils.contains(styleVariantCode, "_SV")) {
                styleVariantCode = StringUtils.substringBefore(styleVariantCode, "_SV");
            }
            String imageSetName = null;
            if (StringUtils.isNotBlank(scene7Properties.getImagesetSuffix())) {
                imageSetName = styleVariantCode + scene7Properties.getImagesetSuffix();
            } else {
                imageSetName = styleVariantCode;
            }
            final String scene7Url = scene7Properties.getScene7Url() + "/" + imageSetName;
            LOG.info("scene7 url:[" + scene7Url + "]");
            final String imageSetUrl = scene7Url + scene7Properties.getImagesetParam();
            final String imageSet = getImageSet(imageSetUrl);
            if (imageSet != null && !StringUtils.isBlank(imageSet)) {
                productBaseWorkDataFile.addData(list);
            } else {
                LOG.warn("No Image Set found for " + styleVariantCode);
                productNoPicWorkDataFile.addData(list);
            }

        });
        LOG.info("check  product's scene7 images end...");

        if (CollectionUtils.isNotEmpty(productBaseWorkDataFile.getWorkData())) {
            workDataFileList.add(productBaseWorkDataFile);
        }
        LOG.info("find no picture product num is" + CollectionUtils.size(productNoPicWorkDataFile.getWorkData()));
        if (CollectionUtils.isNotEmpty(productNoPicWorkDataFile.getWorkData())) {
            workDataFileList.add(productNoPicWorkDataFile);
        }
        return workDataFileList;
    }


    public String getImageSet(final String scene7url) {
        String imageSet = null;
        if (existsImage(scene7url)) {
            imageSet = restTemplate.getForObject(scene7url, String.class);
        }
        return imageSet;
    }

    private boolean existsImage(final String scene7url) {
        try {
            final ResponseEntity<String> responseEntity = restTemplate.exchange(scene7url, HttpMethod.HEAD, new HttpEntity<String>(StringUtils.EMPTY), String.class);
            return responseEntity.getStatusCode() == HttpStatus.OK;
        } catch (final RestClientException rce) {
            LOG.error(rce.getMessage(), rce);
        }
        return false;

    }
}
