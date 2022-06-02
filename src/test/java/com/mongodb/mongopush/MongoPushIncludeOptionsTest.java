package com.mongodb.mongopush;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.exec.ExecuteException;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.mongopush.MongopushOptions.IncludeOption;
import com.mongodb.mongopush.utility.MongoPushTestUtility;

public class MongoPushIncludeOptionsTest extends MongoPushBaseTest {

	private static Logger logger = LoggerFactory.getLogger(MongoPushIncludeOptionsTest.class);
	
	@Autowired
	MongoPushTestUtility mongoPushTestUtility;
	
	private static String includeOptionsTestFilePath = "mongopush/includeOptionsTests";
    
	private static Stream<String> streamStringParameters() {
		List<String> includeOptionsList = MongoPushTestUtility.fileReader(includeOptionsTestFilePath);
		return includeOptionsList.stream();
	}
	
    @Order(1)
	@ParameterizedTest
	@MethodSource("streamStringParameters")
	void mongoPushBasicPushDataIncludeOptionsTest(String includeOptionsString) throws ExecuteException, IOException, InterruptedException, ParseException {
		
		logger.info("Test include options - {}", includeOptionsString);
		IncludeOption[] includeOptions = mongoPushTestUtility.parseIncludeOptionsString(includeOptionsString);
		pocDriverRunner.execute();
		
		while (true) {
			Thread.sleep(5000);
			if (pocDriverRunner.isInitialDataInserted()) {
				logger.info("Documents inserted - {}", pocDriverRunner.getDocumentsInsertedCount());
				assertTrue(pocDriverRunner.getDocumentsInsertedCount() > pocDriverConfiguration.getInitialDocumentCount());
				pocDriverRunner.shutdown();
				MongopushOptions options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).includeNamespace(includeOptions).build();
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
	}
}
