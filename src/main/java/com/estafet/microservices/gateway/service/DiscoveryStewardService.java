package com.estafet.microservices.gateway.service;

import com.netflix.hystrix.exception.HystrixBadRequestException;

public interface DiscoveryStewardService {
	boolean checkServiceHealth(String serviceName);
	String getServiceUrl(String serviceName) throws HystrixBadRequestException;
}
