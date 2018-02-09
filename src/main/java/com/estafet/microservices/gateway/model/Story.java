package com.estafet.microservices.gateway.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Story {

	private int id;
	private String title;
	private String description;
	private Integer storypoints;
	private Integer sprintId;
	private Integer projectId;
	private Set<AcceptanceCriterion> criteria = new HashSet<AcceptanceCriterion>();
	private Set<Task> tasks = new HashSet<Task>();
	private String status = "Not Started";

	public Story start(int sprintId) {
		if ("Not Started".equals(status) || "Planning".equals(status)) {
			status = "In Progress";
			this.sprintId = sprintId;
			return this;
		}
		throw new RuntimeException("StoryDetails has already been started.");
	}

	public Story reopen() {
		if ("Completed".equals(status)) {
			status = "Planning";
			return this;
		}
		throw new RuntimeException("StoryDetails has not been completed.");
	}

	Story updateStatus() {
		for (Task task : tasks) {
			if (!task.getStatus().equals("Completed")) {
				return this;
			}
		}
		status = "Completed";
		return this;
	}

	public Story addAcceptanceCriterion(AcceptanceCriterion acceptanceCriterion) {
		if (!"Completed".equals(status)) {
			acceptanceCriterion.setCriterionStory(this);
			criteria.add(acceptanceCriterion);
			return this;
		}
		throw new RuntimeException("StoryDetails has already been completed.");
	}

	public Story addTask(Task task) {
		task.setTaskStory(this);
		tasks.add(task);
		if ("Not Started".equals(status)) {
			status = "Planning";
		} else if ("Completed".equals(status)) {
			throw new RuntimeException("Story has already been completed.");
		}
		return this;
	}

	public Story update(Story newStory) {
		title = newStory.getTitle() != null ? newStory.getTitle() : title;
		description = newStory.getDescription() != null ? newStory.getDescription() : description;
		storypoints = newStory.getStorypoints() != null ? newStory.getStorypoints() : storypoints;
		return this;
	}

	@JsonIgnore
	public Set<Task> getTasks() {
		return tasks;
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

	public Story setStorypoints(Integer storypoints) {
		this.storypoints = storypoints;
		return this;
	}

	public Story setTitle(String title) {
		this.title = title;
		return this;
	}

	public Story setDescription(String description) {
		this.description = description;
		return this;
	}

	public int getId() {
		return id;
	}

	public Set<AcceptanceCriterion> getCriteria() {
		return criteria;
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

	public Story setProjectId(Integer projectId) {
		this.projectId = projectId;
		return this;
	}

	public Story setSprintId(Integer sprintId) {
		this.sprintId = sprintId;
		return this;
	}

	@JsonProperty
	private void setCriteria(Set<AcceptanceCriterion> criteria) {
		for (AcceptanceCriterion criterion : criteria) {
			addAcceptanceCriterion(criterion);
		}
	}

	@JsonProperty
	private void setTasks(Set<Task> tasks) {
		for (Task task : tasks) {
			addTask(task);
		}
	}

	public String toJSON() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static Story getAPI() {
		Story story = new Story();
		story.id = 1;
		story.description = "my story description";
		story.title = "my story";
		story.projectId = 1;
		story.sprintId = 1;
		story.status = "Not Started";
		story.storypoints = 13;
		story.criteria.add(AcceptanceCriterion.getAPI());
		return story;
	}

	public SimpleStory toSimple() {
		return new SimpleStory()
				.setId(id)
				.setDescription(description)
				.setProjectId(projectId)
				.setSprintId(sprintId)
				.setStatus(status)
				.setStorypoints(storypoints)
				.setTitle(title);
	}

}
