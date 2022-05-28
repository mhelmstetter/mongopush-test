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
	
	@BeforeEach
	public void beforeEach() {
		
		targetTestClient.dropAllDatabases();
	}
	

	
	@Test
	void testInitialSyncOnly() throws ExecuteException, IOException {
		
		sourceTestClient.dropAllDatabases();
		sourceTestClient.populateData(2, 2, 10);
		MongopushOptions options = MongopushOptions.builder().mode(MongopushMode.PUSH_DATA).build();
		MongopushRunner mongopushRunner = context.getBean(MongopushRunner.class);
		mongopushRunner.execute(options);
		mongopushRunner.waitForInitialSyncComplete();
		
		assertFalse("mongopush process failed", mongopushRunner.isProcessFailed());
		assertTrue(mongopushRunner.isInitialSyncComplete());
		mongopushRunner.shutdown();
		
		DiffSummary ds = diffUtil.diff();
		assertDiffResults(ds, 2, 2, 10);
	}

	@Test
	void testIncludes() throws ExecuteException, IOException {
		
		// note, data from previous test still exists, add more
		sourceTestClient.populateData(1, 1, 20000, 999);
		Set<Namespace> includeNamespaces = new HashSet<>();
		includeNamespaces.add(new Namespace("d0.c0"));
		includeNamespaces.add(new Namespace("d1.c1"));
		
		MongopushOptions options = MongopushOptions.builder()
				.mode(MongopushMode.PUSH_DATA)
				.includeNamespace("d0.c0")
				.includeNamespace("d1.c1")
				.build();
		MongopushRunner mongopushRunner = context.getBean(MongopushRunner.class);
		mongopushRunner.execute(options);
		mongopushRunner.waitForInitialSyncComplete();
		
		assertFalse("mongopush process failed", mongopushRunner.isProcessFailed());
		assertTrue(mongopushRunner.isInitialSyncComplete());
		
		
		mongopushRunner.shutdown();
		
		DiffSummary ds = diffUtil.diff(includeNamespaces);
		//assertDiffResults(ds);
	}
	
	private static void assertDiffResults(DiffSummary ds, int numDbs, int collectionsPerDb, int docsPerCollection) {
		assertEquals(0, ds.missingDbs);
		assertEquals(0, ds.totalMissingDocs);
		assertEquals(0, ds.totalKeysMisordered);
		assertEquals(0, ds.totalHashMismatched);
		
		assertEquals(numDbs, ds.totalDbs);
		assertEquals(numDbs*collectionsPerDb, ds.totalCollections);
		assertEquals(numDbs*collectionsPerDb*docsPerCollection, ds.totalMatches);
	}

}
