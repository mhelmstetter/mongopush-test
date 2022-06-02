package com.mongodb.mongopush.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.exec.ExecuteException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.diffutil.DiffSummary;
import com.mongodb.mongopush.MongoPushBaseTest;
import com.mongodb.mongopush.MongopushMode;
import com.mongodb.mongopush.MongopushOptions;
import com.mongodb.mongopush.events.MongoPushTestEvent;
import com.mongodb.mongopush.utility.MongoPushTestUtility;

public class MongoPushPushDataTest extends MongoPushBaseTest {

	private static Logger logger = LoggerFactory.getLogger(MongoPushPushDataTest.class);

	@Autowired
	MongoPushTestUtility mongoPushTestUtility;
	
	private static String pushDataTestFilePath = "mongopush/pushDataTests";
	
	private static Stream<String> streamStringParameters() {
		List<String> testSequenceList = MongoPushTestUtility.fileReader(pushDataTestFilePath);
		return testSequenceList.stream();
	}
	
	@Order(1)
	@ParameterizedTest
	@MethodSource("streamStringParameters")
	void mongoPushBasicPushDataTest(String testSequence) throws ExecuteException, IOException, InterruptedException {
		
		logger.info("Test Sequence - {}", testSequence);
		List<MongoPushTestEvent> testSequenceEventsList = mongoPushTestUtility.parseTestSequenceString(testSequence);
		if(testSequenceEventsList == null)
		{
			logger.info("Error in defining test sequence, Please check the test events");
		}
		else {
			for(MongoPushTestEvent mongoPushTestEvent: testSequenceEventsList)
			{
				processTestEventsSequence(mongoPushTestEvent);
			}
		}
		
		DiffSummary ds = diffUtilRunner.diff();
		assertDiffResults(ds);
	}
	
	private void processTestEventsSequence(MongoPushTestEvent mongoPushTestEvent) throws ExecuteException, IOException, InterruptedException
	{
		logger.info("Processing event - {}", mongoPushTestEvent.getName());
		MongopushOptions options;
		switch (mongoPushTestEvent) {
			case EXECUTE_POC_DRIVER:
				pocDriverRunner.execute();
				break;
			case INITIAL_DATA_INSERTED:
				while (true) {
					Thread.sleep(5000);
					if (pocDriverRunner.isInitialDataInserted()) {
						logger.info("Documents inserted - {}", pocDriverRunner.getDocumentsInsertedCount());
						assertTrue(pocDriverRunner.getDocumentsInsertedCount() > pocDriverConfiguration.getInitialDocumentCount());
						break;
					}
				}
				break;
			case SHUTDOWN_POC_DRIVER:
				pocDriverRunner.shutdown();
				break;
			case EXECUTE_MONGO_PUSH_MODE_DATA:
				options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).build();
				mongopushRunner.execute(options);
				break;
			case EXECUTE_MONGO_PUSH_MODE_DATA_ONLY:
				options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA_ONLY).build();
				mongopushRunner.execute(options);
				break;
			case EXECUTE_MONGO_PUSH_MODE_VERIFY:
				options = MongopushOptions.builder().mode(MongopushMode.VERIFY).build();
				mongopushRunner.execute(options);
				break;
			case INITIAL_SYNC_COMPLETED:
				while (true) {
					Thread.sleep(5000);
					if(mongopushRunner.isInitialSyncComplete())
					{
						assertTrue(mongopushRunner.isInitialSyncComplete());
						break;
					}
				}
				break;
			case OPLOG_STREAMING_COMPLETED:
				while (true) {
					Thread.sleep(5000);
					if (mongopushRunner.isOplogStreamingCompleted()) {
						assertTrue(mongopushRunner.isOplogStreamingCompleted());
						Thread.sleep(15000);
						break;
					}
				}
				break;
			case VERIFICATION_TASK_COMPLETED:
				while (true) {
					Thread.sleep(5000);
					if(mongopushRunner.isVerificationTaskComplete())
					{
						assertTrue(mongopushRunner.isVerificationTaskComplete());
						break;
					}
				}
				break;
			case VERIFICATION_TASK_FAILED:
				while (true) {
					Thread.sleep(5000);
					if(mongopushRunner.isVerificationTaskFailed())
					{
						assertTrue(mongopushRunner.isVerificationTaskFailed());
						break;
					}
				}
				break;
			case SHUTDOWN_MONGO_PUSH:
				mongopushRunner.shutdown();
				break;
			default:
				break;
		}
	}
	
	private static void assertDiffResults(DiffSummary ds) {
		assertEquals(0, ds.missingDbs);
		assertEquals(0, ds.totalMissingDocs);
		assertEquals(0, ds.totalKeysMisordered);
		assertEquals(0, ds.totalHashMismatched);
	}
}
