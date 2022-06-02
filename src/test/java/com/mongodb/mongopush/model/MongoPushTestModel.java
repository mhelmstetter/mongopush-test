package com.mongodb.mongopush.model;

import java.util.List;

import com.mongodb.mongopush.MongopushOptions.IncludeOption;
import com.mongodb.mongopush.events.MongoPushTestEvent;

public class MongoPushTestModel {

	List<MongoPushTestEvent> mongoPushTestEvents;
	IncludeOption[] includeOptions;
	
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
	
}
