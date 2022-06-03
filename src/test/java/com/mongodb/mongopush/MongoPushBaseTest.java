package com.mongodb.mongopush;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.mongodb.diffutil.DiffSummary;
import com.mongodb.diffutil.DiffUtilRunner;
import com.mongodb.mongopush.MongopushOptions.Builder;
import com.mongodb.mongopush.config.MongoPushConfiguration;
import com.mongodb.mongopush.events.InitialSyncCompletedEvent;
import com.mongodb.mongopush.events.OplogStreamingCompletedEvent;
import com.mongodb.mongopush.events.VerificationTaskCompleteEvent;
import com.mongodb.mongopush.events.VerificationTaskFailedEvent;
import com.mongodb.mongopush.model.MongoPushTestEvent;
import com.mongodb.mongopush.model.MongoPushTestModel;
import com.mongodb.mongopush.utility.MongoTestClient;
import com.mongodb.pocdriver.POCDriverRunner;
import com.mongodb.pocdriver.config.POCDriverConfiguration;
import com.mongodb.pocdriver.events.DocumentsInsertedCountEvent;
import com.mongodb.pocdriver.events.InitialDataInsertedEvent;

@ExtendWith({SpringExtension.class})
@TestPropertySource("/test.properties")
@ContextConfiguration(locations = "/test-context.xml")
@SpringJUnitConfig
@TestMethodOrder(OrderAnnotation.class)
public class MongoPushBaseTest {

	private static Logger logger = LoggerFactory.getLogger(MongoPushBaseTest.class);
	
	@Autowired
	protected POCDriverRunner pocDriverRunner;
	
	@Autowired
	protected MongopushRunner mongopushRunner;
	
	@Autowired
	protected DiffUtilRunner diffUtilRunner;
	
	@Autowired
	protected POCDriverConfiguration pocDriverConfiguration;
	
	@Autowired
	MongoPushConfiguration mongoPushConfiguration;
	
	@Autowired
	MongoTestClient sourceTestClient;
	
	@Autowired
	MongoTestClient targetTestClient;
	
	@BeforeEach
	public void beforeEach() {
		pocDriverConfiguration.setPocDriverMongodbConnectionString(sourceTestClient.getConnectionString().getConnectionString());
		mongoPushConfiguration.setMongopushSource(sourceTestClient.getConnectionString().getConnectionString());
		mongoPushConfiguration.setMongopushTarget(targetTestClient.getConnectionString().getConnectionString());
		sourceTestClient.dropAllDatabasesByName();
		targetTestClient.dropAllDatabasesByName();
		pocDriverRunner.initialDataInserted(new InitialDataInsertedEvent(false));
		pocDriverRunner.documentsInsertedCount(new DocumentsInsertedCountEvent(0));
		mongopushRunner.initialSyncCompleted(new InitialSyncCompletedEvent(null, false));
		mongopushRunner.oplogStreamingCompleted(new OplogStreamingCompletedEvent(false));
		mongopushRunner.verificationTaskComplete(new VerificationTaskCompleteEvent(false));
		mongopushRunner.verificationTaskFailed(new VerificationTaskFailedEvent(false));
	}
	
	protected void processTestEventsSequence(MongoPushTestEvent mongoPushTestEvent, MongoPushTestModel mongoPushTestModel) throws ExecuteException, IOException, InterruptedException
	{
		logger.info("Processing event - {}", mongoPushTestEvent.getName());
		MongopushOptions options;
		Builder mongoPushOptionsBuilder;
		switch (mongoPushTestEvent) {
			case EXECUTE_POC_DRIVER:
				if(mongoPushTestModel.getPocdriveArguments() != null)
				{
					pocDriverConfiguration.setPocDriverCommandlineArguments(mongoPushTestModel.getPocdriveArguments());
				}
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
				mongoPushOptionsBuilder = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA);
				if(mongoPushTestModel.getIncludeOptions() != null)
				{
					mongoPushOptionsBuilder = mongoPushOptionsBuilder.includeNamespace(mongoPushTestModel.getIncludeOptions());
				}
				options = mongoPushOptionsBuilder.build();
				mongopushRunner.execute(options);
				break;
			case EXECUTE_MONGO_PUSH_MODE_DATA_ONLY:
				mongoPushOptionsBuilder = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA_ONLY);
				if(mongoPushTestModel.getIncludeOptions() != null)
				{
					mongoPushOptionsBuilder = mongoPushOptionsBuilder.includeNamespace(mongoPushTestModel.getIncludeOptions());
				}
				options = mongoPushOptionsBuilder.build();
				mongopushRunner.execute(options);
				break;
			case EXECUTE_MONGO_PUSH_MODE_VERIFY:
				mongoPushOptionsBuilder = MongopushOptions.builder().mode(MongopushMode.VERIFY);
				if(mongoPushTestModel.getIncludeOptions() != null)
				{
					mongoPushOptionsBuilder = mongoPushOptionsBuilder.includeNamespace(mongoPushTestModel.getIncludeOptions());
				}
				options = mongoPushOptionsBuilder.build();
				mongopushRunner.execute(options);
				break;
			case EXECUTE_MONGO_PUSH_MODE_REFETCH:
				mongoPushOptionsBuilder = MongopushOptions.builder().mode(MongopushMode.REFETCH);
				options = mongoPushOptionsBuilder.build();
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
			case EXECUTE_DIFF_UTIL:
				DiffSummary ds = diffUtilRunner.diff();
				assertDiffResults(ds);
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
