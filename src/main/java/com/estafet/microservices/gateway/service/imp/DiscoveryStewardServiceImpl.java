package com.estafet.microservices.gateway.service.imp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import com.estafet.microservices.gateway.config.ApplicationProperties;
import com.estafet.microservices.gateway.service.DiscoveryStewardService;
import com.netflix.hystrix.exception.HystrixBadRequestException;

@Service
public class DiscoveryStewardServiceImpl implements DiscoveryStewardService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryStewardServiceImpl.class);

	@Autowired
	private DiscoveryClient discoveryClient;
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Override
	public boolean checkServiceHealth(String serviceName) {
		List<ServiceInstance> services = discoveryClient.getInstances(serviceName);
		return !services.isEmpty();
	}

	@Override
	public String getServiceUrl(String serviceName) {
		try {
			if(!checkServiceHealth(serviceName)) {
				throw new HystrixBadRequestException(String.format("All %s pods are down!", serviceName));
			}
		}catch (HystrixBadRequestException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return applicationProperties.getServices().get(serviceName);
	}

}
