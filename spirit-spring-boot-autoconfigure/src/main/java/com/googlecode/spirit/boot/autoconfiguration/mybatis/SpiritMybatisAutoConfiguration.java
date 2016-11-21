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
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Created by JunJie on 11/18/16.
 */
@Configuration
@ConditionalOnBean({ SqlSessionFactory.class })
@EnableConfigurationProperties({ SpiritMybatisProperties.class, SpiritDaoProperties.class })
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class SpiritMybatisAutoConfiguration {

    @Resource
    private SpiritMybatisProperties spiritMybatisProperties;
    @Resource
    private SpiritDaoProperties spiritDaoProperties;

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
