package com.estafet.microservices.gateway;

import org.apache.camel.component.hystrix.metrics.servlet.HystrixEventStreamServlet;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableCircuitBreaker 
public class ApiGatewayApplication extends SpringBootServletInitializer {

	private static final String CAMEL_URL_MAPPING = "/api/*";
	private static final String CAMEL_SERVLET_NAME = "CamelServlet";
	private static final String HYSTRIX_URL_MAPPING = "/hystrix.stream";

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet(), CAMEL_URL_MAPPING);
		
		registration.setName(CAMEL_SERVLET_NAME);
		
		return registration;
	}

	@Bean
	public ServletRegistrationBean metricsServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new HystrixEventStreamServlet(), HYSTRIX_URL_MAPPING);
		
		return registration;
	}

}
