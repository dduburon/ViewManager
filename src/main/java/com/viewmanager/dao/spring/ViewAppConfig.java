package com.viewmanager.dao.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {DatabaseConfig.class, ViewServicesConfig.class})
public class ViewAppConfig {

}
