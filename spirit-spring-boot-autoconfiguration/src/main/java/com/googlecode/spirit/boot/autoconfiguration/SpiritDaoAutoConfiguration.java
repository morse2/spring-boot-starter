package com.googlecode.spirit.boot.autoconfiguration;

import com.googlecode.easyec.spirit.dao.id.IdentifierGenerator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by JunJie on 10/27/16.
 */
@Configuration
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(SpiritDaoProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@ImportAutoConfiguration({
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class
})
public class SpiritDaoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IdentifierGenerator identifierGenerator() {
        return null;
    }

    @Bean
    @Order
    @ConditionalOnMissingBean
    public PlatformTransactionManager transactionManager() {
        return null;
    }
}
