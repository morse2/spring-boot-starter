package com.googlecode.spirit.boot.autoconfiguration.mybatis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by JunJie on 11/18/16.
 */
@ConfigurationProperties(prefix = "spirit.mybatis")
public class SpiritMybatisProperties {

    private int sqlSessionTemplateDecisionOrder = 2;
    private int sqlSessionTemplateExecutorOrder = 10;
    private int pagingInterceptOrder = 4;
    private boolean usePagingInterceptor = true;
    private boolean usePageDelegate;

    public int getSqlSessionTemplateDecisionOrder() {
        return sqlSessionTemplateDecisionOrder;
    }

    public void setSqlSessionTemplateDecisionOrder(int sqlSessionTemplateDecisionOrder) {
        this.sqlSessionTemplateDecisionOrder = sqlSessionTemplateDecisionOrder;
    }

    public int getSqlSessionTemplateExecutorOrder() {
        return sqlSessionTemplateExecutorOrder;
    }

    public void setSqlSessionTemplateExecutorOrder(int sqlSessionTemplateExecutorOrder) {
        this.sqlSessionTemplateExecutorOrder = sqlSessionTemplateExecutorOrder;
    }

    public int getPagingInterceptOrder() {
        return pagingInterceptOrder;
    }

    public void setPagingInterceptOrder(int pagingInterceptOrder) {
        this.pagingInterceptOrder = pagingInterceptOrder;
    }

    public boolean isUsePagingInterceptor() {
        return usePagingInterceptor;
    }

    public void setUsePagingInterceptor(boolean usePagingInterceptor) {
        this.usePagingInterceptor = usePagingInterceptor;
    }

    public boolean isUsePageDelegate() {
        return usePageDelegate;
    }

    public void setUsePageDelegate(boolean usePageDelegate) {
        this.usePageDelegate = usePageDelegate;
    }
}
