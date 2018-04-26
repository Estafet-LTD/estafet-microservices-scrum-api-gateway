package com.estafet.microservices.gateway.service;

import com.estafet.microservices.gateway.model.ServiceHealth;
import com.netflix.hystrix.exception.HystrixBadRequestException;

public interface DiscoveryStewardService {
	ServiceHealth checkServiceHealth(String serviceName);
	
	String getServiceUrl(String serviceName) throws HystrixBadRequestException;
}
