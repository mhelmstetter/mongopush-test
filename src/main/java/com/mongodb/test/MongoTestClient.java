package com.mongodb.test;

import static com.mongodb.client.model.Filters.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCommandException;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.test.faker.FakerService;
import com.mongodb.util.MaskUtil;

public class MongoTestClient {
	
	@Autowired
	FakerService fakerService;
	
	private static Logger logger = LoggerFactory.getLogger(MongoTestClient.class);
	
	private String name;
	private ConnectionString connectionString;

	private MongoClientSettings mongoClientSettings;

	private MongoClient mongoClient;
	
	// are we connecting to mongos or mongod
	private boolean mongos;
	
	public MongoTestClient(String name, String clusterUri) {
		this.name = name;
		this.connectionString = new ConnectionString(clusterUri);
		logger.debug(String.format("%s client, uri: %s", name, MaskUtil.maskConnectionString(connectionString)));
	}
	
	@PostConstruct
	public void init() {
		mongoClientSettings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.uuidRepresentation(UuidRepresentation.STANDARD)
				.build();
		mongoClient = MongoClients.create(mongoClientSettings);
		
		try {
			Document dbgridResult = adminCommand(new Document("isdbgrid", 1));
			Integer dbgrid = dbgridResult.getInteger("isdbgrid");
			mongos = dbgrid.equals(1);
		} catch (MongoException mce) {
			// ignore not supported
		}
	}
	
	public void populateData(int numDbs, int collectionsPerDb, int docsPerCollection) {
		populateData(numDbs, collectionsPerDb, docsPerCollection, 0);
	}
	
	public void populateData(int numDbs, int collectionsPerDb, int docsPerCollection, int startDocNum) {
		List<Document> docsBuffer = new ArrayList<>(docsPerCollection);
		for (int dbNum = 0; dbNum < numDbs; dbNum++) {
			String dbName = "db" + dbNum;
			MongoDatabase db = mongoClient.getDatabase(dbName);
			for (int collNum = 0; collNum < collectionsPerDb; collNum++) {
				String collName = "c" + collNum;
				MongoCollection<Document> coll = db.getCollection(collName);
				for (int docNum = startDocNum; docNum < docsPerCollection+startDocNum; docNum++) {
					Document d = new Document("_id", docNum);
					d.append("uuid", UUID.randomUUID());
					d.append("startDate", fakerService.getRandomDate());
					d.append("endDate", fakerService.getRandomDate());
					docsBuffer.add(d);
				}
				coll.insertMany(docsBuffer);
				docsBuffer.clear();
			}
		}
	}
	
	public List<String> getAllDatabases() {
		if (mongos) {
			return getAllDatabasesSharded();
		} else {
			List<String> dbNames = new ArrayList<>();
			mongoClient.listDatabaseNames().into(dbNames);
			return dbNames;
		}
	}
	
	// TODO - this assumes sharded
	public List<String> getAllDatabasesSharded() {
		MongoCollection<Document> databasesColl = mongoClient.getDatabase("config").getCollection("databases");
		FindIterable<Document> databases = databasesColl.find();
		List<String> databasesList = new ArrayList<String>();

		for (Document database : databases) {
			String databaseName = database.getString("_id");
			databasesList.add(databaseName);
		}
		return databasesList;
	}
	
	public void dropAllDatabases() {
		for (String dbName : getAllDatabases()) {
			if (!dbName.equals("admin") && !dbName.equals("config") && !dbName.equals("local")) {
				logger.debug(name + " dropping " + dbName);
				try {
					mongoClient.getDatabase(dbName).drop();
				} catch (MongoCommandException mce) {
					logger.error("Drop failed, brute forcing.", mce);
					dropForce(dbName);
				}

			}
		}
	}
	
	public Document adminCommand(Document command) {
		return mongoClient.getDatabase("admin").runCommand(command);
	}
	
	private void dropForce(String dbName) {
		DeleteResult r = mongoClient.getDatabase("config").getCollection("collections")
				.deleteMany(regex("_id", "^" + dbName + "\\."));
		logger.debug(String.format("Force deleted %s config.collections documents", r.getDeletedCount()));
		r = mongoClient.getDatabase("config").getCollection("chunks").deleteMany(regex("ns", "^" + dbName + "\\."));
		logger.debug(String.format("Force deleted %s config.chunks documents", r.getDeletedCount()));
	}

}
