package com.example.demo.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.configuration.filter.RequestResponseLogFilter;

@Configuration
public class CustomFilterConfig {

	private static final Logger logger = LoggerFactory.getLogger(CustomFilterConfig.class);
	
	@Bean
	public FilterRegistrationBean<RequestResponseLogFilter> loggingFilter(){
	    FilterRegistrationBean<RequestResponseLogFilter> registrationBean = new FilterRegistrationBean<>();
	    registrationBean.setFilter(new RequestResponseLogFilter());
	    registrationBean.addUrlPatterns("/api/transactions/*");
	    logger.info("RequestResponseLogFilter is initialized.");
	    return registrationBean;    
	}
	
}
