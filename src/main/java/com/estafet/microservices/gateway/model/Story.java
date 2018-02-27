package com.estafet.microservices.gateway.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Story {

	@JsonIgnore
	private RestTemplate restTemplate;
	
	private Integer id;

	private Integer projectId;

	private String title;

	private String description;

	private Integer storypoints;

	private Integer sprintId;

	private String status;

	private Project project;

	private List<AcceptanceCriterion> criteria = new ArrayList<AcceptanceCriterion>();

	public Story init() {
		this.title = "Story #" + ThreadLocalRandom.current().nextInt(1, 10000);
		this.description = this.title;
		int points[] = {1, 2, 3, 5, 8, 20, 40, 100};
		this.storypoints = points[ThreadLocalRandom.current().nextInt(0, 9)]; 
		return this;
	}
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
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

	public Integer getStorypoints() {
		return storypoints;
	}

	public Integer getSprintId() {
		return sprintId;
	}

	public String getStatus() {
		return status;
	}

	public List<AcceptanceCriterion> getCriteria() {
		return criteria;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStorypoints(Integer storypoints) {
		this.storypoints = storypoints;
	}

	@JsonIgnore
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String toJSON() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
