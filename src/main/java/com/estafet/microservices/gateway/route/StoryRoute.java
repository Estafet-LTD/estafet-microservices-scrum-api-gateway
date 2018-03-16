package com.estafet.microservices.gateway.route;

import java.util.ArrayList;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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

import com.estafet.microservices.gateway.model.Story;

@Component
public class StoryRoute extends RouteBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(StoryRoute.class);

	@Value("${camel.hystrix.execution-timeout-in-milliseconds}")
	private int hystrixExecutionTimeout;
	
	@Value("${camel.hystrix.group-key}")
	private String hystrixGroupKey;
	
	@Value("${camel.hystrix.execution-timeout-enabled}")
	private boolean hystrixCircuitBreakerEnabled;
	
	@Value("${application.services.story-api}")
	private String storyUrl;
	
	@Autowired
	private Environment env;
	
	@Override
	public void configure() throws Exception {
		LOGGER.info("- Initialize and configure /story route");
		
		try {
			getContext().setTracing(Boolean.parseBoolean(env.getProperty("ENABLE_TRACER", "false")));	
		} catch (Exception e) {
			LOGGER.error("Failed to parse the ENABLE_TRACER value: {}", env.getProperty("ENABLE_TRACER", "false"));
		}

		restConfiguration().component("servlet")
		.apiContextPath("/api-docs")
		.bindingMode(RestBindingMode.auto);
		
		rest("/story-api")
			.produces(MediaType.ALL_VALUE)
		
		//Create new sprint
		.post("/project/{id}/story")
			.param()
				.name("id")
				.type(RestParamType.path)
			.endParam()
			.type(Story.class)
			.route()
				.id("CreatNewStory")
			.hystrix()
				.id("Create new Story")
			.hystrixConfiguration()
				.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
				.groupKey(hystrixGroupKey)
				.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.end()
			.removeHeaders("CamelHttp*")
			.process((exchange)->{
				Story story = (Story) exchange.getIn().getBody();
				exchange.getIn().setBody(story.toJSON().getBytes());
			})
			.setHeader(Exchange.HTTP_METHOD, HttpMethods.POST)
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.setHeader(Exchange.HTTP_URI, simple(storyUrl + "/project/${header.id}/story"))
			.to("http4://DUMMY")
			.onFallback()
				.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
				.to("direct:defaultStoryFallback")
			.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Story.class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Story.class)
		.endRest()
			
		//Get story by id
		.get("/story/{id}")
			.param()
				.name("id")
				.type(RestParamType.path)
			.endParam()
			.route()
			.id("getStoryRoute")
		.hystrix()
			.id("Get Story By Id")
			.hystrixConfiguration()
			.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
			.groupKey(hystrixGroupKey)
			.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.requestLogEnabled(true)
		.end()
		.removeHeaders("CamelHttp*")
		.setBody(simple("null"))
		.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.setHeader(Exchange.HTTP_URI, simple(storyUrl + "/story/${header.id}"))
		.to("http4://DUMMY")
		.onFallback()
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultStoryFallback")
		.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Story.class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Story.class)
		.endRest()
		
		//Get project stories by project id
		.get("/project/{id}/stories")
			.param()
				.name("id")
				.type(RestParamType.path)
			.endParam()
			.route()
			.id("getProjectStoriesById")
		.hystrix()
			.id("Get Project Stories By Project Id")
			.hystrixConfiguration()
			.executionTimeoutInMilliseconds(hystrixExecutionTimeout)
			.groupKey(hystrixGroupKey)
			.circuitBreakerEnabled(hystrixCircuitBreakerEnabled)
			.requestLogEnabled(true)
		.end()
		.removeHeaders("CamelHttp*")
		.setBody(simple("null"))
		.setHeader(Exchange.HTTP_METHOD, HttpMethods.GET)
		.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
		.setHeader(Exchange.HTTP_URI, simple(storyUrl + "/project/${header.id}/stories"))
		.to("http4://DUMMY")
		.onFallback()
			.setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
			.to("direct:defaultListOfStoriesFallback")
		.end()
			.setHeader("CamelJacksonUnmarshalType", simple(Story[].class.getName())).unmarshal()
			.json(JsonLibrary.Jackson, Story[].class)
		.endRest();
		
		
		// Default fallback returns empty list of stories
	    from("direct:defaultListOfStoriesFallback").routeId("defaultListOfStoriesFallback")
	    .process((exchange) -> {
			Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
			LOGGER.error("Hystrix Default fallback. Empty list of stories returned", cause);
			exchange.getIn().setBody(new ArrayList<Story>());
	    }) .marshal().json(JsonLibrary.Jackson);
	    
		// Default fallback returns empty story
	    from("direct:defaultStoryFallback").routeId("defaultStoryFallback")
	    .process(new Processor() {
	    	@Override
			public void process(Exchange exchange) throws Exception {
	    		Exception cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
				LOGGER.error("Hystrix Default fallback. Empty list of story returned", cause);
	    		exchange.getIn().setBody(new Story());
			}
	    }) .marshal().json(JsonLibrary.Jackson);
	
	}
}
