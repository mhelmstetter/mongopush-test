package com.mongodb.mongopush;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.commons.exec.ExecuteException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.mongopush.diff.DiffUtilRunner;

@ExtendWith({SpringExtension.class})
@TestPropertySource("/test.properties")
@ContextConfiguration(locations = "/test-context.xml")
class MongoPushTest {
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	DiffUtilRunner diffUtil;
	
	@Test
	void testInitialSyncOnly() throws ExecuteException, IOException {
		
		MongopushOptions options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).build();
		MongopushRunner mongopushRunner = context.getBean(MongopushRunner.class);
		mongopushRunner.execute(options);
		mongopushRunner.waitForInitialSyncComplete();
		
		assertFalse("mongopush process failed", mongopushRunner.isProcessFailed());
		assertTrue(mongopushRunner.isInitialSyncComplete());
		mongopushRunner.shutdown();
		
		diffUtil.diff();
	}

	@Test
	void testIncludes() throws ExecuteException, IOException {
		MongopushOptions options = MongopushOptions.builder()
				.mode(MongopushMode.PUSH_DATA)
				.includeNamespace("foo.bar")
				.includeNamespace("foo.bar2")
				.build();
		MongopushRunner mongopushRunner = context.getBean(MongopushRunner.class);
		mongopushRunner.execute(options);
		mongopushRunner.waitForInitialSyncComplete();
		
		assertFalse("mongopush process failed", mongopushRunner.isProcessFailed());
		assertTrue(mongopushRunner.isInitialSyncComplete());
		
		
		mongopushRunner.shutdown();
		
	}

}
