package com.m0schy.domain;

import java.util.Date;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class HttpResponse {

	
	public HttpResponse(int httpStatusCode, HttpStatus status, String reason, String message) {
		super();
		this.httpStatusCode = httpStatusCode;
		this.status = status;
		this.reason = reason;
		this.message = message;
		this.timeStamp = new Date();
	}
	public HttpResponse(int httpStatusCode, HttpStatus status, String reason) {
		this.httpStatusCode = httpStatusCode;
		this.status = status;
		this.reason = reason;
		this.timeStamp = new Date();
	}
	@JsonFormat(shape= JsonFormat.Shape.STRING, pattern="dd-MM-yyyy hh:mm:ss", timezone = "Europe/Paris")
	private Date timeStamp;
	private int httpStatusCode;
	private HttpStatus status;
	private String reason;
	private String message;
	
	
}
