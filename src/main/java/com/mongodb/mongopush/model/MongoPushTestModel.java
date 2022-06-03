package com.mongodb.mongopush.model;

import java.util.List;

import com.mongodb.mongopush.MongopushOptions.IncludeOption;

public class MongoPushTestModel {

	private List<MongoPushTestEvent> mongoPushTestEvents;
	private IncludeOption[] includeOptions;
	private String pocdriveArguments;
	
	public List<MongoPushTestEvent> getMongoPushTestEvents() {
		return mongoPushTestEvents;
	}
	public void setMongoPushTestEvents(List<MongoPushTestEvent> mongoPushTestEvents) {
		this.mongoPushTestEvents = mongoPushTestEvents;
	}
	public IncludeOption[] getIncludeOptions() {
		return includeOptions;
	}
	public void setIncludeOptions(IncludeOption[] includeOptions) {
		this.includeOptions = includeOptions;
	}
	public String getPocdriveArguments() {
		return pocdriveArguments;
	}
	public void setPocdriveArguments(String pocdriveArguments) {
		this.pocdriveArguments = pocdriveArguments;
	}
	
}
