package com.mongodb.mongopush.events;

public class VerificationTaskCompleteEvent {

	private boolean verificationTaskComplete;

	public VerificationTaskCompleteEvent(boolean verificationTaskComplete) {
		this.verificationTaskComplete = verificationTaskComplete;
	}
	
	public boolean isVerificationTaskComplete() {
		return verificationTaskComplete;
	}
}
