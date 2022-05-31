package com.mongodb.mongopush;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.mongodb.mongopush.config.MongoPushConfiguration;
import com.mongodb.mongopush.event.InitialSyncCompletedEvent;
import com.mongodb.mongopush.event.OplogStreamingCompletedEvent;
import com.mongodb.pocdriver.POCDriverRunner;
import com.mongodb.pocdriver.config.POCDriverConfiguration;
import com.mongodb.pocdriver.events.DocumentsInsertedCountEvent;
import com.mongodb.pocdriver.events.InitialDataInsertedEvent;
import com.mongodb.test.MongoTestClient;

@ExtendWith({SpringExtension.class})
@TestPropertySource("/pocdrivertest.properties")
@ContextConfiguration(locations = "/pocdriver-test-context.xml")
@SpringJUnitConfig
@TestMethodOrder(OrderAnnotation.class)
public class MongoPushTest01 {

	private static Logger logger = LoggerFactory.getLogger(MongoPushTest01.class);
	
	@Autowired
	POCDriverRunner pocDriverRunner;
	
	@Autowired
	MongopushRunner mongopushRunner;
	
//	@Autowired
//	DiffUtilRunner diffUtilRunner;
	
	@Autowired
	POCDriverConfiguration pocDriverConfiguration;
	
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
		sourceTestClient.dropDatabase("POCDB");
		targetTestClient.dropDatabase("POCDB");
		targetTestClient.dropDatabase("_mongopush");
		pocDriverRunner.initialDataInserted(new InitialDataInsertedEvent(false));
		pocDriverRunner.documentsInsertedCount(new DocumentsInsertedCountEvent(0));
		mongopushRunner.initialSyncCompleted(new InitialSyncCompletedEvent(null, false));
		mongopushRunner.oplogStreamingCompleted(new OplogStreamingCompletedEvent(false));
	}
	
	@Test
	@Order(1)
	void mongoPushBasicTest() throws ExecuteException, IOException, InterruptedException {
		
		logger.info("Running mongopush basic test");
		pocDriverRunner.execute();
		
		while (true) {
			Thread.sleep(5000);
			if (pocDriverRunner.isInitialDataInserted()) {
				logger.info("Documents inserted - {}", pocDriverRunner.getDocumentsInsertedCount());
				assertTrue(pocDriverRunner.getDocumentsInsertedCount() > pocDriverConfiguration.getInitialDocumentCount());
				pocDriverRunner.shutdown();
				MongopushOptions options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).build();
				mongopushRunner.execute(options);
				break;
			}
		}
		
		while (true) {
			Thread.sleep(5000);
			if(mongopushRunner.isInitialSyncComplete())
			{
				assertTrue(mongopushRunner.isInitialSyncComplete());
				mongopushRunner.shutdown();
				break;
			}
		}
		
//		DiffSummary ds = diffUtilRunner.diff();
//		assertDiffResults(ds);
	}
	
	@Test
	@Order(2)
	void mongoPushLagZeroTest() throws ExecuteException, IOException, InterruptedException {
		
		logger.info("Running mongopush lag 0 test");
		
		pocDriverRunner.execute();
		
		while (true) {
			Thread.sleep(5000);
			if (pocDriverRunner.isInitialDataInserted()) {
				logger.info("Documents inserted - {}", pocDriverRunner.getDocumentsInsertedCount());
				assertTrue(pocDriverRunner.getDocumentsInsertedCount() > pocDriverConfiguration.getInitialDocumentCount());
				MongopushOptions options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).build();
				mongopushRunner.execute(options);
				break;
			}
		}
		
		while (true) {
			Thread.sleep(5000);
			if(mongopushRunner.isInitialSyncComplete())
			{
				pocDriverRunner.shutdown();
				break;
			}
		}
		
		while (true) {
			Thread.sleep(5000);
			if (mongopushRunner.isOplogStreamingCompleted()) {
				assertTrue(mongopushRunner.isOplogStreamingCompleted());
				Thread.sleep(10000);
				mongopushRunner.shutdown();
				break;
			}
		}
	}
	
//	private static void assertDiffResults(DiffSummary ds) {
//		assertEquals(0, ds.missingDbs);
//		assertEquals(0, ds.totalMissingDocs);
//		assertEquals(0, ds.totalKeysMisordered);
//		assertEquals(0, ds.totalHashMismatched);
//	}
}
