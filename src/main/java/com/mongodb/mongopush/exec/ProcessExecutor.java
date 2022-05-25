package com.mongodb.mongopush.exec;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

/**
 * Stateful implementation of DefaultExecutor, not thread safe as it holds the current Process
 * 
 * NOTE - this probably won't work on Windows!
 * 
 * @author mh
 *
 */
public class ProcessExecutor extends DefaultExecutor {
	
	private Process process;

	@Override
	protected Process launch(CommandLine command, Map<String, String> env, File dir) throws IOException {
		this.process = super.launch(command, env, dir);
		return process;
	}
	
	
	public void stop() throws IOException {
		if (process != null && process.isAlive()) {
			long pid = process.pid();
			Runtime.getRuntime().exec("kill -s SIGINT " + pid); // TODO what to do about Windows?
		}
	}
	
	

}
