package com.mongodb.mongopush.events;

public class RefetchTaskCompleteEvent {

	private boolean refetchTaskComplete;
	private String refetchTaskStr01;
	private String refetchTaskStr02;

	public RefetchTaskCompleteEvent(boolean refetchTaskComplete, String refetchTaskStr01, String refetchTaskStr02) {
		this.refetchTaskComplete = refetchTaskComplete;
		this.refetchTaskStr01 = refetchTaskStr01;
		this.refetchTaskStr02 = refetchTaskStr02;
	}
	
	public boolean isRefetchTaskComplete() {
		return refetchTaskComplete;
	}

	public String getRefetchTaskStr01() {
		return refetchTaskStr01;
	}

	public String getRefetchTaskStr02() {
		return refetchTaskStr02;
	}

}
