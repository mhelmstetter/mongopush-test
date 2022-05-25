package com.mongodb.mongopush;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.mongopush.exec.ExecuteResultHandler;

@ExtendWith({SpringExtension.class})
@TestPropertySource("/test.properties")
@ContextConfiguration(locations = "/test-context.xml")
class MongoPushTest {
	
	@Autowired
	MongopushRunner mongopushRunner;
	
	@Test
	void testInitialSyncOnly() throws ExecuteException, IOException {
		mongopushRunner.execute();
		
		while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			if (mongopushRunner.isInitialSyncComplete()) {
				break;
			}
		}
		
		mongopushRunner.shutdown();
	}

	//@Test
	void test() throws ExecuteException, IOException {
		mongopushRunner.execute();
		
		while (true) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			if (mongopushRunner.isComplete()) {
				break;
			}
		}
		
		ExecuteResultHandler executeResultHandler = mongopushRunner.getExecuteResultHandler();
		int exit = executeResultHandler.getExitValue();
		if (exit != 0) {
			ExecuteException e = executeResultHandler.getException();
			throw e;
		}
	}

}
