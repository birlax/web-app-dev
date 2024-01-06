package com.birlax.dbCommonUtils.spring;

import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(basePackages = "com.birlax.dbCommonUtils")
// @PropertySource("classpath:com/birlax/dbCommonUtils/application.properties")
@PropertySource("file:${app.home}/app.properties")
public class DataAccessContext {

    @Inject
    private Environment environment;

    private String temporaryDatabaseOrSchemaName;

    @Bean
    public DataSource dataSource() {
        Properties driverProperties = new Properties();
        driverProperties.setProperty("ApplicationName", environment
                .getRequiredProperty(DbCommonUtilsApplicationContext.applicationContext() + "applicationName"));
        String url = environment
                .getRequiredProperty(DbCommonUtilsApplicationContext.applicationContext() + "datasource.url");
        String driver = environment
                .getRequiredProperty(DbCommonUtilsApplicationContext.applicationContext() + "datasource.driver");

        driverProperties.putIfAbsent(
                environment.getRequiredProperty(
                        DbCommonUtilsApplicationContext.applicationContext() + "datasource.username.keyword"),
                environment.getRequiredProperty(
                        DbCommonUtilsApplicationContext.applicationContext() + "datasource.username"));

        driverProperties.putIfAbsent(environment.getRequiredProperty(
                DbCommonUtilsApplicationContext.applicationContext() + "datasource.password.keyword")
                , environment.getRequiredProperty(
                        DbCommonUtilsApplicationContext.applicationContext() + "datasource.password"));

        DataSource dataSource = new PooledDataSource(driver, url, driverProperties);
        return dataSource;
    }

    @Bean
    public String temporaryDatabaseOrSchemaName() {
        if (this.temporaryDatabaseOrSchemaName == null) {
            this.temporaryDatabaseOrSchemaName = environment.getRequiredProperty(
                    DbCommonUtilsApplicationContext.applicationContext() + "datasource.temparory.database_or_schema");
        }
        return this.temporaryDatabaseOrSchemaName;
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
