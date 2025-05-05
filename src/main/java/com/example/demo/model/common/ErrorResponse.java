package com.example.demo.model.common;

import java.util.Date;

public class ErrorResponse {
	
	private String errorMessage;
	private Date errorTimestamp = new Date();
	
	public ErrorResponse() {}
	
	public ErrorResponse(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Date getErrorTimestamp() {
		return errorTimestamp;
	}

	public void setErrorTimestamp(Date errorTimestamp) {
		this.errorTimestamp = errorTimestamp;
	}
	
}
