package com.mongodb.pocdriver.events;

public class DocumentsInsertedCountEvent {

	private long documentsCount;
	
	public long getDocumentCount() {
		return documentsCount;
	}

	public DocumentsInsertedCountEvent(long documentsCount) {
		this.documentsCount = documentsCount;
	}
}
