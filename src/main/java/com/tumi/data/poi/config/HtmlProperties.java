package com.tumi.data.poi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "html")
public class HtmlProperties {
    private String siteUrlFile;
    private String siteHtmlFile;
    private String siteResultFile;
    private String siteName;
    private static String suffix = ".html";

    public String getSiteUrlFile() {
        return siteUrlFile;
    }

    public void setSiteUrlFile(String siteUrlFile) {
        this.siteUrlFile = siteUrlFile;
    }

    public String getSiteHtmlFile() {
        return siteHtmlFile;
    }

    public void setSiteHtmlFile(String siteHtmlFile) {
        this.siteHtmlFile = siteHtmlFile;
    }

    public String getSiteResultFile(String fileName) {
        int end = fileName.lastIndexOf(".");
        fileName = fileName.substring(0, end);
        return siteResultFile + fileName + "-" + System.currentTimeMillis() + suffix;
    }

    public void setSiteResultFile(String siteResultFile) {
        this.siteResultFile = siteResultFile;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
}
