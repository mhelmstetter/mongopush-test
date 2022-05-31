package com.mongodb.mongopush.event;

public class VerificationTaskFailedEvent {

	private boolean verificationTaskFailed;

	public VerificationTaskFailedEvent(boolean verificationTaskFailed) {
		this.verificationTaskFailed = verificationTaskFailed;
	}
	
	public boolean isVerificationTaskFailed() {
		return verificationTaskFailed;
	}
}
