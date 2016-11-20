package com.googlecode.spirit.boot.autoconfiguration.dao;

import com.googlecode.easyec.spirit.dao.dialect.impl.MySqlJdbcPageDialect;
import com.googlecode.easyec.spirit.dao.dialect.impl.NoOpPageDialect;
import com.googlecode.easyec.spirit.dao.dialect.impl.OracleJdbcPageDialect;
import com.googlecode.easyec.spirit.dao.dialect.impl.PostgreSqlJdbcPageDialect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by JunJie on 11/20/16.
 */
@Configuration
class DefaultPageDialectConfigurer {

    @Bean(name = "pageDialect")
    @ConditionalOnProperty(prefix = "spirit.dao", name = "page-dialect", havingValue = "MySql")
    public MySqlJdbcPageDialect mySqlJdbcPageDialect() {
        return new MySqlJdbcPageDialect();
    }

    @Bean(name = "pageDialect")
    @ConditionalOnProperty(prefix = "spirit.dao", name = "page-dialect", havingValue = "Oracle")
    public OracleJdbcPageDialect oracleJdbcPageDialect() {
        return new OracleJdbcPageDialect();
    }

    @Bean(name = "pageDialect")
    @ConditionalOnProperty(prefix = "spirit.dao", name = "page-dialect", havingValue = "PostgreSql")
    public PostgreSqlJdbcPageDialect postgreSqlJdbcPageDialect() {
        return new PostgreSqlJdbcPageDialect();
    }

    @Bean(name = "pageDialect")
    @ConditionalOnProperty(prefix = "spirit.dao", name = "page-dialect", matchIfMissing = true)
    public NoOpPageDialect noOpPageDialect() {
        return new NoOpPageDialect();
    }
}
