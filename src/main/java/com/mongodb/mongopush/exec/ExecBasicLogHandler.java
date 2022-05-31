package com.mongodb.mongopush.exec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.exec.LogOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.mongopush.MongopushRunner;
import com.mongodb.mongopush.event.InitialSyncCompletedEvent;
import com.mongodb.mongopush.event.OplogStreamingCompletedEvent;
import com.mongodb.mongopush.listener.MongopushStatusListener;

public class ExecBasicLogHandler extends LogOutputStream {
	
	private static Logger logger = LoggerFactory.getLogger(MongopushRunner.class);
    
    private PrintWriter writer;
    
    Pattern initialSyncCompleted = Pattern.compile("^.* initial data copy completed, took (.*)");
    Pattern oplogStreamingCompleted = Pattern.compile("^.* lag 0s");
    
    private MongopushStatusListener listener;
    
    public ExecBasicLogHandler(String id, MongopushStatusListener listener) throws IOException {
        super();
        writer = new PrintWriter(new FileWriter(new File(id + ".log")));
        this.listener = listener;
    }


    @Override
    protected void processLine(String line, int logLevel) {
    	logger.debug(line);
        writer.println(line);
        writer.flush();
        
        Matcher initialSyncMatcher = initialSyncCompleted.matcher(line);
        if (initialSyncMatcher.find()) {
        	String execTimeStr = initialSyncMatcher.group(1);
        	listener.initialSyncCompleted(new InitialSyncCompletedEvent(execTimeStr, true));
        }
        
        Matcher oplogStreamingMatcher = oplogStreamingCompleted.matcher(line);
        if (oplogStreamingMatcher.find()) {
        	listener.oplogStreamingCompleted(new OplogStreamingCompletedEvent(true));
        }
    }
}