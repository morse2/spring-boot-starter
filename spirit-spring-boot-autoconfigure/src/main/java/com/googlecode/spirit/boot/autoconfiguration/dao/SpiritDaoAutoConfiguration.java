package com.googlecode.spirit.boot.autoconfiguration.dao;

import com.googlecode.easyec.spirit.dao.dialect.PageDialect;
import com.googlecode.easyec.spirit.dao.id.IdentifierGenerator;
import com.googlecode.easyec.spirit.dao.id.IdentifierNameConverter;
import com.googlecode.easyec.spirit.dao.id.SequenceGenerator;
import com.googlecode.easyec.spirit.dao.id.impl.*;
import com.googlecode.easyec.spirit.dao.id.support.PlatformSequenceGenerateDecisionInterceptor;
import com.googlecode.easyec.spirit.dao.paging.JdbcPage;
import com.googlecode.easyec.spirit.dao.paging.JdbcPageWritable;
import com.googlecode.easyec.spirit.dao.paging.PageProxy;
import com.googlecode.easyec.spirit.dao.paging.PagingInterceptor;
import com.googlecode.easyec.spirit.dao.paging.factory.PageDelegate;
import com.googlecode.easyec.spirit.dao.paging.support.JdbcPageProxy;
import com.googlecode.easyec.spirit.dao.paging.support.JdbcPagingInterceptor;
import com.googlecode.easyec.spirit.web.controller.formbean.impl.AbstractSearchFormBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * Created by JunJie on 10/27/16.
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(SpiritDaoProperties.class)
public class SpiritDaoAutoConfiguration {

    @Resource
    private SpiritDaoProperties spiritDaoProperties;

    @Configuration
    @Import(DefaultPageDialectConfigurer.class)
    static class PageDialectConfig {
        /* no op */
    }

    @Bean
    @ConditionalOnBean(PageDialect.class)
    @ConditionalOnMissingBean(PageDelegate.class)
    @ConditionalOnProperty(prefix = "spirit.dao", name = "use-page-delegate", havingValue = "true")
    public PageDelegate<JdbcPage> pageConfigurer(PageDialect pageDialect) {
        return new InternalJdbcPageDelegate(spiritDaoProperties.getPageSize(), pageDialect, new JdbcPageProxy());
    }

    @Bean
    @ConditionalOnMissingBean(IdentifierNameConverter.class)
    @ConditionalOnProperty(prefix = "spirit.dao", name = "use-sequence-generator", havingValue = "true", matchIfMissing = true)
    public AnnotatedIdentifierNameConverter annotatedIdentifierNameConverter() {
        return new AnnotatedIdentifierNameConverter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "spirit.dao", name = { "use-sequence-generator", "use-default-id-generators" }, havingValue = "true", matchIfMissing = true)
    public List<IdentifierGenerator> identifierGenerators() {
        List<IdentifierGenerator> identifierGenerators = new ArrayList<IdentifierGenerator>();
        identifierGenerators.add(new LongValueHiloIdentifierGenerator(spiritDaoProperties.getMaxLoValue()));
        identifierGenerators.add(new IntegerValueHiloIdentifierGenerator(spiritDaoProperties.getMaxLoValue()));
        identifierGenerators.add(new ShortValueHiloIdentifierGenerator(spiritDaoProperties.getMaxLoValue()));

        return identifierGenerators;
    }

    @Bean
    @ConditionalOnMissingBean(SequenceGenerator.class)
    @ConditionalOnProperty(prefix = "spirit.dao", name = "use-sequence-generator", havingValue = "true", matchIfMissing = true)
    public SequenceGenerator sequenceGenerator(DataSource dataSource, IdentifierNameConverter identifierNameConverter, List<IdentifierGenerator> identifierGenerators) {
        try {
            InternalSequenceGeneratorFactoryBean fb = new InternalSequenceGeneratorFactoryBean();
            fb.setIdentifierNameConverter(identifierNameConverter);
            fb.setIdentifierGenerators(identifierGenerators);
            fb.setDataSource(dataSource);
            fb.afterPropertiesSet();

            return fb.getObject();
        } catch (Exception e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }

    @Bean
    @ConditionalOnBean({ PlatformTransactionManager.class, SequenceGenerator.class })
    public PlatformSequenceGenerateDecisionInterceptor sequenceGenerateDecisionInterceptor(
        PlatformTransactionManager transactionManager, SequenceGenerator sequenceGenerator) {
        PlatformSequenceGenerateDecisionInterceptor interceptor = new PlatformSequenceGenerateDecisionInterceptor();
        interceptor.setOrder(spiritDaoProperties.getIdGenerateInterceptorOrder());
        interceptor.setTransactionManager(transactionManager);
        interceptor.setSequenceGenerator(sequenceGenerator);

        return interceptor;
    }

    @Bean
    @ConditionalOnMissingBean(PagingInterceptor.class)
    @ConditionalOnProperty(prefix = "spirit.dao", name = "use-paging-interceptor", havingValue = "true")
    public JdbcPagingInterceptor pagingInterceptor(DataSource dataSource) {
        JdbcPagingInterceptor interceptor = new JdbcPagingInterceptor();
        interceptor.setOrder(spiritDaoProperties.getPagingInterceptOrder());
        interceptor.setDataSource(dataSource);

        return interceptor;
    }

    private static class InternalJdbcPageDelegate extends PageDelegate<JdbcPage> {

        InternalJdbcPageDelegate(Integer pageSize, PageDialect pageDialect, PageProxy<JdbcPage> pageProxy) {
            super(pageSize, pageDialect, pageProxy);
        }

        @Override
        public JdbcPage createPage(AbstractSearchFormBean abstractSearchFormBean) {
            return createPage(abstractSearchFormBean, getPageSize());
        }

        @Override
        public JdbcPage createPage(AbstractSearchFormBean bean, int pageSize) {
            JdbcPage page = createPage(bean.getPageNumber(), pageSize);
            if (page instanceof JdbcPageWritable) {
                ((JdbcPageWritable) page).setSearchTerms(bean.getSearchTerms());
            }

            return page;
        }
    }

    /* Internal SequenceGenerator Factory Bean Class */
    private static class InternalSequenceGeneratorFactoryBean extends AbstractFactoryBean<SequenceGenerator> {

        private List<IdentifierGenerator> identifierGenerators;
        private IdentifierNameConverter identifierNameConverter;
        private DataSource dataSource;

        void setIdentifierGenerators(List<IdentifierGenerator> identifierGenerators) {
            this.identifierGenerators = identifierGenerators;
        }

        void setIdentifierNameConverter(IdentifierNameConverter identifierNameConverter) {
            this.identifierNameConverter = identifierNameConverter;
        }

        void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public Class<?> getObjectType() {
            return SequenceGenerator.class;
        }

        @Override
        protected SequenceGenerator createInstance() throws Exception {
            Assert.notNull(identifierNameConverter, "IdentifierNameConverter bean cannot find in spring context.");
            Assert.notNull(dataSource, "DataSource bean cannot find in spring context.");

            DomainModelSequenceGeneratorChain instance = new DomainModelSequenceGeneratorChain();
            instance.setIdentifierNameConverter(identifierNameConverter);
            instance.setDataSource(dataSource);

            if (isNotEmpty(identifierGenerators)) {
                instance.setIdentifierGenerators(identifierGenerators);
            }

            Assert.isTrue(
                isNotEmpty(instance.getIdentifierGenerators()),
                "IdentifierGenerator cannot be null or empty."
            );

            return instance;
        }
    }
}
