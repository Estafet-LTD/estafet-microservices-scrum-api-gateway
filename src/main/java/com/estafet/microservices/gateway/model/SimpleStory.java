package com.estafet.microservices.gateway.model;

import java.util.ArrayList;
import java.util.List;

public class SimpleStory {

	private int id;

	private String title;

	private String description;

	private Integer storypoints;

	private Integer sprintId;

	private Integer projectId;

	private String status;

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Integer getStorypoints() {
		return storypoints;
	}

	public SimpleStory setStorypoints(Integer storypoints) {
		this.storypoints = storypoints;
		return this;
	}

	public SimpleStory setTitle(String title) {
		this.title = title;
		return this;
	}

	public SimpleStory setDescription(String description) {
		this.description = description;
		return this;
	}

	public int getId() {
		return id;
	}

	public String getStatus() {
		return status;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public Integer getSprintId() {
		return sprintId;
	}

	public SimpleStory setId(int id) {
		this.id = id;
		return this;
	}

	public SimpleStory setSprintId(Integer sprintId) {
		this.sprintId = sprintId;
		return this;
	}

	public SimpleStory setProjectId(Integer projectId) {
		this.projectId = projectId;
		return this;
	}

	public SimpleStory setStatus(String status) {
		this.status = status;
		return this;
	}

	public static List<SimpleStory> toList(List<Story> stories) {
		List<SimpleStory> simpleStories = new ArrayList<SimpleStory>();
		for (Story story : stories) {
			simpleStories.add(story.toSimple());
		}
		return simpleStories;
	}
	
}
