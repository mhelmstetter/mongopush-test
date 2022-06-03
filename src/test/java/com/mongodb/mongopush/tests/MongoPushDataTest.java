package com.mongodb.mongopush.tests;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.exec.ExecuteException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.mongopush.MongoPushBaseTest;
import com.mongodb.mongopush.model.MongoPushTestEvent;
import com.mongodb.mongopush.model.MongoPushTestModel;
import com.mongodb.mongopush.utility.MongoPushTestUtility;

public class MongoPushDataTest extends MongoPushBaseTest {

	private static Logger logger = LoggerFactory.getLogger(MongoPushDataTest.class);

	@Autowired
	MongoPushTestUtility mongoPushTestUtility;
	
	private static String testFolderPath = "mongopush/tests";
	
	private static Stream<String> streamStringParameters() {
		List<String> testFileNamesList = MongoPushTestUtility.readAllFilesFromPath(testFolderPath);
		return testFileNamesList.stream();
	}
	
	@ParameterizedTest
	@MethodSource("streamStringParameters")
	void mongoPushBasicPushDataTest(String testFilePath) throws ExecuteException, IOException, InterruptedException, ParseException {
		
		logger.info("Test file path - {}", testFilePath);
		MongoPushTestModel mongoPushTestModel = mongoPushTestUtility.readFileAndParse(testFilePath);
		if(mongoPushTestModel.getMongoPushTestEvents() == null)
		{
			logger.info("Error in defining test sequence, Please check the test events");
		}
		else {
			for(MongoPushTestEvent mongoPushTestEvent: mongoPushTestModel.getMongoPushTestEvents())
			{
				processTestEventsSequence(mongoPushTestEvent, mongoPushTestModel);
			}
		}
		
	}
}
