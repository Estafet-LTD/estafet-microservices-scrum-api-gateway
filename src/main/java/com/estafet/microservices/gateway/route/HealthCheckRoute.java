package com.estafet.microservices.gateway.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.estafet.microservices.gateway.model.ServiceHealth;
import com.estafet.microservices.gateway.service.DiscoveryStewardService;

@Component
public class HealthCheckRoute extends RouteBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckRoute.class);

	@Autowired
	private Environment env;

	@Value("${camel.hystrix.execution-timeout-in-milliseconds}")
	private int hystrixExecutionTimeout;
	
	@Value("${camel.hystrix.group-key}")
	private String hystrixGroupKey;
	
	@Value("${camel.hystrix.execution-timeout-enabled}")
	private boolean hystrixCircuitBreakerEnabled;
	
	@Autowired
	private DiscoveryStewardService discoveryStewardService;
	
	@Override
	public void configure() throws Exception {
		LOGGER.info("- Initialize and configure /service route");

		try {
			getContext().setTracing(Boolean.parseBoolean(env.getProperty("ENABLE_TRACER", "false")));	
		} catch (Exception e) {
			LOGGER.error("Failed to parse the ENABLE_TRACER value: {}", env.getProperty("ENABLE_TRACER"));
		}

		restConfiguration().component("servlet")
		.apiContextPath("/api-docs")
		.bindingMode(RestBindingMode.auto);
		
		rest("/health-api")			
			.get("/{serviceName}")
			.param()
				.name("serviceName")
				.type(RestParamType.path)
			.endParam()
			.route()
			.id("healthCheck")
			.hystrix()
				.id("Health Check")
				.hystrixConfiguration()
				.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
				.groupKey(hystrixGroupKey)
				.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.end()
			.process((exchange) -> {
				ServiceHealth serviceHealth = discoveryStewardService.checkServiceHealth(exchange.getIn().getHeader("serviceName").toString());
				
				exchange.getIn().setBody(serviceHealth.toJSON().getBytes());
			})
			.onFallback()
				.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultHealthCheckFallback")
			.end()
			.setHeader("CamelJacksonUnmarshalType", simple(ServiceHealth.class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, ServiceHealth.class)
		.endRest();
		
	
	    from("direct:defaultHealthCheckFallback").routeId("defaultHealthCheckFallback")
	    .process((exchange)->{
			Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("Hystrix Default fallback. Service down!", cause);
			exchange.getIn().setBody(new ServiceHealth(exchange.getIn().getHeader("serviceName").toString(), "Service is not available", "NONE"));
		}).marshal().json(JsonLibrary.Jackson);
	}
}
