package com.mongodb.mongopush;

import static com.mongodb.mongopush.constants.MongoPushConstants.COMMA;
import static com.mongodb.mongopush.constants.MongoPushConstants.SLASH_DOT;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
import com.mongodb.mongopush.events.RefetchTaskCompleteEvent;
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

	private String[] mongopushTestSuitesNames;

	@BeforeEach
	public void beforeEach() {
		pocDriverConfiguration.setPocDriverMongodbConnectionString(mongoPushConfiguration.getMongopushSource());
		sourceTestClient.setConnectionString(mongoPushConfiguration.getMongopushSource());
		targetTestClient.setConnectionString(mongoPushConfiguration.getMongopushTarget());

		sourceTestClient.dropAllDatabasesByName();
		targetTestClient.dropAllDatabasesByName();
	}

	protected boolean isTestSuiteToRun(String testFileName)
	{
		mongopushTestSuitesNames = mongoPushConfiguration.getMongopushTestSuiteNames().split(COMMA);
		boolean testToRun = false;
		for(String testSuiteName : mongopushTestSuitesNames)
		{
			if(testFileName.toLowerCase().contains(testSuiteName))
			{
				testToRun = true;
				break;
			}
		}
		return testToRun;
	}

	protected void processTestEventsSequence(MongoPushTestEvent mongoPushTestEvent, MongoPushTestModel mongoPushTestModel) throws ExecuteException, IOException, InterruptedException
	{
		logger.info("Processing event started - {}", mongoPushTestEvent.getName());
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
				pocDriverRunner.initialDataInserted(new InitialDataInsertedEvent(false));
				pocDriverRunner.documentsInsertedCount(new DocumentsInsertedCountEvent(0));
				break;
			case SHUTDOWN_POC_DRIVER:
				pocDriverRunner.shutdown();
				break;
			case POPULATE_DATA_ONE_DATABASE_NAME:
				if(mongoPushTestModel.getPopulateDataArguments() != null)
				{	String[] populateDataArguments = mongoPushTestModel.getPopulateDataArguments().split(COMMA);
					sourceTestClient.populateDataForDatabase(populateDataArguments[0], populateDataArguments[1], Integer.valueOf(populateDataArguments[2]), false);
				}
				break;
			case POPULATE_DATA_MULTIPLE_DATABASE:
				if(mongoPushTestModel.getPopulateDataArguments() != null)
				{	String[] populateDataArguments = mongoPushTestModel.getPopulateDataArguments().split(COMMA);
					sourceTestClient.populateData(Integer.valueOf(populateDataArguments[0]), Integer.valueOf(populateDataArguments[1]), Integer.valueOf(populateDataArguments[2]), false);
				}
				break;
			case DATA_TYPE_OPERATIONS:
				if(mongoPushTestModel.getReplaceDataArguments() != null)
				{
					String[] dbCollStr = mongoPushTestModel.getReplaceDataArguments().getNamespace().split(SLASH_DOT);
					long replacedDocumentCount = sourceTestClient.replaceDocuments(dbCollStr[0], dbCollStr[1], mongoPushTestModel.getReplaceDataArguments().getFilter());
					logger.info("Number of documents replcaed - {}", replacedDocumentCount);
				}
				if(mongoPushTestModel.getIdAsDocumentArguments() != null)
				{
					String[] idAsDocumentArgumentsArray = mongoPushTestModel.getIdAsDocumentArguments().split(COMMA);
					sourceTestClient.populateDataForDatabase(idAsDocumentArgumentsArray[0], idAsDocumentArgumentsArray[1], Integer.valueOf(idAsDocumentArgumentsArray[2]), true);

				}
				if(mongoPushTestModel.getUniqueIndexArguments() != null)
				{
					String[] uniqueIndexArguments = mongoPushTestModel.getUniqueIndexArguments().split(COMMA);
					sourceTestClient.populateData(Integer.valueOf(uniqueIndexArguments[0]), Integer.valueOf(uniqueIndexArguments[1]), Integer.valueOf(uniqueIndexArguments[2]), Boolean.valueOf(uniqueIndexArguments[3]));
				}
				break;
			case EXECUTE_OTHER_MIGRATION_TOOL:
				mongoPushOptionsBuilder = MongopushOptions.builder().mode(MongopushMode.START);
				options = mongoPushOptionsBuilder.build();
				mongopushRunner.execute(options);
				Thread.sleep(5000);
				break;
			case EXECUTE_MONGO_PUSH_MODE_DATA:
				mongoPushOptionsBuilder = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA);
				if(mongoPushTestModel.getIncludeOptions() != null)
				{
					mongoPushOptionsBuilder = mongoPushOptionsBuilder.includeNamespace(mongoPushTestModel.getIncludeOptions());
				}
				options = mongoPushOptionsBuilder.build();
				mongopushRunner.execute(options);
				Thread.sleep(5000);
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
			case EXECUTE_OTHER_MIGRATION_TOOL_COMPARE:
				mongoPushOptionsBuilder = MongopushOptions.builder().mode(MongopushMode.COMPARE);
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
				mongopushRunner.initialSyncCompleted(new InitialSyncCompletedEvent(null, false));
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
				mongopushRunner.oplogStreamingCompleted(new OplogStreamingCompletedEvent(false));
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
				mongopushRunner.verificationTaskComplete(new VerificationTaskCompleteEvent(false));
				break;
			case FINAL_VERIFICATION_TASK_COMPLETED:
				while (true) {
					Thread.sleep(5000);
					if(mongopushRunner.isVerificationTaskFailed())
					{
						assertFalse(mongopushRunner.isVerificationTaskFailed());
						break;
					}
					else if(mongopushRunner.isVerificationTaskComplete())
					{
						assertTrue(mongopushRunner.isVerificationTaskComplete());
						break;
					}
				}
				mongopushRunner.verificationTaskFailed(new VerificationTaskFailedEvent(false));
				mongopushRunner.verificationTaskComplete(new VerificationTaskCompleteEvent(false));
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
				mongopushRunner.verificationTaskFailed(new VerificationTaskFailedEvent(false));
				break;
			case REFETCH_TASK_COMPLETED:
				while (true) {
					Thread.sleep(5000);
					if(mongopushRunner.isRefetchTaskComplete())
					{
						assertTrue(mongopushRunner.isRefetchTaskComplete());
						break;
					}
				}
				mongopushRunner.refetchTaskComplete(new RefetchTaskCompleteEvent(false));
				break;
			case SHUTDOWN_MONGO_PUSH:
				mongopushRunner.shutdown();
				Thread.sleep(5000);
				break;
			case RESUME_MONGO_PUSH:
				Thread.sleep(20000);
				mongoPushOptionsBuilder = MongopushOptions.builder().mode(MongopushMode.RESUME);
				if(mongoPushTestModel.getIncludeOptions() != null)
				{
					mongoPushOptionsBuilder = mongoPushOptionsBuilder.includeNamespace(mongoPushTestModel.getIncludeOptions());
				}
				options = mongoPushOptionsBuilder.build();
				mongopushRunner.execute(options);
				break;
			case EXECUTE_DIFF_UTIL:
				DiffSummary ds = diffUtilRunner.diff();
				assertDiffResults(ds);
			default:
				break;
		}
		logger.info("Processing event completed - {}", mongoPushTestEvent.getName());
	}

	private static void assertDiffResults(DiffSummary ds) {
		assertEquals(0, ds.missingDbs);
		assertEquals(0, ds.totalMissingDocs);
		assertEquals(0, ds.totalKeysMisordered);
		assertEquals(0, ds.totalHashMismatched);
	}
}
