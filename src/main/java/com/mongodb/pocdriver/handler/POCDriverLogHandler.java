package com.mongodb.pocdriver.handler;

import static com.mongodb.pocdriver.constants.POCDriverConstants.DOT_LOG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import com.mongodb.pocdriver.events.DocumentsInsertedCountEvent;
import com.mongodb.pocdriver.events.InitialDataInsertedEvent;
import com.mongodb.pocdriver.listener.POCDriverStatusListener;

public class POCDriverLogHandler {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(POCDriverLogHandler.class);
    
    private PrintWriter writer;
    
    Pattern initialDataInsertedPattern = Pattern.compile("^.* new documents inserted - collection has (.*) in total(.*)");
    
    private POCDriverStatusListener listener;
    private Process process;
    private BufferedReader br;
    
    private long initialDocumentCount;

	public void setInitialDocumentCount(long initialDocumentCount) {
		this.initialDocumentCount = initialDocumentCount;
	}

	public Process getProcess() {
		return process;
	}
	
	public void setProcess(Process process) {
		this.process = process;
	}

	public POCDriverLogHandler(String id, POCDriverStatusListener listener) throws IOException {
        super();
        writer = new PrintWriter(new FileWriter(new File(id.concat(DOT_LOG))));
        this.listener = listener;
    }

    synchronized public void processLogs()
    {
    		try {
    			processLine();
    		} catch (IOException | InterruptedException e) {
    			e.printStackTrace();
    		}
    }
    
    
    protected void processLine() throws IOException, InterruptedException {   	
    	br = new BufferedReader(new InputStreamReader(process.getInputStream()));
    	String line = br.readLine();
    	while (line != null) {
				logger.debug(line);
		        writer.println(line);
		        writer.flush();
	        	Matcher initialDataInsertedMatcher = initialDataInsertedPattern.matcher(line);
	            if (initialDataInsertedMatcher.find()) {
	            	String dataInsertedCount = initialDataInsertedMatcher.group(1);
	            	System.out.println("Data inserted count - " + dataInsertedCount);
	            	String[] documentCountArray = dataInsertedCount.split(",");
	            	String documentCount = "";
	            	for(int i=0;i<documentCountArray.length;i++)
	            	{
	            		documentCount += documentCountArray[i];
	            	}
	            	long docCountLong = Long.valueOf(documentCount);
	            	listener.documentsInsertedCount(new DocumentsInsertedCountEvent(docCountLong));
	            	if(docCountLong > initialDocumentCount)
	            	{
	            		listener.initialDataInserted(new InitialDataInsertedEvent(true));
	            		break;
	            	}
	            }
	            line = br.readLine();
    	}
    	this.wait(5000);
    	if(process.isAlive())
		{
			this.processLine();
		}
    }
    
}
