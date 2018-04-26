package com.estafet.microservices.gateway.service.imp;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.estafet.microservices.gateway.config.ApplicationProperties;
import com.estafet.microservices.gateway.model.ServiceHealth;
import com.estafet.microservices.gateway.service.DiscoveryStewardService;
import com.netflix.hystrix.exception.HystrixBadRequestException;

@Service
public class DiscoveryStewardServiceImpl implements DiscoveryStewardService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryStewardServiceImpl.class);

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ApplicationProperties applicationProperties;

	@Override
	public ServiceHealth checkServiceHealth(String serviceName) {
		List<ServiceInstance> services = discoveryClient.getInstances(serviceName);

		if (!services.isEmpty()) {
			try {				
				ServiceHealth serviceHealth = restTemplate.getForObject(applicationProperties.getServices().get(serviceName) + "/health", ServiceHealth.class);
				serviceHealth.setServiceName(serviceName);

				if (serviceHealth != null && serviceHealth.getStatus() != null
						&& serviceHealth.getStatus().equals("UP")) {
					return serviceHealth;
				}
			} catch (ResourceAccessException e) {
				LOGGER.error(e.getMessage(), e);
				return new ServiceHealth(serviceName, "Service is not available", "NONE");
			}
		}

		return new ServiceHealth(serviceName, "Service is not available", "NONE");
	}

	@Override
	public String getServiceUrl(String serviceName) {
		try {
			if (checkServiceHealth(serviceName).getStatus() != null && checkServiceHealth(serviceName).getStatus() != null
					&& !checkServiceHealth(serviceName).getStatus().equals("UP")) {
				throw new HystrixBadRequestException(String.format("All %s pods are down!", serviceName));
			}
		} catch (HystrixBadRequestException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return applicationProperties.getServices().get(serviceName);
	}
}
