package com.estafet.microservices.gateway.route;

import java.awt.List;
import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpMethods;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.jackson.ListJacksonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.apache.camel.http.common.HttpMessage;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.estafet.microservices.gateway.model.Project;
import com.netflix.hystrix.exception.HystrixTimeoutException;

@Component
public class ProjectRoute extends RouteBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectRoute.class);

	@Value("${camel.hystrix.execution-timeout-in-milliseconds}")
	private int hystrixExecutionTimeout;
	
	@Value("${camel.hystrix.group-key}")
	private String hystrixGroupKey;
	
	@Value("${camel.hystrix.execution-timeout-enabled}")
	private boolean hystrixCircuitBreakerEnabled;
	
	@Autowired
	private Environment env;
	
	@Override
	public void configure() throws Exception {
		LOGGER.info("------------------ Initialize and configure /project route");
		
		try {
			getContext().setTracing(Boolean.parseBoolean(env.getProperty("ENABLE_TRACER", "false")));	
		} catch (Exception e) {
			LOGGER.error("Failed to parse the ENABLE_TRACER value: {}", env.getProperty("ENABLE_TRACER", "false"));
		}
		
		JacksonDataFormat productFormatter = new ListJacksonDataFormat();
//		productFormatter.setUnmarshalType(Project.class);
		productFormatter.setUnmarshalType(Object[].class);

		restConfiguration().component("servlet")
		.apiContextPath("/api-docs")
		.contextPath("/api")
		.bindingMode(RestBindingMode.auto);

		
		rest("/project")
			.produces(MediaType.ALL_VALUE)
		.get("/")
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
			.setBody(simple("null"))
			.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
			.setHeader(Exchange.HTTP_URI, simple("http://project-api.microservices-scrum.svc:8080/projects"))
		.to("http4://DUMMY")
		.onFallback()
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultFallback")
		.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Object[].class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Object[].class)
		.endRest();
			
		
		 // Provide a response
        from("direct:defaultFallback").routeId("defaultfallback")
        .process(new Processor() {
        	@Override
			public void process(Exchange exchange) throws Exception {
        		Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        		LOGGER.error(cause.getStackTrace().toString());
        		exchange.getIn().setBody(new ArrayList<Project>());
			}
        })
        .marshal().json(JsonLibrary.Jackson);
	}

}