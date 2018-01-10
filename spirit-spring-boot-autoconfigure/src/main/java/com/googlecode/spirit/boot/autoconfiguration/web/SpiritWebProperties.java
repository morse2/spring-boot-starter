package com.googlecode.spirit.boot.autoconfiguration.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 */
@ConfigurationProperties(prefix = "spirit.web")
public class SpiritWebProperties {

    private int defaultMaxPerRoute;
    private int maxTotal;
    private String jsonDateFormat;
    private String jaxbPackageToScan;

    public int getDefaultMaxPerRoute() {
        return defaultMaxPerRoute;
    }

    public void setDefaultMaxPerRoute(int defaultMaxPerRoute) {
        this.defaultMaxPerRoute = defaultMaxPerRoute;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public String getJsonDateFormat() {
        return jsonDateFormat;
    }

    public void setJsonDateFormat(String jsonDateFormat) {
        this.jsonDateFormat = jsonDateFormat;
    }

    public String getJaxbPackageToScan() {
        return jaxbPackageToScan;
    }

    public void setJaxbPackageToScan(String jaxbPackageToScan) {
        this.jaxbPackageToScan = jaxbPackageToScan;
    }
}
