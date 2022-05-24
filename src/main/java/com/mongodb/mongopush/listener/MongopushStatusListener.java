package com.mongodb.mongopush.listener;

import com.mongodb.mongopush.event.InitialSyncCompletedEvent;

public interface MongopushStatusListener {
	
	public boolean isInitialSyncComplete();
	

	public void initialSyncCompleted(InitialSyncCompletedEvent initialSyncCompletedEvent);


}
