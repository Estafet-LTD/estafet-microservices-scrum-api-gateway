package com.estafet.microservices.gateway.config;

import java.util.HashMap;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="application")
public class ApplicationProperties {
	private HashMap<String, String> services;

	public HashMap<String, String> getServices() {
		return services;
	}

	public void setServices(HashMap<String, String> services) {
		this.services = services;
	}
	
	public String findServiceUriByName(String serviceName) {
		return getServices().get(serviceName);
	}
}
