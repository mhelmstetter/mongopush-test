package com.mongodb.mongopush.event;

public class InitialSyncCompletedEvent {
	
	private String initialSyncDuration;
	private boolean initialSyncCompleted;
	
	public String getInitialSyncDuration() {
		return initialSyncDuration;
	}
	
	public boolean getInitialSyncCompleted() {
		return initialSyncCompleted;
	}

	public InitialSyncCompletedEvent(String initialSyncDuration, boolean initialSyncCompleted) {
		this.initialSyncDuration = initialSyncDuration;
		this.initialSyncCompleted = initialSyncCompleted;
	}

}
