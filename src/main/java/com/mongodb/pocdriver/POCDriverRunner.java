package com.mongodb.pocdriver;

import static com.mongodb.mongopush.constants.MongoPushConstants.*;

import java.io.IOException;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.ExecuteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.pocdriver.config.POCDriverConfiguration;
import com.mongodb.pocdriver.events.DocumentsInsertedCountEvent;
import com.mongodb.pocdriver.events.InitialDataInsertedEvent;
import com.mongodb.pocdriver.handler.POCDriverLogHandler;
import com.mongodb.pocdriver.listener.POCDriverStatusListener;

@Component
public class POCDriverRunner implements POCDriverStatusListener {

	private static Logger logger = LoggerFactory.getLogger(POCDriverRunner.class);
	
	@Autowired
	POCDriverConfiguration pocDriverConfiguration;
	
	private POCDriverLogHandler pocDriverLogHandler;
	private InitialDataInsertedEvent initialDataInsertedEvent;
	private boolean initialDataInserted;
	private DocumentsInsertedCountEvent documentsInsertedCountEvent;
	private long documentInsertedCount;
	
	public void execute() throws ExecuteException, IOException, InterruptedException {

		logger.debug("POCDriver relative binary path: {}", pocDriverConfiguration.getPocDriverBinaryPath());
		
		pocDriverLogHandler = new POCDriverLogHandler(POC_DRIVER, this);
		
		
		Executors.newSingleThreadExecutor().execute(new Runnable() {
		    @Override
		    public void run() {
			    try {
			    	ProcessBuilder builder = new ProcessBuilder(createDriverRunCommand());
					Process ps = builder.start();
					pocDriverLogHandler.setProcess(ps);
					pocDriverLogHandler.setInitialDocumentCount(pocDriverConfiguration.getInitialDocumentCount());
					pocDriverLogHandler.processLogs();
		    	} catch (IOException e) {
					e.printStackTrace();
				}
		    }
		});	
	}
	
	private String[] createDriverRunCommand() {
		CommandLine cmdLine = new CommandLine(JAVA);
		cmdLine.addArgument(HYPHEN_JAR);
		String pocDriverDefaultPath = this.getClass().getClassLoader().getResource(pocDriverConfiguration.getPocDriverBinaryPath()).toString().split(COLON)[1];
		cmdLine.addArgument(pocDriverDefaultPath);
		
		if(!pocDriverConfiguration.isPocDriverDefault())
		{
			String[] cmdOptions = pocDriverConfiguration.getPocDriverCommandlineArguments().split(SPACE);
			cmdLine.addArguments(cmdOptions);
			cmdLine.addArgument("-c");
			cmdLine.addArgument(pocDriverConfiguration.getPocDriverMongodbConnectionString());
		}
		logger.debug(cmdLine.toString());
		logger.debug(cmdLine.toStrings().toString());
		return cmdLine.toStrings();
	}
	
	public void shutdown() throws IOException {
		Process process = pocDriverLogHandler.getProcess();
		if(process.isAlive()) {
			process.destroy();
		}
	}
	
	@Override
	public boolean isInitialDataInserted() {
		return initialDataInserted;
	}

	@Override
	public void initialDataInserted(InitialDataInsertedEvent initialDataInsertedEvent) {
		
		this.initialDataInsertedEvent = initialDataInsertedEvent;
		logger.debug("***** initial data inserted {} *****", initialDataInsertedEvent.getInitialDataInserted());
		this.initialDataInserted = initialDataInsertedEvent.getInitialDataInserted();
	}
	
	@Override
	public long getDocumentsInsertedCount() {
		return documentInsertedCount;
	}
	
	@Override
	public void documentsInsertedCount(DocumentsInsertedCountEvent documentsInsertedCountEvent) {
		
		this.documentsInsertedCountEvent = documentsInsertedCountEvent;
		logger.debug("***** document inserted count {} *****", documentsInsertedCountEvent.getDocumentCount());
		this.documentInsertedCount = documentsInsertedCountEvent.getDocumentCount();
	}
	
}
