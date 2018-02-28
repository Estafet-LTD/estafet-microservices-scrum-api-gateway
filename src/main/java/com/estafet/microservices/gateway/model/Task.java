package com.estafet.microservices.gateway.model;

import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {

	private Integer id;

	private String title;

	private String description;

	private Integer initialHours;

	private Integer remainingHours;

	private String status = "Not Started";

	private String remainingUpdated;

	private String storyTitle;

	public Task init() {
		this.title = "Task #" + ThreadLocalRandom.current().nextInt(1, 10000);
		this.description = title;
		this.initialHours =  ThreadLocalRandom.current().nextInt(1, 20);
		return this;
	}
	
	public Task setRemainingHours(Integer remainingHours) {
		this.remainingHours = remainingHours;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Integer getInitialHours() {
		return initialHours;
	}

	public Integer getRemainingHours() {
		return remainingHours;
	}

	public String getRemainingUpdated() {
		return remainingUpdated;
	}

	public String getStoryTitle() {
		return storyTitle;
	}

	public Task setRemainingUpdated(String remainingUpdated) {
		this.remainingUpdated = remainingUpdated;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public Task setTitle(String title) {
		this.title = title;
		return this;
	}

	public Task setDescription(String description) {
		this.description = description;
		return this;
	}

	public Task setInitialHours(Integer initialHours) {
		this.initialHours = initialHours;
		return this;
	}

}
