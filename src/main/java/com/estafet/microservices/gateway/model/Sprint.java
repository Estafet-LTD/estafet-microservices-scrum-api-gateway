package com.estafet.microservices.gateway.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sprint {

	private Integer id;

	private Integer projectId;

	private String startDate;

	private String endDate;

	private Integer number;

	private String status;

	private Integer noDays;

	private List<Story> sprintStories = new ArrayList<Story>();

	private List<Story> nonSprintStories = new ArrayList<Story>();

	public Sprint addStories(List<Story> stories) {
		for (Story story : stories) {
			if (story.getSprintId() == id) {
				sprintStories.add(story);
			} else if (!status.equals("Completed") && !story.getStatus().equals("Completed")) {
				nonSprintStories.add(story);
			}
		}
		return this;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getStatus() {
		return status;
	}

	public Integer getId() {
		return id;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public Integer getNumber() {
		return number;
	}

	public Integer getNoDays() {
		return noDays;
	}

	public String getName() {
		return "Sprint #" + number;
	}

	public List<Story> getSprintStories() {
		return sprintStories;
	}

	public List<Story> getNonSprintStories() {
		return nonSprintStories;
	}

}
