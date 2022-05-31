package com.mongodb.mongopush;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mongodb.mongopush.MongopushOptions.IncludeOption;
import com.mongodb.mongopush.config.MongoPushConfiguration;
import com.mongodb.mongopush.event.InitialSyncCompletedEvent;
import com.mongodb.mongopush.event.OplogStreamingCompletedEvent;
import com.mongodb.mongopush.exec.ExecBasicLogHandler;
import com.mongodb.mongopush.exec.ExecuteResultHandler;
import com.mongodb.mongopush.exec.ProcessExecutor;
import com.mongodb.mongopush.listener.MongopushStatusListener;
import com.mongodb.pocdriver.config.POCDriverConfiguration;

@Component
//@Scope("prototype")
public class MongopushRunner implements MongopushStatusListener {

	private static Logger logger = LoggerFactory.getLogger(MongopushRunner.class);

//	@Value("${mongopushBinary}")
//	private String mongopushBinary;
//
//	@Value("${source}")
//	private String source;
//
//	@Value("${target}")
//	private String target;
	
	@Autowired
	MongoPushConfiguration mongoPushConfiguration;

	private ProcessExecutor executor;
	private ExecuteResultHandler executeResultHandler;

	private CommandLine cmdLine;

	private boolean processFailed;

	private boolean initialSyncComplete;
	private InitialSyncCompletedEvent initialSyncCompletedEvent;
	private boolean oplogStreamingCompleted;
	private OplogStreamingCompletedEvent oplogStreamingCompletedEvent;

	public boolean isComplete() {
		return executeResultHandler != null && executeResultHandler.hasResult();
	}

	public ExecuteResultHandler getExecuteResultHandler() {
		return executeResultHandler;
	}

	public void execute(MongopushOptions options) throws ExecuteException, IOException {

		logger.debug("execute() MONGOPUSH_BINARY: " + mongoPushConfiguration.getMongopushBinary());

		executeResultHandler = new ExecuteResultHandler(this);
		cmdLine = new CommandLine(new File(mongoPushConfiguration.getMongopushBinary()));

		// this currently assumes that mongopush has been modified to accept command
		// line only
		// and to not exit when there's no config file

		switch (options.getMode()) {
			case PUSH_DATA:
				addArg("push", "data");
				break;
			default:
				break;
		}
		
		for (IncludeOption include : options.getIncludeOptions()) {
			addArg("include", include.toJson());
		}

		addArg("source", mongoPushConfiguration.getMongopushSource());
		addArg("target", mongoPushConfiguration.getMongopushTarget());
		addArg("yes");

		PumpStreamHandler psh = new PumpStreamHandler(new ExecBasicLogHandler("mongopush", this));

		executor = new ProcessExecutor();
		executor.setExitValue(0);
		executor.setStreamHandler(psh);

		logger.debug("executing {}", String.join(" ", cmdLine.toStrings()));
		executor.execute(cmdLine, executeResultHandler);

	}

	public void waitForInitialSyncComplete() {
		while (isInitialSyncComplete() == false && processFailed == false) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
		}
	}

	public void shutdown() throws IOException {
		logger.debug("shutdown() initiated");
		executor.stop();
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
			cmdLine.addArgument("--" + argName + "=" + argValue, false);
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
		this.initialSyncCompletedEvent = initialSyncCompletedEvent;
		logger.debug("***** initial sync completed {} *****", initialSyncCompletedEvent.getInitialSyncDuration());
		initialSyncComplete = initialSyncCompletedEvent.getInitialSyncCompleted();
	}

	@Override
	public void processFailed(ExecuteException e) {
		this.processFailed = true;
	}

	public boolean isProcessFailed() {
		return processFailed;
	}

	public void oplogStreamingCompleted(OplogStreamingCompletedEvent oplogStreamingCompletedEvent) {
		this.oplogStreamingCompletedEvent = oplogStreamingCompletedEvent;
		logger.debug("***** oplog streaming completed {} *****", oplogStreamingCompletedEvent.isOplogStreamingCompleted());
		oplogStreamingCompleted = oplogStreamingCompletedEvent.isOplogStreamingCompleted();
	}

	public boolean isOplogStreamingCompleted() {
		return oplogStreamingCompleted;
	}
}
