package com.estafet.microservices.gateway.model;

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

}
