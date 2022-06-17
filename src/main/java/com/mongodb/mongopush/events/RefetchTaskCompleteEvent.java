package com.mongodb.mongopush.events;

public class RefetchTaskCompleteEvent {

	private boolean refetchTaskComplete;

	public RefetchTaskCompleteEvent(boolean refetchTaskComplete) {
		this.refetchTaskComplete = refetchTaskComplete;
	}
	
	public boolean isRefetchTaskComplete() {
		return refetchTaskComplete;
	}

}
