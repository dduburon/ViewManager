package com.viewmanager.dao.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("com.viewmanager.dao.service")
@EnableTransactionManagement
@Profile("default")
public class ViewServicesConfig {

}
