package com.exem.exemone2.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 멀티 데이터소스 설정
 * - exemone2: 자체 PostgreSQL (읽기/쓰기)
 * - exemone-pg: 기존 ExemOne PostgreSQL (읽기 전용)
 * - exemone-clickhouse: 기존 ExemOne ClickHouse (읽기 전용)
 */
@Configuration
public class DataSourceConfig {

    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.exemone2")
    public DataSource exemone2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "exemonePgDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.exemone-pg")
    public DataSource exemonePgDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "clickhouseDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.clickhouse")
    public DataSource clickhouseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "pgJdbcTemplate")
    public JdbcTemplate pgJdbcTemplate(@Qualifier("exemonePgDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean(name = "clickhouseJdbcTemplate")
    public JdbcTemplate clickhouseJdbcTemplate(@Qualifier("clickhouseDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
