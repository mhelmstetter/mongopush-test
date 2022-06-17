package com.mongodb.mongopush.listener;

import org.apache.commons.exec.ExecuteException;

import com.mongodb.mongopush.events.InitialSyncCompletedEvent;
import com.mongodb.mongopush.events.OplogStreamingCompletedEvent;
import com.mongodb.mongopush.events.RefetchTaskCompleteEvent;
import com.mongodb.mongopush.events.VerificationTaskCompleteEvent;
import com.mongodb.mongopush.events.VerificationTaskFailedEvent;

public interface MongopushStatusListener {
	
	public boolean isInitialSyncComplete();
	
	public void initialSyncCompleted(InitialSyncCompletedEvent initialSyncCompletedEvent);
	
	public void processFailed(ExecuteException e);
	
	public void oplogStreamingCompleted(OplogStreamingCompletedEvent oplogStreamingCompletedEvent);

	public boolean isOplogStreamingCompleted();
	
	public void verificationTaskComplete(VerificationTaskCompleteEvent verificationTaskCompleteEvent);

	public boolean isVerificationTaskComplete();
	
	public void verificationTaskFailed(VerificationTaskFailedEvent verificationTaskFailedEvent);

	public boolean isVerificationTaskFailed();
	
	public void refetchTaskComplete(RefetchTaskCompleteEvent refetchTaskCompleteEvent);

	public boolean isRefetchTaskComplete();
}
