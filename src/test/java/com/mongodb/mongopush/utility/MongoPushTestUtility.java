package com.mongodb.mongopush.utility;

import java.io.BufferedReader;
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

import com.mongodb.mongopush.MongoPushTestEvent;
import com.mongodb.mongopush.MongopushOptions.IncludeOption;

@Component
public class MongoPushTestUtility {

	private static Logger logger = LoggerFactory.getLogger(MongoPushTestUtility.class);
	
	private static String testResourceBasePath = "src/test/resources/";
	
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
		JSONParser jsonParser = new JSONParser();
        Object includeOptionObj = jsonParser.parse(includeOption);
        JSONArray includeOptionsJsonArray = (JSONArray) includeOptionObj;
        IncludeOption[] includeOptions = new IncludeOption[includeOptionsJsonArray.size()];
        for(int i=0;i<includeOptionsJsonArray.size();i++)
        {
        	includeOptions[i] = parseIncludeOptionsJsonObject((JSONObject)includeOptionsJsonArray.get(i));
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
    
    public List<MongoPushTestEvent> parseTestSequenceString(String testSequence)
	{
    	List<MongoPushTestEvent> testSequenceEventsList = null;
		if(testSequence != null && !testSequence.isBlank())
		{
			testSequenceEventsList = new ArrayList<MongoPushTestEvent>();
			String[] testSequenceEventsArray = testSequence.split("->");
			for(String eventName : testSequenceEventsArray)
			{
				MongoPushTestEvent mongoPushTestEvent = getEventEnumFromName(eventName);
				if(mongoPushTestEvent != null)
				{
					testSequenceEventsList.add(getEventEnumFromName(eventName));
				}
				else
				{
					testSequenceEventsList = null;
					break;
				}
			}
		}
        
        return testSequenceEventsList;
	}
    
    private MongoPushTestEvent getEventEnumFromName(String eventName)
    {
    	MongoPushTestEvent eventNameEnum = null;
    	for(MongoPushTestEvent mongoPushTestEvent: MongoPushTestEvent.values())
    	{
    		if(mongoPushTestEvent.getName().equalsIgnoreCase(eventName))
    		{
    			eventNameEnum = mongoPushTestEvent;
    			break;
    		}
    	}
    	return eventNameEnum;
    }
}
