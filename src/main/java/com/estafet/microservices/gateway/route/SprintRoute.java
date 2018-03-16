package com.estafet.microservices.gateway.route;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
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

import com.estafet.microservices.gateway.config.ApplicationProperties;
import com.estafet.microservices.gateway.model.Sprint;
import com.estafet.microservices.gateway.service.DiscoveryStewardService;

@Component
public class SprintRoute extends RouteBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(SprintRoute.class);

	@Value("${camel.hystrix.execution-timeout-in-milliseconds}")
	private int hystrixExecutionTimeout;
	
	@Value("${camel.hystrix.group-key}")
	private String hystrixGroupKey;
	
	@Value("${camel.hystrix.execution-timeout-enabled}")
	private boolean hystrixCircuitBreakerEnabled;

	@Autowired
	private DiscoveryStewardService discoveryStewardService;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
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
		
		restConfiguration().component("servlet")
		.apiContextPath("/api-docs")
		.bindingMode(RestBindingMode.auto);
		
		rest("/sprint-api")
			.produces(MediaType.ALL_VALUE)
		
		//Get sprint by sprint id
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
		.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.setHeader(Exchange.HTTP_URI, simple(applicationProperties.getServices().get("sprint-api") + "/sprint/${header.id}"))
		.to("http4://DUMMY")
		.onFallback()
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultSprintFallback")
		.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Sprint.class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Sprint.class)
		.endRest()
		
		//Get all project sprints by project id
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
		.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.setHeader(Exchange.HTTP_URI, simple(applicationProperties.getServices().get("sprint-api") + "/project/${header.id}/sprints"))
		.to("http4://DUMMY")
		.onFallback()
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultListOfSprintsFallback")
		.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Sprint[].class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Sprint[].class)
		.endRest();
	
		 // Default fallback returns empty list of projects
	    from("direct:defaultListOfSprintsFallback").routeId("defaultListOfSprintsFallback")
	    .process((exchange) -> {
    		Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("Hystrix Default fallback. Empty list of sprint returned", cause);
    		exchange.getIn().setBody(new ArrayList<Sprint>());
	    }) .marshal().json(JsonLibrary.Jackson);
	    
		 // Default fallback returns empty project
	    from("direct:defaultSprintFallback").routeId("defaultSprintFallback")
	    .process((exchange) -> {
			Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("Hystrix Default fallback. Empty sprints returned", cause);
			exchange.getIn().setBody(new Sprint());
	    }) .marshal().json(JsonLibrary.Jackson);
	
	}
}
