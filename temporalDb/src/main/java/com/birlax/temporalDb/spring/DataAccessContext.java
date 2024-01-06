package com.birlax.temporalDb.spring;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(
        basePackages = "com.birlax")
public class DataAccessContext {

    @Bean
    public DataSource dataSource() {
        Properties driverProperties = new Properties();
        driverProperties.setProperty("ApplicationName", "birlax-test-applicat");
        String url = "jdbc:postgresql://localhost:5432/sec_master";
        String driver = "org.postgresql.Driver";
        DataSource dataSource = new PooledDataSource(driver, url, driverProperties);

        return dataSource;
    }

    @Bean
    public org.apache.ibatis.session.Configuration myBatisConfiguration() throws Exception {
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        return configuration;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setConfiguration(myBatisConfiguration());
        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sessionTemplate() throws Exception {
        SqlSessionTemplate sessionTemplate = new SqlSessionTemplate(sqlSessionFactory(), ExecutorType.BATCH);
        return sessionTemplate;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() throws Exception {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource());
        return transactionManager;
    }

}
