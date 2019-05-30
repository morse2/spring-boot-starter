package com.googlecode.spirit.boot.autoconfiguration.mybatis;

import com.googlecode.easyec.spirit.dao.dialect.PageDialect;
import com.googlecode.easyec.spirit.dao.paging.PageProxy;
import com.googlecode.easyec.spirit.dao.paging.PagingInterceptor;
import com.googlecode.easyec.spirit.dao.paging.factory.PageDelegate;
import com.googlecode.easyec.spirit.mybatis.executor.support.SqlSessionTemplateDecisionInterceptor;
import com.googlecode.easyec.spirit.mybatis.executor.support.SqlSessionTemplateExecutor;
import com.googlecode.easyec.spirit.mybatis.paging.MybatisPage;
import com.googlecode.easyec.spirit.mybatis.paging.MybatisPageWritable;
import com.googlecode.easyec.spirit.mybatis.paging.support.MybatisPageProxy;
import com.googlecode.easyec.spirit.mybatis.paging.support.MybatisPagingInterceptor;
import com.googlecode.easyec.spirit.mybatis.service.impl.DelegateServiceBeanPostProcessor;
import com.googlecode.easyec.spirit.web.controller.formbean.impl.AbstractSearchFormBean;
import com.googlecode.spirit.boot.autoconfiguration.dao.SpiritDaoProperties;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Created by JunJie on 11/18/16.
 */
@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties({ SpiritMybatisProperties.class, SpiritDaoProperties.class })
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class SpiritMybatisAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpiritMybatisAutoConfiguration.class);

    private SpiritMybatisProperties spiritMybatisProperties;
    private SpiritDaoProperties spiritDaoProperties;

    private ResourcePatternResolver resourcePatternResolver;
    private final Interceptor[] interceptors;
    private final DatabaseIdProvider databaseIdProvider;

    public SpiritMybatisAutoConfiguration(
        SpiritDaoProperties spiritDaoProperties,
        SpiritMybatisProperties spiritMybatisProperties,
        ResourcePatternResolver resourcePatternResolver,
        ObjectProvider<Interceptor[]> interceptorsProvider,
        ObjectProvider<DatabaseIdProvider> databaseIdProvider) {
        this.spiritDaoProperties = spiritDaoProperties;
        this.spiritMybatisProperties = spiritMybatisProperties;
        this.resourcePatternResolver = resourcePatternResolver;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);

        String configLocation = spiritMybatisProperties.getConfigLocation();
        if (StringUtils.hasText(configLocation)) {
            factory.setConfigLocation(resourcePatternResolver.getResource(configLocation));
        }

        org.apache.ibatis.session.Configuration configuration = spiritMybatisProperties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(configLocation)) {
            configuration = new org.apache.ibatis.session.Configuration();
        }

        factory.setConfiguration(configuration);

        final Properties properties = new Properties();
        Resource[] propertiesResources = getResources(spiritMybatisProperties.getConfigurationPropertiesLocations());
        if (!ObjectUtils.isEmpty(propertiesResources)) {
            Stream.of(propertiesResources).forEach(res -> {
                try {
                    properties.load(res.getInputStream());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    // ignore..
                }
            });
        }

        Properties configurationProperties = spiritMybatisProperties.getConfigurationProperties();
        if (configurationProperties != null) {
            properties.putAll(configurationProperties);
        }

        if (!properties.isEmpty()) {
            factory.setConfigurationProperties(properties);
        }

        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }

        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        String typeAliasesPackage = spiritMybatisProperties.getTypeAliasesPackage();
        if (StringUtils.hasLength(typeAliasesPackage)) {
            factory.setTypeAliasesPackage(typeAliasesPackage);
        }
        if (StringUtils.hasLength(spiritMybatisProperties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(spiritMybatisProperties.getTypeHandlersPackage());
        }

        Resource[] mapperLocations = getResources(spiritMybatisProperties.getMapperLocations());
        if (!ObjectUtils.isEmpty(mapperLocations)) {
            factory.setMapperLocations(mapperLocations);
        }

        return factory.getObject();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean
    @ConditionalOnBean(PageDialect.class)
    @ConditionalOnMissingBean(PageDelegate.class)
    @ConditionalOnProperty(prefix = "spirit.mybatis", name = "use-page-delegate", havingValue = "true")
    public PageDelegate<MybatisPage> pageConfigurer(PageDialect pageDialect) {
        return new InternalMybatisPageDelegate(spiritDaoProperties.getPageSize(), pageDialect, new MybatisPageProxy());
    }

    @Bean
    @ConditionalOnMissingBean(SqlSessionTemplateDecisionInterceptor.class)
    public SqlSessionTemplateDecisionInterceptor sqlSessionTemplateDecisionInterceptor(SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplateDecisionInterceptor interceptor = new SqlSessionTemplateDecisionInterceptor();
        interceptor.setOrder(spiritMybatisProperties.getSqlSessionTemplateDecisionOrder());
        interceptor.setSqlSessionFactory(sqlSessionFactory);

        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(SqlSessionTemplateExecutor.class)
    public SqlSessionTemplateExecutor sqlSessionTemplateExecutor() {
        SqlSessionTemplateExecutor exec = new SqlSessionTemplateExecutor();
        exec.setOrder(spiritMybatisProperties.getSqlSessionTemplateExecutorOrder());
        return exec;
    }

    @Bean
    @ConditionalOnMissingBean(DelegateServiceBeanPostProcessor.class)
    public DelegateServiceBeanPostProcessor delegateServiceBeanPostProcessor() {
        return new DelegateServiceBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean(PagingInterceptor.class)
    @ConditionalOnProperty(prefix = "spirit.mybatis", name = "use-paging-interceptor", havingValue = "true")
    public MybatisPagingInterceptor pagingInterceptor(SqlSessionFactory sqlSessionFactory) {
        MybatisPagingInterceptor interceptor = new MybatisPagingInterceptor();
        interceptor.setOrder(spiritMybatisProperties.getPagingInterceptOrder());
        interceptor.setSqlSessionFactory(sqlSessionFactory);

        return interceptor;
    }

    private Resource[] getResources(String[] locations) {
        if (ObjectUtils.isEmpty(locations)) return new Resource[0];

        List<Resource> result = new ArrayList<>();
        for (String location : locations) {
            try {
                Resource[] resources = resourcePatternResolver.getResources(location);
                result.addAll(Arrays.asList(resources));
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                // ignore..
            }
        }

        return result.toArray(new Resource[0]);
    }

    private class InternalMybatisPageDelegate extends PageDelegate<MybatisPage> {

        InternalMybatisPageDelegate(Integer pageSize, PageDialect dialect, PageProxy<MybatisPage> proxy) {
            super(pageSize, dialect, proxy);
        }

        @Override
        public MybatisPage createPage(AbstractSearchFormBean bean) {
            return createPage(bean, getPageSize());
        }

        @Override
        public MybatisPage createPage(AbstractSearchFormBean bean, int pageSize) {
            MybatisPage page = createPage(bean.getPageNumber(), pageSize);

            if (page instanceof MybatisPageWritable) {
                // 设置搜索条件
                ((MybatisPageWritable) page).setParameterObject(bean.getSearchTerms());
                // 设置排序条件
                ((MybatisPageWritable) page).setSorts(bean.getSorts());
            }

            return page;
        }
    }
}
