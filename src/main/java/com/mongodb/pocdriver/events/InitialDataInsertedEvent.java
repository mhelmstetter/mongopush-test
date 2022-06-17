package com.mongodb.pocdriver.events;

public class InitialDataInsertedEvent {

	private boolean initialDataInserted;
	
	public boolean getInitialDataInserted() {
		return initialDataInserted;
	}
	
	public InitialDataInsertedEvent(boolean initialDataInserted) {
		this.initialDataInserted = initialDataInserted;
	}
}
