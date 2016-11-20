package com.googlecode.spirit.boot.autoconfiguration.dao;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by JunJie on 10/29/16.
 */
@ConfigurationProperties(prefix = "spirit.dao")
public class SpiritDaoProperties {

    private int pageSize = 10;
    private int maxLoValue = 10;
    private int idGenerateInterceptorOrder = 5;

    private boolean usePageDelegate = false;
    private boolean useSequenceGenerator = true;
    private boolean useDefaultIdGenerators = true;

    private String pageDialect;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getMaxLoValue() {
        return maxLoValue;
    }

    public void setMaxLoValue(int maxLoValue) {
        this.maxLoValue = maxLoValue;
    }

    public int getIdGenerateInterceptorOrder() {
        return idGenerateInterceptorOrder;
    }

    public void setIdGenerateInterceptorOrder(int idGenerateInterceptorOrder) {
        this.idGenerateInterceptorOrder = idGenerateInterceptorOrder;
    }

    public boolean isUsePageDelegate() {
        return usePageDelegate;
    }

    public void setUsePageDelegate(boolean usePageDelegate) {
        this.usePageDelegate = usePageDelegate;
    }

    public boolean isUseSequenceGenerator() {
        return useSequenceGenerator;
    }

    public void setUseSequenceGenerator(boolean useSequenceGenerator) {
        this.useSequenceGenerator = useSequenceGenerator;
    }

    public boolean isUseDefaultIdGenerators() {
        return useDefaultIdGenerators;
    }

    public void setUseDefaultIdGenerators(boolean useDefaultIdGenerators) {
        this.useDefaultIdGenerators = useDefaultIdGenerators;
    }

    public String getPageDialect() {
        return pageDialect;
    }

    public void setPageDialect(String pageDialect) {
        this.pageDialect = pageDialect;
    }
}
