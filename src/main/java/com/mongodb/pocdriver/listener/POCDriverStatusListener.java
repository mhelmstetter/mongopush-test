package com.mongodb.pocdriver.listener;

import com.mongodb.pocdriver.events.DocumentsInsertedCountEvent;
import com.mongodb.pocdriver.events.InitialDataInsertedEvent;

public interface POCDriverStatusListener {

	public boolean isInitialDataInserted();
	
	public void initialDataInserted(InitialDataInsertedEvent initialDataInsertedEvent);
	
	public long getDocumentsInsertedCount();
	
	public void documentsInsertedCount(DocumentsInsertedCountEvent documentsInsertedCountEvent);
}
