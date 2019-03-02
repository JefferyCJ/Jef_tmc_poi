package com.tumi.data.poi.service.html.impl;

import com.googlecode.easyec.sika.WorkbookReader;
import com.googlecode.easyec.sika.ss.ExcelFactory;
import com.tumi.data.poi.config.HtmlProperties;
import com.tumi.data.poi.handler.ImageUrlHandler;
import com.tumi.data.poi.service.html.HtmlService;
import com.tumi.data.poi.service.product.impl.TumiProductServiceImpl;
import com.tumi.data.poi.service.stream.FileOpService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("htmlService")
public class HtmlServiceImpl implements HtmlService {
    private static final Logger logger = LoggerFactory.getLogger(TumiProductServiceImpl.class);
    @Resource
    private HtmlProperties htmlProperties;
    @Resource
    private FileOpService fileOpService;


    @Override
    public Set<String> getImageUrl(File file) throws Exception {
        InputStream in = new FileInputStream(file);
        ImageUrlHandler handler = new ImageUrlHandler();
        WorkbookReader reader = new WorkbookReader();
        reader.add(handler);
        ExcelFactory.getInstance().read(in, reader);
        return handler.getImageUrls();
    }

    @Override
    public Set<String> scanImageUrlFile(String filePath) {
        Set<String> imageUrls = new HashSet<>();
        logger.info("begin get html image url...");
        List<File> dirs = fileOpService.scanFiles(filePath);
        for (File dir : dirs) {
            if (dir.isFile()) {
                try {
                    imageUrls.addAll(this.getImageUrl(dir));
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
        logger.info("A total of image url num: [" + CollectionUtils.size(imageUrls) + "]");
        return imageUrls;
    }

    @Override
    public void scanHtmlFile(String filePath, Set<String> imageUrls) {
        List<File> dirs = fileOpService.scanFiles(filePath);
        for (File dir : dirs) {
            if (dir.isFile()) {

                String szConetnt = openFile(dir);
                try {
                    Parser parser = new Parser(szConetnt);
                    parser.setEncoding(ENCODE);

                    NodeFilter filter = new TagNameFilter("img");
                    NodeList nodes = parser.extractAllNodesThatMatch(filter);
                    for (int i = 0; i < nodes.size(); i++) {
                        Node node = nodes.elementAt(i);
                        if (node instanceof ImageTag) {
                            ImageTag imageTag = (ImageTag) node;
                            String htImageURL = imageTag.getImageURL();
                            String url = buildImageUrl(imageUrls, htImageURL);
                            if (StringUtils.isNotBlank(url)) {
                                szConetnt = StringUtils.replace(szConetnt, htImageURL, url);
                            }
                        }
                    }
                    parser.reset();
                    NodeFilter videoFilter = new TagNameFilter("video");
                    NodeList videoNodes = parser.extractAllNodesThatMatch(videoFilter);
                    for (int i = 0; i < videoNodes.size(); i++) {
                        Node node = videoNodes.elementAt(i);
                        String text = node.getText();
                        int end = text.lastIndexOf(".") + 4;
                        int begin = text.lastIndexOf("poster=") + 8;
                        String htVideoUrl = text.substring(begin, end);
                        String url = buildImageUrl(imageUrls, htVideoUrl);
                        szConetnt = replaceContent(szConetnt, htVideoUrl, url);
                    }
                    parser.reset();
                    NodeFilter sourceFilter = new TagNameFilter("source");
                    NodeList sourceNodes = parser.extractAllNodesThatMatch(sourceFilter);
                    for (int i = 0; i < sourceNodes.size(); i++) {
                        Node sourceNode = sourceNodes.elementAt(i);
                        String sourceNodeText = sourceNode.getText();
                        int begin = sourceNodeText.lastIndexOf("src=") + 5;
                        int end = sourceNodeText.lastIndexOf(".") + 4;
                        if (sourceNodeText.contains(".webm")) {
                            end++;
                        }
                        String htSourceUrl = sourceNodeText.substring(begin, end);
                        String url = buildImageUrl(imageUrls, htSourceUrl);
                        szConetnt = replaceContent(szConetnt, htSourceUrl, url);
                    }
                    parser.reset();
                    fileOpService.htmlFileDownload(szConetnt, htmlProperties.getSiteResultFile(dir.getName()));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    fileOpService.moveFileToHistory(filePath, dir);
                }
            }
        }

    }

    private String replaceContent(String content, String search, String replacement) {
        if (StringUtils.isNotBlank(replacement)) {
            content = StringUtils.replace(content, search, replacement);
        }
        return content;
    }

    private String buildImageUrl(Set<String> imageUrls, String htImageURL) {
        if (StringUtils.isBlank(htImageURL)) {
            return "";
        }
        int begin = htImageURL.lastIndexOf("/") + 1;
        int end = htImageURL.lastIndexOf(".");
        if (htImageURL.contains("webm")) {
            end += 5;
        }else if (htImageURL.contains("mp4")){
            end+=4;
        }
        String searchImageUrl = htImageURL.substring(begin, end);
        String result = null;
        for (String imageUrl : imageUrls) {
            if (StringUtils.containsIgnoreCase(imageUrl, searchImageUrl.trim())) {
                result = htmlProperties.getSiteName() + imageUrl;
                break;
            }
        }
        if (null == result) {
            logger.warn("not found imageUrl. html local image url:[" + htImageURL + "]");
        }
        return result;
    }

    private String openFile(File file) {
        try {
            BufferedReader bis = new BufferedReader(new InputStreamReader(new FileInputStream(file), ENCODE));
            String szContent = "";
            String szTemp;
            while ((szTemp = bis.readLine()) != null) {
                szContent += szTemp + "\n";
            }
            bis.close();
            return szContent;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        }
    }

}
