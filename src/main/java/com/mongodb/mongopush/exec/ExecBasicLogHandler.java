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
import com.mongodb.mongopush.events.InitialSyncCompletedEvent;
import com.mongodb.mongopush.events.OplogStreamingCompletedEvent;
import com.mongodb.mongopush.events.VerificationTaskCompleteEvent;
import com.mongodb.mongopush.events.VerificationTaskFailedEvent;
import com.mongodb.mongopush.listener.MongopushStatusListener;

public class ExecBasicLogHandler extends LogOutputStream {
	
	private static Logger logger = LoggerFactory.getLogger(MongopushRunner.class);
    
    private PrintWriter writer;
    
    Pattern initialSyncCompletedPattern = Pattern.compile("^.* initial data copy completed, took (.*)");
    Pattern oplogStreamingCompletedPattern = Pattern.compile("^.* lag 0s");
    Pattern verificationTaskCompletePattern = Pattern.compile("^.* Verification tasks complete");
    Pattern verificationTaskFailedPattern = Pattern.compile("^.* Verification Task .* out of retries, failing");
    
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
        
        Matcher initialSyncMatcher = initialSyncCompletedPattern.matcher(line);
        if (initialSyncMatcher.find()) {
        	String execTimeStr = initialSyncMatcher.group(1);
        	listener.initialSyncCompleted(new InitialSyncCompletedEvent(execTimeStr, true));
        }
        
        Matcher oplogStreamingMatcher = oplogStreamingCompletedPattern.matcher(line);
        if (oplogStreamingMatcher.find()) {
        	listener.oplogStreamingCompleted(new OplogStreamingCompletedEvent(true));
        }
        
        Matcher verificationTaskCompleteMatcher = verificationTaskCompletePattern.matcher(line);
        if (verificationTaskCompleteMatcher.find()) {
        	listener.verificationTaskComplete(new VerificationTaskCompleteEvent(true));
        }
        
        Matcher verificationTaskFailedMatcher = verificationTaskFailedPattern.matcher(line);
        if (verificationTaskFailedMatcher.find()) {
        	listener.verificationTaskFailed(new VerificationTaskFailedEvent(true));
        }
    }
}