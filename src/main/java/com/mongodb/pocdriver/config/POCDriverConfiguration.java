package com.mongodb.pocdriver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class POCDriverConfiguration {

	@Value("${pocdriver.default}")
	private boolean pocDriverDefault;
	
	@Value("${pocdriver.binary.path}")
	private String pocDriverBinaryPath;
	
	@Value("${pocdriver.initial.documentcount}")
	private long initialDocumentCount;
	
	@Value("${pocdriver.commandline.arguments}")
	private String pocDriverCommandlineArguments;
	
	@Value("${pocdriver.mongodb.connectionstring:mongodb://localhost:27017}")
	private String pocDriverMongodbConnectionString;

	public boolean isPocDriverDefault() {
		return pocDriverDefault;
	}

	public void setPocDriverDefault(boolean pocDriverDefault) {
		this.pocDriverDefault = pocDriverDefault;
	}

	public String getPocDriverBinaryPath() {
		return pocDriverBinaryPath;
	}

	public void setPocDriverBinaryPath(String pocDriverBinaryPath) {
		this.pocDriverBinaryPath = pocDriverBinaryPath;
	}

	public long getInitialDocumentCount() {
		return initialDocumentCount;
	}

	public void setInitialDocumentCount(long initialDocumentCount) {
		this.initialDocumentCount = initialDocumentCount;
	}

	public String getPocDriverCommandlineArguments() {
		return pocDriverCommandlineArguments;
	}

	public void setPocDriverCommandlineArguments(String pocDriverCommandlineArguments) {
		this.pocDriverCommandlineArguments = pocDriverCommandlineArguments;
	}

	public String getPocDriverMongodbConnectionString() {
		return pocDriverMongodbConnectionString;
	}

	public void setPocDriverMongodbConnectionString(String pocDriverMongodbConnectionString) {
		this.pocDriverMongodbConnectionString = pocDriverMongodbConnectionString;
	}
	
}
