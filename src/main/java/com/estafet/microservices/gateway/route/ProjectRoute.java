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
import com.estafet.microservices.gateway.model.Project;
import com.estafet.microservices.gateway.service.DiscoveryStewardService;

@Component
public class ProjectRoute extends RouteBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRoute.class);

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
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Override
	public void configure() throws Exception {
		LOGGER.info("- Initialize and configure /project route");

		try {
			getContext().setTracing(Boolean.parseBoolean(env.getProperty("ENABLE_TRACER", "true")));	
		} catch (Exception e) {
			LOGGER.error("Failed to parse the ENABLE_TRACER value: {}", env.getProperty("ENABLE_TRACER", "false"));
		}

		restConfiguration().component("servlet")
		.apiContextPath("/api-docs")
		.bindingMode(RestBindingMode.auto);
		
		rest("/project-api")
			.produces(MediaType.APPLICATION_JSON_VALUE)
		
		//Create new project
		.post("/project")
			.type(Project.class)
			.route()
				.id("CreateNewProject")
			.hystrix()
				.id("Create new Project")
			.hystrixConfiguration()
				.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
				.groupKey(hystrixGroupKey)
				.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.end()
			.removeHeaders("CamelHttp*")
			.process((exchange)->{
				Project project = (Project) exchange.getIn().getBody();
				exchange.getIn().setBody(project.toJSON().getBytes());
			})
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
			.setHeader(Exchange.HTTP_URI, simple(applicationProperties.findServiceUriByName("project-api") + "/project"))
			.to("http4://DUMMY")
			.onFallback()
				.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.to("direct:defaultProjectFallback")
			.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Project.class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Project.class)
		.endRest()
		
		//Get All Projects
		.get("/project")
		.route()
			.id("getProjectRoute")
		.hystrix()
			.id("project")
		.hystrixConfiguration()
			.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
			.groupKey(hystrixGroupKey)
			.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.requestLogEnabled(true)
		.end()
		.removeHeaders("CamelHttp*")
		.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.setHeader(Exchange.HTTP_URI, simple(applicationProperties.findServiceUriByName("project-api") + "/project"))
		.to("http4://DUMMY")
		.onFallback()
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultListOfProjectsFallback")
		.end()
		.setHeader("CamelJacksonUnmarshalType", simple(Project[].class.getName())).unmarshal()
		.json(JsonLibrary.Jackson, Project[].class)
		.endRest()
		
		.get("/project/{id}")
			.param()
				.name("id")
				.type(RestParamType.path)
			.endParam()
			.route()
				.id("getProjectById")
			.hystrix()
				.id("Get Project By Id")
			.hystrixConfiguration()
				.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
				.groupKey(hystrixGroupKey)
				.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.end()
			.removeHeaders("CamelHttp*")
			.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.setHeader(Exchange.HTTP_URI, simple(applicationProperties.findServiceUriByName("project-api") + "/project/${header.id}"))
			.to("http4://DUMMY")
			.onFallback()
				.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.to("direct:defaultProjectFallback")
			.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Project.class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Project.class)
		.endRest();

		
		 // Default fallback returns empty list of projects
	    from("direct:defaultListOfProjectsFallback").routeId("defaultListOfProjectsFallback")
	    .process((exchange)->{
			Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("Hystrix Default fallback. Empty list of project returned", cause);
			exchange.getIn().setBody(new ArrayList<Project>());
		}).marshal().json(JsonLibrary.Jackson);
	    
		 // Default fallback returns empty project
	    from("direct:defaultProjectFallback").routeId("defaultProjectFallback")
	    .process((exchange)->{
			Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("Hystrix Default fallback. Empty project returned", cause);
			exchange.getIn().setBody(new Project());
	    }).marshal().json(JsonLibrary.Jackson);

	}
}
