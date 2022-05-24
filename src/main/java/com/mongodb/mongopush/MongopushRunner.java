package com.mongodb.mongopush;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.mongopush.event.InitialSyncCompletedEvent;
import com.mongodb.mongopush.listener.MongopushStatusListener;

@Component
public class MongopushRunner implements MongopushStatusListener {
	
	private static Logger logger = LoggerFactory.getLogger(MongopushRunner.class);

	@Value("${mongopushBinary}")
	private String mongopushBinary;
	
	@Value("${source}")
	private String source;
	
	@Value("${target}")
	private String target;
	

	private ExecuteResultHandler executeResultHandler;

	private CommandLine cmdLine;
	
	private boolean initialSyncComplete;
	
	private InitialSyncCompletedEvent initialSyncCompletedEvent;
	
	public boolean isComplete() {
		return executeResultHandler != null && executeResultHandler.hasResult();
	}
	
	public ExecuteResultHandler getExecuteResultHandler() {
		return executeResultHandler;
	}

	public void execute() throws ExecuteException, IOException {

		logger.debug("execute() MONGOPUSH_BINARY: " + mongopushBinary);

		executeResultHandler = new ExecuteResultHandler();
		cmdLine = new CommandLine(new File(mongopushBinary));
		
		
		// this currently assumes that mongopush has been modified to accept command line only
		// and to not exit when there's no config file
		
		addArg("push", "data");
		addArg("source", source);
		addArg("target", target);
		addArg("yes");
		//addArg("drop", true);

		//addArg("license", "Apache-2.0");
		//addArg("exec", "/var/folders/90/hvcbspz52fq_xw6bn2wjrwvw0000gp/T/mongopush-data.json");

		PumpStreamHandler psh = new PumpStreamHandler(new ExecBasicLogHandler("mongopush", this));

		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(0);
		executor.setStreamHandler(psh);

		executor.execute(cmdLine, executeResultHandler);

	}

	private void addArg(String argName) {
		cmdLine.addArgument("--" + argName);
	}

	private void addArg(String argName, Integer argValue) {
		if (argValue != null) {
			cmdLine.addArgument("--" + argName + "=" + argValue);
		}
	}

	private void addArg(String argName, String argValue) {
		if (argValue != null) {
			cmdLine.addArgument("--" + argName + "=" + argValue);
		}
	}

	private void addArg(String argName, Boolean argValue) {
		if (argValue != null && argValue) {
			cmdLine.addArgument("--" + argName);
		}
	}

	@Override
	public boolean isInitialSyncComplete() {
		return initialSyncComplete;
	}

	@Override
	public void initialSyncCompleted(InitialSyncCompletedEvent initialSyncCompletedEvent) {
		logger.debug("***** initial sync completed {} *****", initialSyncCompletedEvent.getInitialSyncDuration());
		initialSyncComplete = true;
		this.initialSyncCompletedEvent = initialSyncCompletedEvent;
		
	}



}
