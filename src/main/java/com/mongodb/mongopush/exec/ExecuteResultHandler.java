package com.mongodb.mongopush.exec;

import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.mongopush.listener.MongopushStatusListener;

public class ExecuteResultHandler extends DefaultExecuteResultHandler {
	
	private static Logger logger = LoggerFactory.getLogger(ExecuteResultHandler.class);
	
	private MongopushStatusListener listener;

	public ExecuteResultHandler(MongopushStatusListener listener) {
		this.listener = listener;
	}

	@Override
	public void onProcessComplete(int exitValue) {
		logger.debug("onProcessComplete(): exitValue: {}", exitValue);
		super.onProcessComplete(exitValue);
	}

	@Override
	public void onProcessFailed(ExecuteException e) {
		logger.error("onProcessFailed(): ExecuteException: {}", e);
		super.onProcessFailed(e);
		listener.processFailed(e);
	}
	
	
	

}
