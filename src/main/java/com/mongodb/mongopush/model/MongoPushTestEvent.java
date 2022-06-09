package com.mongodb.mongopush.model;

public enum MongoPushTestEvent {

	EXECUTE_POC_DRIVER("ExecutePOCDriver"),
	INITIAL_DATA_INSERTED("InitialDataInserted"),
	SHUTDOWN_POC_DRIVER("ShutDownPOCDriver"),
	POPULATE_DATA_ONE_DATABASE_NAME("PopulateDataOneDatabaseName"),
	POPULATE_DATA_MULTIPLE_DATABASE("PopulateDataMultipleDatabase"),
	EXECUTE_MONGO_PUSH_MODE_DATA("ExecuteMongoPushModeData"),
	EXECUTE_MONGO_PUSH_MODE_DATA_ONLY("ExecuteMongoPushModeDataOnly"),
	EXECUTE_MONGO_PUSH_MODE_VERIFY("ExecuteMongoPushModeVerify"),
	EXECUTE_MONGO_PUSH_MODE_REFETCH("ExecuteMongoPushModeRefetch"),
	INITIAL_SYNC_COMPLETED("InitialSyncCompleted"),
	OPLOG_STREAMING_COMPLETED("OplogStreamingCompleted"),
	VERIFICATION_TASK_COMPLETED("VerificationTaskCompleted"),
	REFETCH_TASK_COMPLETED("RefetchTaskCompleted"),
	VERIFICATION_TASK_FAILED("VerificationTaskFailed"),
	SHUTDOWN_MONGO_PUSH("ShutDownMongoPush"),
	RESUME_MONGO_PUSH("ResumeMongoPush"),
	EXECUTE_DIFF_UTIL("ExecuteDiffUtil");
	
	private String name;
	
	MongoPushTestEvent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
