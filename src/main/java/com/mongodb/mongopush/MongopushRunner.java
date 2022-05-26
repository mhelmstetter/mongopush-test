package com.mongodb.mongopush;

import java.io.File;
import java.io.IOException;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.mongodb.mongopush.MongopushOptions.IncludeOption;
import com.mongodb.mongopush.event.InitialSyncCompletedEvent;
import com.mongodb.mongopush.exec.ExecBasicLogHandler;
import com.mongodb.mongopush.exec.ExecuteResultHandler;
import com.mongodb.mongopush.exec.ProcessExecutor;
import com.mongodb.mongopush.listener.MongopushStatusListener;

@Component
@Scope("prototype")
public class MongopushRunner implements MongopushStatusListener {

	private static Logger logger = LoggerFactory.getLogger(MongopushRunner.class);

	@Value("${mongopushBinary}")
	private String mongopushBinary;

	@Value("${source}")
	private String source;

	@Value("${target}")
	private String target;

	private ProcessExecutor executor;
	private ExecuteResultHandler executeResultHandler;

	private CommandLine cmdLine;

	private boolean initialSyncComplete;
	private boolean processFailed;

	private InitialSyncCompletedEvent initialSyncCompletedEvent;

	public boolean isComplete() {
		return executeResultHandler != null && executeResultHandler.hasResult();
	}

	public ExecuteResultHandler getExecuteResultHandler() {
		return executeResultHandler;
	}

	public void execute(MongopushOptions options) throws ExecuteException, IOException {

		logger.debug("execute() MONGOPUSH_BINARY: " + mongopushBinary);

		executeResultHandler = new ExecuteResultHandler(this);
		cmdLine = new CommandLine(new File(mongopushBinary));

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

		addArg("source", source);
		addArg("target", target);
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
		logger.debug("***** initial sync completed {} *****", initialSyncCompletedEvent.getInitialSyncDuration());
		initialSyncComplete = true;
		this.initialSyncCompletedEvent = initialSyncCompletedEvent;

	}

	@Override
	public void processFailed(ExecuteException e) {
		this.processFailed = true;
	}

	public boolean isProcessFailed() {
		return processFailed;
	}

}
