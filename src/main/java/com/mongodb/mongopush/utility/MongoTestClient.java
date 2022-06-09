package com.mongodb.mongopush.utility;

import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.mongopush.constants.MongoPushConstants.ADMIN;
import static com.mongodb.mongopush.constants.MongoPushConstants.CONFIG;
import static com.mongodb.mongopush.constants.MongoPushConstants.LOCAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

public class MongoTestClient {
	
	@Autowired
	private FakerService fakerService;
	
	private static Logger logger = LoggerFactory.getLogger(MongoTestClient.class);
	
	private String name;
	private ConnectionString connectionString;

	private MongoClientSettings mongoClientSettings;
	private List<String> databaseNameList;
	private List<String> databasesNotToDelete = Arrays.asList(ADMIN, CONFIG, LOCAL);;

	private MongoClient mongoClient;
	
	public MongoTestClient(String name, String clusterUri) {
		this.name = name;
		this.connectionString = new ConnectionString(clusterUri);
		//logger.debug(String.format("%s client, uri: %s", name, MaskUtil.maskConnectionString(connectionString)));
	}

	public ConnectionString getConnectionString() {
		return connectionString;
	}

	@PostConstruct
	public void init() {
		mongoClientSettings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.uuidRepresentation(UuidRepresentation.STANDARD)
				.build();
		mongoClient = MongoClients.create(mongoClientSettings);
	}
	
	public void populateData(int numDbs, int collectionsPerDb, int docsPerCollection) {
		List<Document> docsBuffer = new ArrayList<>(docsPerCollection);
		for (int dbNum = 0; dbNum < numDbs; dbNum++) {
			String dbName = "db" + dbNum;
			MongoDatabase db = mongoClient.getDatabase(dbName);
			for (int collNum = 0; collNum < collectionsPerDb; collNum++) {
				String collName = "c" + collNum;
				MongoCollection<Document> coll = db.getCollection(collName);
				for (int docNum = 0; docNum < docsPerCollection; docNum++) {
					docsBuffer.add(createDocument());
				}
				coll.insertMany(docsBuffer);
				docsBuffer.clear();
			}
		}
	}
	
	public void populateDataForDatabase(String dbName, String collName, int docsPerCollection) {
		List<Document> docsBuffer = new ArrayList<>(docsPerCollection);
			MongoDatabase db = mongoClient.getDatabase(dbName);
				MongoCollection<Document> coll = db.getCollection(collName);
				for (int docNum = 0; docNum < docsPerCollection; docNum++) {
					docsBuffer.add(createDocument());
				}
				coll.insertMany(docsBuffer);
				docsBuffer.clear();
	}
	
	private Document createDocument()
	{
		UUID uuid = UUID.randomUUID();
		Random random = new Random();
		Document document = new Document("_id", uuid.toString());
		document.append("fld0", random.nextLong());
		document.append("fld1", fakerService.getRandomDate());
		document.append("fld2", "sit amet. Lorem ipsum dolor");
		document.append("fld3", fakerService.getRandomText());
		document.append("fld4", fakerService.getRandomText());
		document.append("fld5", fakerService.getRandomDate());
		document.append("fld6", random.nextLong());
		document.append("fld7", fakerService.getRandomText());
		document.append("fld8", fakerService.getRandomText());
		document.append("fld9", random.nextLong());
		
		return document;
	}
	
	public List<String> getAllDatabases() {
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
			if (!dbName.equals("admin")) {
				logger.debug(name + " dropping " + dbName);
				try {
					mongoClient.getDatabase(dbName).drop();
				} catch (MongoCommandException mce) {
					logger.warn("Drop failed, brute forcing.", mce);
					dropForce(dbName);
				}

			}
		}
	}
	
	private void dropForce(String dbName) {
		DeleteResult r = mongoClient.getDatabase("config").getCollection("collections")
				.deleteMany(regex("_id", "^" + dbName + "\\."));
		logger.debug(String.format("Force deleted %s config.collections documents", r.getDeletedCount()));
		r = mongoClient.getDatabase("config").getCollection("chunks").deleteMany(regex("ns", "^" + dbName + "\\."));
		logger.debug(String.format("Force deleted %s config.chunks documents", r.getDeletedCount()));
	}
	
	public List<String> getAllDatabaseNames(){
		databaseNameList = new ArrayList<String>();
	    MongoCursor<String> dbsCursor = mongoClient.listDatabaseNames().iterator();
	    while(dbsCursor.hasNext()) {
	    	databaseNameList.add(dbsCursor.next());
	    }
	    return databaseNameList;
	}
	
	public void dropAllDatabasesByName() {
		for (String databaseName : getAllDatabaseNames()) {
			if (!databasesNotToDelete.contains(databaseName)) {
				logger.debug(name + " dropping " + databaseName);
				dropDatabase(databaseName);
			}
		}
	}
	
	public void dropDatabase(String databaseName) {
		
		MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
		if(mongoDatabase != null && mongoDatabase.getName() != null)
		{
			logger.info("MongoDB Database found - {}", databaseName);
			mongoDatabase.drop();
		}
	}
	
}
