package com.birlax.dbCommonUtils.spring;

import jakarta.inject.Inject;
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

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = "com.birlax.dbCommonUtils")
@PropertySource("classpath:db_common/application.properties")
//@PropertySource("file:${app.home}/app.properties")
public class DataAccessContext {

    @Inject
    private Environment environment;

    private String temporaryDatabaseOrSchemaName;

    @Bean
    public DataSource dataSource() {
        Properties driverProperties = new Properties();
        final String contextName = DbCommonUtilsApplicationContext.applicationContext();

        driverProperties.setProperty("ApplicationName",
                environment.getRequiredProperty(contextName + "applicationName"));
        String url = environment.getRequiredProperty(contextName + "datasource.url");
        String driver = environment.getRequiredProperty(contextName + "datasource.driver");

        driverProperties.put("user", environment.getRequiredProperty(contextName + "datasource.username"));
        driverProperties.put("datasource.username", environment.getRequiredProperty(contextName + "datasource.username"));

        driverProperties.put("datasource.password",
                environment.getRequiredProperty(contextName + "datasource.password"));

        return new PooledDataSource(driver, url, driverProperties);
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
        return new SqlSessionTemplate(sqlSessionFactory(), ExecutorType.BATCH);
    }

    @Bean
    public DataSourceTransactionManager transactionManager() throws Exception {
        return new DataSourceTransactionManager(dataSource());
    }

}
