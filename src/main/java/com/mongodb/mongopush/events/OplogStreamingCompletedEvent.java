package com.mongodb.mongopush.events;

public class OplogStreamingCompletedEvent {

	private boolean oplogStreamingCompleted;

	public OplogStreamingCompletedEvent(boolean oplogStreamingCompleted) {
		this.oplogStreamingCompleted = oplogStreamingCompleted;
	}
	
	public boolean isOplogStreamingCompleted() {
		return oplogStreamingCompleted;
	}
	
}
