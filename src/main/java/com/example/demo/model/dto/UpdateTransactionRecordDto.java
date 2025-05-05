package com.example.demo.model.dto;

import jakarta.validation.constraints.NotEmpty;

public class UpdateTransactionRecordDto {

	@NotEmpty
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
