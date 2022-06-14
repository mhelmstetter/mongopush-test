package com.mongodb.mongopush.utility;

import static com.mongodb.mongopush.constants.MongoPushConstants.FILTER;
import static com.mongodb.mongopush.constants.MongoPushConstants.ID_AS_DOCUMENT_ARGUMENTS;
import static com.mongodb.mongopush.constants.MongoPushConstants.INCLUDE_OPTIONS;
import static com.mongodb.mongopush.constants.MongoPushConstants.NAMESPACE;
import static com.mongodb.mongopush.constants.MongoPushConstants.POC_DRIVER_ARGUMENTS;
import static com.mongodb.mongopush.constants.MongoPushConstants.POPULATE_DATA_ARGUMENTS;
import static com.mongodb.mongopush.constants.MongoPushConstants.REPLACE_DATA_ARGUMENTS;
import static com.mongodb.mongopush.constants.MongoPushConstants.TEST_RESOURCE_BASE_PATH;
import static com.mongodb.mongopush.constants.MongoPushConstants.TEST_SEQUENCE_NAME;
import static com.mongodb.mongopush.constants.MongoPushConstants.TO;
import static com.mongodb.mongopush.constants.MongoPushConstants.UNIQUE_INDEX_ARGUMENTS;

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
import com.mongodb.mongopush.model.MongoPushTestEvent;
import com.mongodb.mongopush.model.MongoPushTestModel;
import com.mongodb.mongopush.model.MongoPushTestSequence;
import com.mongodb.mongopush.model.ReplaceDataArgumentsModel;

@Component
public class MongoPushTestUtility {

	private static Logger logger = LoggerFactory.getLogger(MongoPushTestUtility.class);
	
	public static List<String> readAllFilesFromPath(String folderPath, boolean needFolders)
	{
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		List<String> testFileNamesList = new ArrayList<String>();
		List<String> testFolderNamesList = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				testFileNamesList.add(listOfFiles[i].getPath());
			}
			else
			{
				testFolderNamesList.add(listOfFiles[i].getPath());
			}
		}
		if(needFolders)
			return testFolderNamesList;
		else
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
		String populateDataArguments = (String) jsonObject.get(POPULATE_DATA_ARGUMENTS);
		
		String replaceDataArguments = null;
		if(jsonObject.get(REPLACE_DATA_ARGUMENTS) != null)
		{
			replaceDataArguments = jsonObject.get(REPLACE_DATA_ARGUMENTS).toString();
		}
		
		String idAsDocumentArguments = (String) jsonObject.get(ID_AS_DOCUMENT_ARGUMENTS);
		String uniqueIndexArguments = (String) jsonObject.get(UNIQUE_INDEX_ARGUMENTS);
		
        List<MongoPushTestEvent> testSequenceEventsList = getTestSequenceEvents(testSequenceName);
        IncludeOption[] includeOptionArray = parseIncludeOptionsString(includeOptions);
        ReplaceDataArgumentsModel replaceDataArgumentsModel = parseReplaceDataArguments(replaceDataArguments);
        
        MongoPushTestModel mongoPushTestModel = new MongoPushTestModel();
        mongoPushTestModel.setMongoPushTestEvents(testSequenceEventsList);
        mongoPushTestModel.setIncludeOptions(includeOptionArray);
        mongoPushTestModel.setPocdriveArguments(pocDriverArguments);
        mongoPushTestModel.setPopulateDataArguments(populateDataArguments);
        mongoPushTestModel.setReplaceDataArguments(replaceDataArgumentsModel);
        mongoPushTestModel.setIdAsDocumentArguments(idAsDocumentArguments);
        mongoPushTestModel.setUniqueIndexArguments(uniqueIndexArguments);
        return mongoPushTestModel;
	}
	
	public static List<String> fileReader(String filePath) {
		String fileProjectContextPath = TEST_RESOURCE_BASE_PATH.concat(filePath);
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
	
	private ReplaceDataArgumentsModel parseReplaceDataArguments(String replaceDataArguments) throws ParseException 
    {
		ReplaceDataArgumentsModel replaceDataArgumentsModel = null;
		if(replaceDataArguments != null)
		{
			JSONParser jsonParser = new JSONParser();
			JSONObject replaceDataArgumentsObj = (JSONObject) jsonParser.parse(replaceDataArguments);
	        String namespace = (String) replaceDataArgumentsObj.get(NAMESPACE);
	        String filter = null;
	        if(replaceDataArgumentsObj.get(FILTER) != null)
	        {
	        	filter = replaceDataArgumentsObj.get(FILTER).toString();
	        }
	        logger.info("Replace data arguments - namespace - {}, filter - {}, to - {}", namespace, filter);
	        
	        replaceDataArgumentsModel = new ReplaceDataArgumentsModel();
	        replaceDataArgumentsModel.setNamespace(namespace);
	        replaceDataArgumentsModel.setFilter(filter);
		}
        
        return replaceDataArgumentsModel;
         
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
        String namespace = (String) jsonObject.get(NAMESPACE);
        String filter = null;
        if(jsonObject.get(FILTER) != null)
        {
        	filter = jsonObject.get(FILTER).toString();
        }
        String to = null;
        if(jsonObject.get(TO) != null)
        {
        	to = (String) jsonObject.get(TO);
        }
        logger.info("include options - namespace - {}, filter - {}, to - {}", namespace, filter, to);
        return new IncludeOption(namespace, filter, to);
         
    }
    
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
    
}
