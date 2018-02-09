package com.estafet.microservices.gateway.model;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Task {

	private Integer id;
	private String status = "Not Started";
	private Integer storyId;
	private Story taskStory;
	
	public Task update(Task newTask) {
		status = newTask.getStatus() != null ? newTask.getStatus() : status;
		if (status.equals("Completed")) {
			taskStory.updateStatus();
		}
		return this;
	}

	public Integer getId() {
		return id;
	}

	public Task setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public Story getTaskStory() {
		return taskStory;
	}

	Task setTaskStory(Story taskStory) {
		this.taskStory = taskStory;
		return this;
	}

	public Integer getStoryId() {
		return storyId;
	}

	public void setStoryId(Integer storyId) {
		this.storyId = storyId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public static Task fromJSON(String message) {
		try {
			return new ObjectMapper().readValue(message, Task.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
