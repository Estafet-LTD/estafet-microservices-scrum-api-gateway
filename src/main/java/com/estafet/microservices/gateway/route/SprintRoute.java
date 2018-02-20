package com.estafet.microservices.gateway.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class SprintRoute extends RouteBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(SprintRoute.class);

	@Value("${camel.hystrix.execution-timeout-in-milliseconds}")
	private int hystrixExecutionTimeout;
	
	@Value("${camel.hystrix.group-key}")
	private String hystrixGroupKey;
	
	@Value("${camel.hystrix.execution-timeout-enabled}")
	private boolean hystrixCircuitBreakerEnabled;
	
	@Value("${application.estafet.projectUrl}")
	private String sprintUrl;
	
	@Autowired
	private Environment env;
	
	@Override
	public void configure() throws Exception {
		LOGGER.info("- Initialize and configure /sprint route");
		
		try {
			getContext().setTracing(Boolean.parseBoolean(env.getProperty("ENABLE_TRACER", "false")));	
		} catch (Exception e) {
			LOGGER.error("Failed to parse the ENABLE_TRACER value: {}", env.getProperty("ENABLE_TRACER", "false"));
		}
		
		JacksonDataFormat productFormatter = new ListJacksonDataFormat();
		productFormatter.setUnmarshalType(Object[].class);

		restConfiguration().component("servlet")
		.apiContextPath("/api-docs")
		.contextPath("/api")
		.bindingMode(RestBindingMode.auto);
		
		rest("/sprint")
			.produces(MediaType.ALL_VALUE)
		.get("/sprint/{id}")
			.param()
				.name("id")
				.type(RestParamType.path)
			.endParam()
			.route()
			.id("getSprintRoute")
		.hystrix()
			.id("Get Sprint By Id")
			.hystrixConfiguration()
			.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
			.groupKey(hystrixGroupKey)
			.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.requestLogEnabled(true)
		.end()
		.removeHeaders("CamelHttp*")
		.setBody(simple("null"))
		.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
		.setHeader(Exchange.HTTP_URI, simple(sprintUrl))
		.to("http4://DUMMY")
		.onFallback()
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultFallback")
		.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Object[].class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Object[].class)
		.endRest()
		
		.get("/project/{id}/sprints")
			.param()
				.name("id")
				.type(RestParamType.path)
			.endParam()
			.route()
			.id("getProjectSprintsById")
		.hystrix()
			.id("Get Project Sprints By Project Id")
			.hystrixConfiguration()
			.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
			.groupKey(hystrixGroupKey)
			.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.requestLogEnabled(true)
		.end()
		.removeHeaders("CamelHttp*")
		.setBody(simple("null"))
		.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
		.setHeader(Exchange.HTTP_URI, simple(sprintUrl))
		.to("http4://DUMMY")
		.onFallback()
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultFallback")
		.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Object[].class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Object[].class)
		.endRest();
				
	}

}
