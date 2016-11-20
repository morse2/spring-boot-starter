package com.googlecode.spirit.boot.autoconfiguration.mybatis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by JunJie on 11/18/16.
 */
@ConfigurationProperties(prefix = "spirit.mybatis")
public class SpiritMybatisProperties {

    private int sqlSessionTemplateDecisionOrder = 2;
    private int sqlSessionTemplateExecutorOrder = 10;
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

    public boolean isUsePageDelegate() {
        return usePageDelegate;
    }

    public void setUsePageDelegate(boolean usePageDelegate) {
        this.usePageDelegate = usePageDelegate;
    }
}
