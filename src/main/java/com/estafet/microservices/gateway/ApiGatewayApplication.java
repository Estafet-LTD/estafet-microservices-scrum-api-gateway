package com.estafet.microservices.gateway;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.opentracing.starter.CamelOpenTracing;
import org.apache.camel.spi.RestConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

@SpringBootApplication
@Configuration
@ComponentScan
@EnableAutoConfiguration
@CamelOpenTracing
@EnableCircuitBreaker 
@EnableDiscoveryClient
public class ApiGatewayApplication extends SpringBootServletInitializer {

	private static final String CAMEL_SERVLET_NAME = "CamelServlet";
	private static final String HYSTRIX_URL_MAPPING = "/hystrix.stream";

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}
	
	@Bean
	public io.opentracing.Tracer jaegerTracer() {
		return new com.uber.jaeger.Configuration("gateway",
				com.uber.jaeger.Configuration.SamplerConfiguration.fromEnv(),
				com.uber.jaeger.Configuration.ReporterConfiguration.fromEnv())
				.getTracer();
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.build();
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new CamelHttpTransportServlet());

		registration.setName(CAMEL_SERVLET_NAME);
		
		return registration;
	}

	@Bean
	public ServletRegistrationBean metricsServlet() {
		ServletRegistrationBean registration = new ServletRegistrationBean(new HystrixMetricsStreamServlet(), HYSTRIX_URL_MAPPING);
		
		return registration;
	}
	
	
	private class CORSServlet extends CamelHttpTransportServlet {
		private static final long serialVersionUID = 1L;

		@Override
        protected void doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            String origin = request.getHeader("Origin");
            if (origin == null || origin.isEmpty()) {
                origin = "*";
            }

            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Methods", RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_METHODS);
            response.setHeader("Access-Control-Allow-Headers", "Authorization, " + RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_HEADERS);
            response.setHeader("Access-Control-Max-Age", RestConfiguration.CORS_ACCESS_CONTROL_MAX_AGE);
            response.setHeader("Access-Control-Allow-Credentials", "true");

            super.doService(request, response);
        }
    }

}
