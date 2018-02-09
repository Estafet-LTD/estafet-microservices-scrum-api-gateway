package com.estafet.microservices.gateway.model;

public class AcceptanceCriterion {

	private int id;
	private Story criterionStory;

	private String description;

	public int getId() {
		return id;
	}

	public Story getCriterionStory() {
		return criterionStory;
	}

	public String getDescription() {
		return description;
	}

	public void setId(int id) {
		this.id = id;
	}

	void setCriterionStory(Story criterionStory) {
		this.criterionStory = criterionStory;
	}

	public AcceptanceCriterion setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		AcceptanceCriterion other = (AcceptanceCriterion) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public static AcceptanceCriterion getAPI() {
		AcceptanceCriterion criterion = new AcceptanceCriterion();
		criterion.id = 1;
		criterion.description = "my criterion";
		return criterion;
	}

}
