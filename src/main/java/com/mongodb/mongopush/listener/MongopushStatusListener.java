package com.mongodb.mongopush.listener;

import org.apache.commons.exec.ExecuteException;

import com.mongodb.mongopush.event.InitialSyncCompletedEvent;
import com.mongodb.mongopush.event.OplogStreamingCompletedEvent;

public interface MongopushStatusListener {
	
	public boolean isInitialSyncComplete();
	
	public void initialSyncCompleted(InitialSyncCompletedEvent initialSyncCompletedEvent);
	
	public void processFailed(ExecuteException e);
	
	public void oplogStreamingCompleted(OplogStreamingCompletedEvent oplogStreamingCompletedEvent);

	public boolean isOplogStreamingCompleted();
}
