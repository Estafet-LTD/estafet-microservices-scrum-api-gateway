package com.estafet.microservices.gateway.model;

import java.util.concurrent.ThreadLocalRandom;

public class AcceptanceCriterion {

	private int id;

	private String description;

	public AcceptanceCriterion init() {
		description = "Acceptance #" + ThreadLocalRandom.current().nextInt(1, 10000);
		return this;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

}
