package com.estafet.microservices.gateway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sprint {

	private Integer id;


	public Integer getId() {
		return id;
	}

}
