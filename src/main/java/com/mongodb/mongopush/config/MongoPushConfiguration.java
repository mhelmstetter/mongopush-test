package com.mongodb.mongopush.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoPushConfiguration {

	@Value("${mongopush.source:mongodb://localhost:27017}")
	private String mongopushSource;
	
	@Value("${mongopush.target:mongodb://localhost:27017}")
	private String mongopushTarget;
	
	@Value("${mongopush.binary.path}")
	private String mongopushBinaryPath;

	public String getMongopushSource() {
		return mongopushSource;
	}

	public void setMongopushSource(String mongopushSource) {
		this.mongopushSource = mongopushSource;
	}

	public String getMongopushTarget() {
		return mongopushTarget;
	}

	public void setMongopushTarget(String mongopushTarget) {
		this.mongopushTarget = mongopushTarget;
	}

	public String getMongopushBinaryPath() {
		return mongopushBinaryPath;
	}

	public void setMongopushBinaryPath(String mongopushBinaryPath) {
		this.mongopushBinaryPath = mongopushBinaryPath;
	}
	
}