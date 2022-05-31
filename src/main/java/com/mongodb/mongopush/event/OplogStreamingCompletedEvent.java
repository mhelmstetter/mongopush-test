package com.mongodb.mongopush.event;

public class OplogStreamingCompletedEvent {

	private boolean oplogStreamingCompleted;

	public OplogStreamingCompletedEvent(boolean oplogStreamingCompleted) {
		this.oplogStreamingCompleted = oplogStreamingCompleted;
	}
	
	public boolean isOplogStreamingCompleted() {
		return oplogStreamingCompleted;
	}
	
}
