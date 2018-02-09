package com.estafet.microservices.gateway.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Project {

	private Integer id;
	private String title;
	private Integer noSprints;
	private Integer sprintLengthDays;

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Project setTitle(String title) {
		this.title = title;
		return this;
	}

	public Integer getNoSprints() {
		return noSprints;
	}

	public void setNoSprints(Integer noSprints) {
		this.noSprints = noSprints;
	}

	public Integer getSprintLengthDays() {
		return sprintLengthDays;
	}

	public void setSprintLengthDays(Integer sprintLengthDays) {
		this.sprintLengthDays = sprintLengthDays;
	}
	
	public String toJSON() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static Project getAPI() {
		Project project = new Project();
		project.id = 1;
		project.title = "my project";
		return project;
	}

}
