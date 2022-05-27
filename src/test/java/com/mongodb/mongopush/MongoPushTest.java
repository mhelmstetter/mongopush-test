package com.mongodb.mongopush;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.exec.ExecuteException;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.mongodb.diffutil.DiffSummary;
import com.mongodb.model.Namespace;
import com.mongodb.mongopush.diff.DiffUtilRunner;
import com.mongodb.test.MongoTestClient;

@ExtendWith({SpringExtension.class})
@TestPropertySource("/test.properties")
@ContextConfiguration(locations = "/test-context.xml")
@SpringJUnitConfig
class MongoPushTest {
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	DiffUtilRunner diffUtil;
	
	@Autowired
	MongoTestClient sourceTestClient;
	
	@Autowired
	MongoTestClient targetTestClient;
	
	private static boolean sourceInitialized = false;
	
	
	@BeforeEach
	public void beforeEach() {
		if (!sourceInitialized) {
			sourceTestClient.dropAllDatabases();
			sourceTestClient.populateData(2, 2, 10);
			sourceInitialized = true;
		}
		targetTestClient.dropAllDatabases();
	}
	

	
	@Test
	void testInitialSyncOnly() throws ExecuteException, IOException {
		
		MongopushOptions options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).build();
		MongopushRunner mongopushRunner = context.getBean(MongopushRunner.class);
		mongopushRunner.execute(options);
		mongopushRunner.waitForInitialSyncComplete();
		
		assertFalse("mongopush process failed", mongopushRunner.isProcessFailed());
		assertTrue(mongopushRunner.isInitialSyncComplete());
		mongopushRunner.shutdown();
		
		DiffSummary ds = diffUtil.diff();
		assertDiffResults(ds);
	}

	@Test
	void testIncludes() throws ExecuteException, IOException {
		
		Set<Namespace> includeNamespaces = new HashSet<>();
		includeNamespaces.add(new Namespace("foo.bar"));
		includeNamespaces.add(new Namespace("foo.bar2"));
		
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
		
		DiffSummary ds = diffUtil.diff(includeNamespaces);
		assertDiffResults(ds);
	}
	
	private static void assertDiffResults(DiffSummary ds) {
		assertEquals(0, ds.missingDbs);
		assertEquals(0, ds.totalMissingDocs);
		assertEquals(0, ds.totalKeysMisordered);
		assertEquals(0, ds.totalHashMismatched);
	}

}
