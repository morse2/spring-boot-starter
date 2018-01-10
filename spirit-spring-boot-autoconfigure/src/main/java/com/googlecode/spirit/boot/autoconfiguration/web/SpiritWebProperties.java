package com.googlecode.spirit.boot.autoconfiguration.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 */
@ConfigurationProperties(prefix = "spirit.web")
public class SpiritWebProperties {

    private boolean useHttpRequest;
    private boolean useStringStream;
    private boolean useJsonStream;
    private boolean useJaxbStream;
    private int defaultMaxPerRoute;
    private int maxTotal;
    private String jsonDateFormat;
    private String jaxbPackageToScan;

    public boolean isUseHttpRequest() {
        return useHttpRequest;
    }

    public void setUseHttpRequest(boolean useHttpRequest) {
        this.useHttpRequest = useHttpRequest;
    }

    public boolean isUseStringStream() {
        return useStringStream;
    }

    public void setUseStringStream(boolean useStringStream) {
        this.useStringStream = useStringStream;
    }

    public boolean isUseJsonStream() {
        return useJsonStream;
    }

    public void setUseJsonStream(boolean useJsonStream) {
        this.useJsonStream = useJsonStream;
    }

    public boolean isUseJaxbStream() {
        return useJaxbStream;
    }

    public void setUseJaxbStream(boolean useJaxbStream) {
        this.useJaxbStream = useJaxbStream;
    }

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
