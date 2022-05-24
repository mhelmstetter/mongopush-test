package com.mongodb.mongopush.event;

public class InitialSyncCompletedEvent {
	
	private String initialSyncDuration;
	
	
	public String getInitialSyncDuration() {
		return initialSyncDuration;
	}


	public InitialSyncCompletedEvent(String initialSyncDuration) {
		this.initialSyncDuration = initialSyncDuration;
	}

}
