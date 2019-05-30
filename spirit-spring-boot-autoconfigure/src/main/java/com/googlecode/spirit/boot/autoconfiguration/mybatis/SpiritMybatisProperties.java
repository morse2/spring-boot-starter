package com.googlecode.spirit.boot.autoconfiguration.mybatis;

import org.apache.ibatis.session.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Properties;

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

    /* mybatis properties */
    /**
     * Location of MyBatis xml config file.
     */
    private String configLocation;
    /**
     * A Configuration object for customize default settings. If {@link #configLocation}
     * is specified, this property is not used.
     */
    @NestedConfigurationProperty
    private Configuration configuration;

    /**
     * Externalized properties for MyBatis configuration.
     */
    private Properties configurationProperties;
    /**
     * Locations of MyBatis mapper files.
     */
    private String[] mapperLocations;

    /**
     * Packages to search type aliases. (Package delimiters are ",; \t\n")
     */
    private String typeAliasesPackage;

    /**
     * Packages to search for type handlers. (Package delimiters are ",; \t\n")
     */
    private String typeHandlersPackage;
    private String[] configurationPropertiesLocations;

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

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public Properties getConfigurationProperties() {
        return configurationProperties;
    }

    public void setConfigurationProperties(Properties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getTypeAliasesPackage() {
        return typeAliasesPackage;
    }

    public void setTypeAliasesPackage(String typeAliasesPackage) {
        this.typeAliasesPackage = typeAliasesPackage;
    }

    public String getTypeHandlersPackage() {
        return typeHandlersPackage;
    }

    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    public String[] getConfigurationPropertiesLocations() {
        return configurationPropertiesLocations;
    }

    public void setConfigurationPropertiesLocations(String[] configurationPropertiesLocations) {
        this.configurationPropertiesLocations = configurationPropertiesLocations;
    }
}
