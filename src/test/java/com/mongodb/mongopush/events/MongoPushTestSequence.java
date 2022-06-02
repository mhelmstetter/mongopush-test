package com.mongodb.mongopush.events;

import java.util.Arrays;
import java.util.List;

public enum MongoPushTestSequence {

	POC_DRIVER_MONGO_PUSH_DATA_SYNC(Arrays.asList(
			MongoPushTestEvent.EXECUTE_POC_DRIVER,
			MongoPushTestEvent.INITIAL_DATA_INSERTED,
			MongoPushTestEvent.SHUTDOWN_POC_DRIVER,
			MongoPushTestEvent.EXECUTE_MONGO_PUSH_MODE_DATA,
			MongoPushTestEvent.INITIAL_SYNC_COMPLETED,
			MongoPushTestEvent.SHUTDOWN_MONGO_PUSH
			)),
	
	POC_DRIVER_MONGO_PUSH_DATA_ONLY_SYNC(Arrays.asList(
			MongoPushTestEvent.EXECUTE_POC_DRIVER,
			MongoPushTestEvent.INITIAL_DATA_INSERTED,
			MongoPushTestEvent.SHUTDOWN_POC_DRIVER,
			MongoPushTestEvent.EXECUTE_MONGO_PUSH_MODE_DATA_ONLY,
			MongoPushTestEvent.INITIAL_SYNC_COMPLETED,
			MongoPushTestEvent.SHUTDOWN_MONGO_PUSH
			)),
	
	POC_DRIVER_MONGO_PUSH_DATA_ONLY_SYNC_VERIFY(Arrays.asList(
			MongoPushTestEvent.EXECUTE_POC_DRIVER,
			MongoPushTestEvent.INITIAL_DATA_INSERTED,
			MongoPushTestEvent.SHUTDOWN_POC_DRIVER,
			MongoPushTestEvent.EXECUTE_MONGO_PUSH_MODE_DATA_ONLY,
			MongoPushTestEvent.INITIAL_SYNC_COMPLETED,
			MongoPushTestEvent.SHUTDOWN_MONGO_PUSH,
			MongoPushTestEvent.EXECUTE_MONGO_PUSH_MODE_VERIFY,
			MongoPushTestEvent.VERIFICATION_TASK_COMPLETED,
			MongoPushTestEvent.SHUTDOWN_MONGO_PUSH
			)),
	
	POC_DRIVER_MONGO_PUSH_DATA_SYNC_OPLOG(Arrays.asList(
			MongoPushTestEvent.EXECUTE_POC_DRIVER,
			MongoPushTestEvent.INITIAL_DATA_INSERTED,
			MongoPushTestEvent.EXECUTE_MONGO_PUSH_MODE_DATA,
			MongoPushTestEvent.INITIAL_SYNC_COMPLETED,
			MongoPushTestEvent.SHUTDOWN_POC_DRIVER,
			MongoPushTestEvent.OPLOG_STREAMING_COMPLETED,
			MongoPushTestEvent.SHUTDOWN_MONGO_PUSH
			)),

	POC_DRIVER_MONGO_PUSH_DATA_ONLY_SYNC_VERIFY_FAILED(Arrays.asList(
			MongoPushTestEvent.EXECUTE_POC_DRIVER,
			MongoPushTestEvent.INITIAL_DATA_INSERTED,
			MongoPushTestEvent.EXECUTE_MONGO_PUSH_MODE_DATA_ONLY,
			MongoPushTestEvent.INITIAL_SYNC_COMPLETED,
			MongoPushTestEvent.SHUTDOWN_MONGO_PUSH,
			MongoPushTestEvent.SHUTDOWN_POC_DRIVER,
			MongoPushTestEvent.EXECUTE_MONGO_PUSH_MODE_VERIFY,
			MongoPushTestEvent.VERIFICATION_TASK_FAILED,
			MongoPushTestEvent.SHUTDOWN_MONGO_PUSH
			));
	
	private List<MongoPushTestEvent> mongoPushTestEvents;
	
	MongoPushTestSequence(List<MongoPushTestEvent> mongoPushTestEvents) {
		this.mongoPushTestEvents = mongoPushTestEvents;
	}

	public List<MongoPushTestEvent> getMongoPushTestEvents() {
		return mongoPushTestEvents;
	}
}
