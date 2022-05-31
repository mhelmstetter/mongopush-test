package com.mongodb.pocdriver;

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

import com.mongodb.pocdriver.config.POCDriverConfiguration;
import com.mongodb.test.MongoTestClient;

@ExtendWith({SpringExtension.class})
@TestPropertySource("/pocdrivertest.properties")
@ContextConfiguration(locations = "/pocdriver-test-context.xml")
@SpringJUnitConfig
@TestMethodOrder(OrderAnnotation.class)
public class POCDriverTest {

	private static Logger logger = LoggerFactory.getLogger(POCDriverTest.class);
	
	@Autowired
	POCDriverRunner pocDriverRunner;
	
	@Autowired
	POCDriverConfiguration pocDriverConfiguration;
	
	@Autowired
	MongoTestClient sourceTestClient;
	
	@BeforeEach
	public void beforeEach() {
		sourceTestClient.dropAllDatabases();
	}
	
	@Test
	@Order(1)
	void initialDataInsertedTest() throws ExecuteException, IOException, InterruptedException {
		
		logger.info("Running test for default document count - {}", pocDriverConfiguration.getInitialDocumentCount());
		pocDriverRunner.execute();
		while (true) {
			Thread.sleep(5000);
			if (pocDriverRunner.isInitialDataInserted()) {
				assertTrue(pocDriverRunner.getDocumentsInsertedCount() > pocDriverConfiguration.getInitialDocumentCount());
				pocDriverRunner.shutdown();
				break;
			}
		}
	}
	
	@Test
	@Order(2)
	void documentInsertedCountLimitTest() throws ExecuteException, IOException, InterruptedException {
		
		pocDriverConfiguration.setInitialDocumentCount(5000000);
		logger.info("Running test for document count - {}", pocDriverConfiguration.getInitialDocumentCount());
		pocDriverRunner.execute();
		while (true) {
			Thread.sleep(5000);
			if (pocDriverRunner.getDocumentsInsertedCount() > pocDriverConfiguration.getInitialDocumentCount()) {
				assertTrue(pocDriverRunner.isInitialDataInserted());
				pocDriverRunner.shutdown();
				break;
			}
		}
		
	}
}
