package com.mongodb.mongopush;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

import com.mongodb.diffutil.DiffUtilRunner;
import com.mongodb.mongopush.MongopushOptions.IncludeOption;
import com.mongodb.mongopush.config.MongoPushConfiguration;
import com.mongodb.mongopush.events.InitialSyncCompletedEvent;
import com.mongodb.mongopush.events.OplogStreamingCompletedEvent;
import com.mongodb.mongopush.events.VerificationTaskCompleteEvent;
import com.mongodb.mongopush.events.VerificationTaskFailedEvent;
import com.mongodb.pocdriver.POCDriverRunner;
import com.mongodb.pocdriver.config.POCDriverConfiguration;
import com.mongodb.pocdriver.events.DocumentsInsertedCountEvent;
import com.mongodb.pocdriver.events.InitialDataInsertedEvent;
import com.mongodb.test.MongoTestClient;

@ExtendWith({SpringExtension.class})
@TestPropertySource("/test.properties")
@ContextConfiguration(locations = "/test-context.xml")
@SpringJUnitConfig
@TestMethodOrder(OrderAnnotation.class)
public class MongoPushBaseTest {

	private static Logger logger = LoggerFactory.getLogger(MongoPushBaseTest.class);
	
	@Autowired
	POCDriverRunner pocDriverRunner;
	
	@Autowired
	MongopushRunner mongopushRunner;
	
	@Autowired
	DiffUtilRunner diffUtilRunner;
	
	@Autowired
	POCDriverConfiguration pocDriverConfiguration;
	
	@Autowired
	MongoPushConfiguration mongoPushConfiguration;
	
	@Autowired
	MongoTestClient sourceTestClient;
	
	@Autowired
	MongoTestClient targetTestClient;
	
	private static String testResourceBasePath = "src/test/resources/";
	
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
}
