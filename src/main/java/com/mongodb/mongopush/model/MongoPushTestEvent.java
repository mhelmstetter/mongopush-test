package com.mongodb.mongopush.model;

public enum MongoPushTestEvent {

	EXECUTE_POC_DRIVER("ExecutePOCDriver"),
	INITIAL_DATA_INSERTED("InitialDataInserted"),
	SHUTDOWN_POC_DRIVER("ShutDownPOCDriver"),
	EXECUTE_MONGO_PUSH_MODE_DATA("ExecuteMongoPushModeData"),
	EXECUTE_MONGO_PUSH_MODE_DATA_ONLY("ExecuteMongoPushModeDataOnly"),
	EXECUTE_MONGO_PUSH_MODE_VERIFY("ExecuteMongoPushModeVerify"),
	EXECUTE_MONGO_PUSH_MODE_REFETCH("ExecuteMongoPushModeRefetch"),
	INITIAL_SYNC_COMPLETED("InitialSyncCompleted"),
	OPLOG_STREAMING_COMPLETED("OplogStreamingCompleted"),
	VERIFICATION_TASK_COMPLETED("VerificationTaskCompleted"),
	VERIFICATION_TASK_FAILED("VerificationTaskFailed"),
	SHUTDOWN_MONGO_PUSH("ShutDownMongoPush"),
	EXECUTE_DIFF_UTIL("ExecuteDiffUtil");
	
	private String name;
	
	MongoPushTestEvent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}