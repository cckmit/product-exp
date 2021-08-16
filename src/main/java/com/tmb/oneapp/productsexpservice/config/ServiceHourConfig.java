package com.tmb.oneapp.productsexpservice.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ServiceHourConfig implements WebMvcConfigurer {

	private final ServiceHourInterceptor serviceHourConfiguration;

	@Lazy
	@Autowired
	public ServiceHourConfig(ServiceHourInterceptor serviceHourConfiguration) {
		this.serviceHourConfiguration = serviceHourConfiguration;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		List<String> serviceHourCheckArray = new ArrayList<>();
		serviceHourCheckArray.add("/filter/*");
		registry.addInterceptor(serviceHourConfiguration).addPathPatterns(serviceHourCheckArray);

	}

}
