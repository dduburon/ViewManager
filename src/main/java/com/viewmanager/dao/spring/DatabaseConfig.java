package com.viewmanager.dao.spring;

import com.viewmanager.config.ViewMConfig;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Profile("default")
public class DatabaseConfig implements ApplicationContextAware {

	public static final String SQL_SESSION_FACTORY = "sqlSessionFactoryBean";
	ApplicationContext applicationContext;

	@Bean(name = SQL_SESSION_FACTORY)
	public static SqlSessionFactoryBean sqlSessionFactory() throws Exception {
		SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource());
		factoryBean.setConfigLocation(
				new ClassPathResource("mybatis-config.xml"));
		return factoryBean;
	}

	@Bean
	public static MapperScannerConfigurer myBatisScanner(SqlSessionFactoryBean sqlSessionFactoryBean) {
		MapperScannerConfigurer result = new MapperScannerConfigurer();
		result.setBasePackage("com.viewmanager.dao.mapper");
		result.setSqlSessionFactoryBeanName(SQL_SESSION_FACTORY);
		return result;
	}

	@Bean
	public static PlatformTransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Bean(destroyMethod = "")
	public static PGPoolingDataSource dataSource() {
		PGPoolingDataSource ds = new PGPoolingDataSource();
		ds.setPortNumber(ViewMConfig.getDBPort());
		ds.setServerName(ViewMConfig.getDBServer());
		ds.setUser(ViewMConfig.getDBUser());
		ds.setPassword(ViewMConfig.getDBPass());
		ds.setDatabaseName(ViewMConfig.getDBName());
		return ds;
	}
}