package com.mongodb.mongopush;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.exec.ExecuteException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer.ClassName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.mongodb.diffutil.DiffSummary;
import com.mongodb.diffutil.DiffUtilRunner;
import com.mongodb.mongopush.MongopushOptions.IncludeOption;
import com.mongodb.mongopush.config.MongoPushConfiguration;
import com.mongodb.mongopush.events.InitialSyncCompletedEvent;
import com.mongodb.mongopush.events.OplogStreamingCompletedEvent;
import com.mongodb.mongopush.events.VerificationTaskCompleteEvent;
import com.mongodb.mongopush.events.VerificationTaskFailedEvent;
import com.mongodb.mongopush.utility.MongoPushTestUtility;
import com.mongodb.pocdriver.POCDriverRunner;
import com.mongodb.pocdriver.config.POCDriverConfiguration;
import com.mongodb.pocdriver.events.DocumentsInsertedCountEvent;
import com.mongodb.pocdriver.events.InitialDataInsertedEvent;
import com.mongodb.test.MongoTestClient;

//@ExtendWith({SpringExtension.class})
//@TestPropertySource("/test.properties")
//@ContextConfiguration(locations = "/test-context.xml")
//@SpringJUnitConfig
//@TestMethodOrder(OrderAnnotation.class)
public class MongoPushIncludeOptionsTestBackup extends MongoPushBaseTest {

	private static Logger logger = LoggerFactory.getLogger(MongoPushIncludeOptionsTestBackup.class);
	
//	@Autowired
//	POCDriverRunner pocDriverRunner;
//	
//	@Autowired
//	MongopushRunner mongopushRunner;
//	
//	@Autowired
//	DiffUtilRunner diffUtilRunner;
//	
//	@Autowired
//	POCDriverConfiguration pocDriverConfiguration;
//	
//	@Autowired
//	MongoPushConfiguration mongoPushConfiguration;
//	
//	@Autowired
//	MongoTestClient sourceTestClient;
//	
//	@Autowired
//	MongoTestClient targetTestClient;
	
	@Autowired
	MongoPushTestUtility mongoPushTestUtility;
	
	private static String includeOptionsTestFilePath = "mongopush/includeOptionsTests";
	
//	@BeforeEach
//	public void beforeEach() {
//		pocDriverConfiguration.setPocDriverMongodbConnectionString(sourceTestClient.getConnectionString().getConnectionString());
//		mongoPushConfiguration.setMongopushSource(sourceTestClient.getConnectionString().getConnectionString());
//		mongoPushConfiguration.setMongopushTarget(targetTestClient.getConnectionString().getConnectionString());
//		sourceTestClient.dropAllDatabasesByName();
//		targetTestClient.dropAllDatabasesByName();
//		pocDriverRunner.initialDataInserted(new InitialDataInsertedEvent(false));
//		pocDriverRunner.documentsInsertedCount(new DocumentsInsertedCountEvent(0));
//		mongopushRunner.initialSyncCompleted(new InitialSyncCompletedEvent(null, false));
//		mongopushRunner.oplogStreamingCompleted(new OplogStreamingCompletedEvent(false));
//		mongopushRunner.verificationTaskComplete(new VerificationTaskCompleteEvent(false));
//		mongopushRunner.verificationTaskFailed(new VerificationTaskFailedEvent(false));
//	}

//	private List<IncludeOption[]> includeOptionsParameters() throws JsonProcessingException, ParseException {
//		List<String> includeOptionsList = fileReader("/Users/deshaggarwal/work/mongopush/code/mongopush-test/src/test/resources/mongopush/includeoptions/includeoptions01.json");
//		
//		List<IncludeOption[]> includeOptionsArrayList = new ArrayList<MongopushOptions.IncludeOption[]>();
//		for(String includeOption: includeOptionsList)
//		{
//			JSONParser jsonParser = new JSONParser();
//	        Object includeOptionObj = jsonParser.parse(includeOption);
//	        JSONArray includeOptionsJsonArray = (JSONArray) includeOptionObj;
//	        IncludeOption[] includeOptions = new IncludeOption[1];
//	        includeOptions[0] = parseIncludeOptionsJsonObject((JSONObject)includeOptionsJsonArray.get(0));
//	        includeOptionsArrayList.add(includeOptions);
//		}
//		return includeOptionsArrayList;
//        
//	}
    
//	@Order(1)
//	@Test
//	void mongoPushBasicPushDataIncludeOptionsTest() throws ExecuteException, IOException, InterruptedException, ParseException {
//		
//		logger.info("Running mongopush basic test with -push data");
//		List<IncludeOption[]> includeOptionsArrayList = includeOptionsParameters();
//		IncludeOption[] includeOptions = includeOptionsArrayList.get(0);
//		pocDriverRunner.execute();
//		
//		while (true) {
//			Thread.sleep(5000);
//			if (pocDriverRunner.isInitialDataInserted()) {
//				logger.info("Documents inserted - {}", pocDriverRunner.getDocumentsInsertedCount());
//				assertTrue(pocDriverRunner.getDocumentsInsertedCount() > pocDriverConfiguration.getInitialDocumentCount());
//				pocDriverRunner.shutdown();
//				MongopushOptions options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).includeNamespace(includeOptions).build();
//				mongopushRunner.execute(options);
//				break;
//			}
//		}
//		
//		while (true) {
//			Thread.sleep(5000);
//			if(mongopushRunner.isInitialSyncComplete())
//			{
//				assertTrue(mongopushRunner.isInitialSyncComplete());
//				mongopushRunner.shutdown();
//				break;
//			}
//		}
//		
//		DiffSummary ds = diffUtilRunner.diff();
//		assertDiffResults(ds);
//	}
	
//	private static Stream<IncludeOption[]> streamIncludeOptionsParameters() throws JsonProcessingException, ParseException {
//		List<String> includeOptionsList = fileReader("/Users/deshaggarwal/work/mongopush/code/mongopush-test/src/test/resources/mongopush/includeoptions/includeoptions01.json");
//		
//		List<IncludeOption[]> includeOptionsArrayList = new ArrayList<MongopushOptions.IncludeOption[]>();
//		for(String includeOption: includeOptionsList)
//		{
//			JSONParser jsonParser = new JSONParser();
//	        Object includeOptionObj = jsonParser.parse(includeOption);
//	        JSONArray includeOptionsJsonArray = (JSONArray) includeOptionObj;
//	        IncludeOption[] includeOptions = new IncludeOption[1];
//	        includeOptions[0] = parseIncludeOptionsJsonObject((JSONObject)includeOptionsJsonArray.get(0));
//	        includeOptionsArrayList.add(includeOptions);
//		}
//		return includeOptionsArrayList.stream();
//        
//	}
	
	private static Stream<String> streamStringParameters() {
		List<String> includeOptionsList = MongoPushTestUtility.fileReader(includeOptionsTestFilePath);
		return includeOptionsList.stream();
	}
	
//	private static List<String> fileReader(String filePath) {
//		List<String> includeOptionsList = new ArrayList<String>();
//		BufferedReader reader;
//		try {
//			reader = new BufferedReader(new FileReader(filePath));
//			String line = reader.readLine();
//			while (line != null) {
//				includeOptionsList.add(line);
//				line = reader.readLine();
//			}
//			reader.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return includeOptionsList;
//	}
//	
//    private static IncludeOption parseIncludeOptionsJsonObject(JSONObject jsonObject) 
//    {
//        String namespace = (String) jsonObject.get("namespace");
//        String filter = jsonObject.get("filter").toString();    
//        System.out.println(filter);
//        return new IncludeOption(namespace, filter);
//         
//    }
//    
//	private IncludeOption[] parseIncludeOptionsString(String includeOption) throws ParseException
//	{
//		JSONParser jsonParser = new JSONParser();
//        Object includeOptionObj = jsonParser.parse(includeOption);
//        JSONArray includeOptionsJsonArray = (JSONArray) includeOptionObj;
//        IncludeOption[] includeOptions = new IncludeOption[includeOptionsJsonArray.size()];
//        for(int i=0;i<includeOptionsJsonArray.size();i++)
//        {
//        	includeOptions[i] = parseIncludeOptionsJsonObject((JSONObject)includeOptionsJsonArray.get(i));
//        }
//        
//        return includeOptions;
//	}
	
//	@Order(1)
//	@ParameterizedTest
//	@MethodSource("streamIncludeOptionsParameters")
//	void mongoPushBasicPushDataIncludeOptionsTest(IncludeOption[] includeOptions) throws ExecuteException, IOException, InterruptedException {
//		
//		logger.info("Running mongopush basic test with -push data");
//
//		pocDriverRunner.execute();
//		
//		while (true) {
//			Thread.sleep(5000);
//			if (pocDriverRunner.isInitialDataInserted()) {
//				logger.info("Documents inserted - {}", pocDriverRunner.getDocumentsInsertedCount());
//				assertTrue(pocDriverRunner.getDocumentsInsertedCount() > pocDriverConfiguration.getInitialDocumentCount());
//				pocDriverRunner.shutdown();
//				MongopushOptions options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).includeNamespace(includeOptions).build();
//				mongopushRunner.execute(options);
//				break;
//			}
//		}
//		
//		while (true) {
//			Thread.sleep(5000);
//			if(mongopushRunner.isInitialSyncComplete())
//			{
//				assertTrue(mongopushRunner.isInitialSyncComplete());
//				mongopushRunner.shutdown();
//				break;
//			}
//		}
//		
//		DiffSummary ds = diffUtilRunner.diff();
//		assertDiffResults(ds);
//	}
    
    @Order(1)
	@ParameterizedTest
	@MethodSource("streamStringParameters")
	void mongoPushBasicPushDataIncludeOptionsTest(String includeOptionsString) throws ExecuteException, IOException, InterruptedException, ParseException {
		
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
		
		//DiffSummary ds = diffUtilRunner.diff();
		//assertDiffResults(ds);
	}
    
	private static void assertDiffResults(DiffSummary ds) {
		assertEquals(0, ds.missingDbs);
		assertEquals(0, ds.totalMissingDocs);
		assertEquals(0, ds.totalKeysMisordered);
		assertEquals(0, ds.totalHashMismatched);
	}
}
