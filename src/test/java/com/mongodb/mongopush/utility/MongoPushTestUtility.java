package com.mongodb.mongopush.utility;

import static com.mongodb.mongopush.constants.MongoPushConstants.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mongodb.mongopush.MongopushOptions.IncludeOption;
import com.mongodb.mongopush.events.MongoPushTestEvent;
import com.mongodb.mongopush.events.MongoPushTestSequence;
import com.mongodb.mongopush.model.MongoPushTestModel;

@Component
public class MongoPushTestUtility {

	private static Logger logger = LoggerFactory.getLogger(MongoPushTestUtility.class);
	
	private static String testResourceBasePath = TEST_RESOURCE_BASE_PATH;
	
	public static List<String> readAllFilesFromPath(String folderPath)
	{
		File folder = new File(testResourceBasePath.concat(folderPath));
		File[] listOfFiles = folder.listFiles();
		List<String> testFileNamesList = null;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				if(testFileNamesList ==  null)
				{
					testFileNamesList = new ArrayList<String>();
				}
				testFileNamesList.add(listOfFiles[i].getPath());
			}
		}
		return testFileNamesList;
	}
	
	public MongoPushTestModel readFileAndParse(String filePath) throws FileNotFoundException, IOException, ParseException
	{
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(filePath));
		String testSequenceName = (String) jsonObject.get(TEST_SEQUENCE_NAME);
		String includeOptions = null;
		if(jsonObject.get(INCLUDE_OPTIONS) != null)
		{
			includeOptions = jsonObject.get(INCLUDE_OPTIONS).toString();
		}
		
		String pocDriverArguments = (String) jsonObject.get(POC_DRIVER_ARGUMENTS);
		
        List<MongoPushTestEvent> testSequenceEventsList = getTestSequenceEvents(testSequenceName);
        IncludeOption[] includeOptionArray = parseIncludeOptionsString(includeOptions);
        
        MongoPushTestModel mongoPushTestModel = new MongoPushTestModel();
        mongoPushTestModel.setMongoPushTestEvents(testSequenceEventsList);
        mongoPushTestModel.setIncludeOptions(includeOptionArray);
        mongoPushTestModel.setPocdriveArguments(pocDriverArguments);
        return mongoPushTestModel;
	}
	
	public static List<String> fileReader(String filePath) {
		String fileProjectContextPath = testResourceBasePath.concat(filePath);
		logger.info("File project context path {}", fileProjectContextPath);
		List<String> includeOptionsList = new ArrayList<String>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(fileProjectContextPath));
			String line = reader.readLine();
			while (line != null) {
				includeOptionsList.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return includeOptionsList;
	}
	
	public IncludeOption[] parseIncludeOptionsString(String includeOption) throws ParseException
	{
		IncludeOption[] includeOptions = null;
		if(includeOption != null)
		{
			JSONParser jsonParser = new JSONParser();
	        Object includeOptionObj = jsonParser.parse(includeOption);
	        JSONArray includeOptionsJsonArray = (JSONArray) includeOptionObj;
	        includeOptions = new IncludeOption[includeOptionsJsonArray.size()];
	        for(int i=0;i<includeOptionsJsonArray.size();i++)
	        {
	        	includeOptions[i] = parseIncludeOptionsJsonObject((JSONObject)includeOptionsJsonArray.get(i));
	        }
		}
        
        return includeOptions;
	}
	
    private IncludeOption parseIncludeOptionsJsonObject(JSONObject jsonObject) 
    {
        String namespace = (String) jsonObject.get("namespace");
        String filter = jsonObject.get("filter").toString();    
        logger.info("include options - namespace - {}, filter - {}", namespace, filter);
        return new IncludeOption(namespace, filter);
         
    }
    
//    public List<MongoPushTestEvent> parseTestSequenceString(String testSequenceName)
//	{
//    	List<MongoPushTestEvent> testSequenceEventsList = null;
//		if(testSequence != null && !testSequence.isBlank())
//		{
//			testSequenceEventsList = new ArrayList<MongoPushTestEvent>();
//			String[] testSequenceEventsArray = testSequence.split("->");
//			for(String eventName : testSequenceEventsArray)
//			{
//				MongoPushTestEvent mongoPushTestEvent = getEventEnumFromName(eventName);
//				if(mongoPushTestEvent != null)
//				{
//					testSequenceEventsList.add(getEventEnumFromName(eventName));
//				}
//				else
//				{
//					testSequenceEventsList = null;
//					break;
//				}
//			}
//		}
//        
//        return testSequenceEventsList;
//	}
    
    public List<MongoPushTestEvent> getTestSequenceEvents(String testSequenceName)
    {
    	List<MongoPushTestEvent> mongoPushTestEvents = null;
    	for(MongoPushTestSequence mongoPushTestSequence: MongoPushTestSequence.values())
    	{
    		if(mongoPushTestSequence.name().equalsIgnoreCase(testSequenceName))
    		{
    			mongoPushTestEvents = mongoPushTestSequence.getMongoPushTestEvents();
    			break;
    		}
    	}
    	return mongoPushTestEvents;
    }
    
//    private MongoPushTestEvent getEventEnumFromName(String eventName)
//    {
//    	MongoPushTestEvent eventNameEnum = null;
//    	for(MongoPushTestEvent mongoPushTestEvent: MongoPushTestEvent.values())
//    	{
//    		if(mongoPushTestEvent.getName().equalsIgnoreCase(eventName))
//    		{
//    			eventNameEnum = mongoPushTestEvent;
//    			break;
//    		}
//    	}
//    	return eventNameEnum;
//    }
}
