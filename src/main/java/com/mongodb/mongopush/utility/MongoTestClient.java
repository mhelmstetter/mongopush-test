package com.mongodb.mongopush.utility;

import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.mongopush.constants.MongoPushConstants.ADMIN;
import static com.mongodb.mongopush.constants.MongoPushConstants.CONFIG;
import static com.mongodb.mongopush.constants.MongoPushConstants.LOCAL;
import static com.mongodb.mongopush.constants.MongoPushConstants.UNIQUE_FIELD;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.bson.BsonUndefined;
import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.conversions.Bson;
import org.bson.types.Binary;
import org.bson.types.Decimal128;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
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
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

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
	
	public void populateData(int numDbs, int collectionsPerDb, int docsPerCollection, boolean uniqueIndex) {
		List<Document> docsBuffer = new ArrayList<>(docsPerCollection);
		for (int dbNum = 0; dbNum < numDbs; dbNum++) {
			String dbName = "db" + dbNum;
			if(uniqueIndex)
			{
				dbName += "unique";
			}
			MongoDatabase db = mongoClient.getDatabase(dbName);
			for (int collNum = 0; collNum < collectionsPerDb; collNum++) {
				String collName = "c" + collNum;
				MongoCollection<Document> coll = db.getCollection(collName);
				
				if(uniqueIndex)
				{
					IndexOptions indexOptions = new IndexOptions().unique(true);
				    String resultCreateIndex = coll.createIndex(Indexes.ascending(UNIQUE_FIELD), indexOptions);
				    logger.info(String.format("Unique index created: {}", resultCreateIndex));
				}
				
				for (int docNum = 0; docNum < docsPerCollection; docNum++) {
					docsBuffer.add(createDocument(docNum, false));
				}
				coll.insertMany(docsBuffer);
				docsBuffer.clear();
			}
		}
	}
	
	public void populateDataForDatabase(String dbName, String collName, int docsPerCollection, boolean idAsDocument) {
		List<Document> docsBuffer = new ArrayList<>(docsPerCollection);
			MongoDatabase db = mongoClient.getDatabase(dbName);
				MongoCollection<Document> coll = db.getCollection(collName);
				for (int docNum = 0; docNum < docsPerCollection; docNum++) {
					docsBuffer.add(createDocument(docNum, idAsDocument));
				}
				coll.insertMany(docsBuffer);
				docsBuffer.clear();
	}
	
	public long replaceDocuments(String dbName, String collName, String filter)
	{
		MongoDatabase mongoDatabase = mongoClient.getDatabase(dbName);
		MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collName);
		Bson query = Document.parse(filter);
        Bson updates = Updates.combine( Updates.addToSet("replaced_document", "Recently replaced"),Updates.currentTimestamp("lastUpdated"));
        UpdateResult result = mongoCollection.updateMany(query, updates);
        return result.getModifiedCount();
	}
	
	private Document createDocument(int docNum, boolean idAsDocument)
	{
		Document document = new Document();
		UUID uuid = UUID.randomUUID();
		if(idAsDocument)
		{
			Document idDocument = new Document();
			idDocument.append("uuid", uuid);
			idDocument.append("created", fakerService.getRandomDate());
			document.append("_id", idDocument);
		}
		else
		{
			document.append("_id", uuid.toString());
		}
		document.append(UNIQUE_FIELD, docNum);
		addDocumentFields(document);
		return document;
	}
	
	private void addDocumentFields(Document document)
	{
		Random random = new Random();
		document.append("fld0", random.nextLong());
		document.append("fld1", fakerService.getRandomDate());
		document.append("fld2", "sit amet. Lorem ipsum dolor");
		document.append("fld3", fakerService.getRandomText());
		document.append("fld4", fakerService.getRandomText());
		document.append("fld5", fakerService.getRandomDate());
		document.append("long_field", random.nextLong());
		document.append("boolean_field", random.nextBoolean());
		document.append("double_field", random.nextDouble());
		document.append("document_field", new Document("uuid", UUID.randomUUID()));
		String[] cars_array = {"Volvo", "BMW", "Honda"};
		document.append("array_field", Arrays.asList(cars_array));
		
		Map<String, String> documentRandomMap = new HashMap<String, String>();
		documentRandomMap.put("text_1", fakerService.getRandomText());
		documentRandomMap.put("text_2", fakerService.getRandomText());
		document.append("map_field", documentRandomMap);
		document.append("binary_field", new Binary(fakerService.getRandomText().getBytes()));
		document.append("objectId_field", new ObjectId());
		document.append("undefined_field", new BsonUndefined());
		document.append("null_field", null);
		document.append("regex_field", Pattern.compile("^.* random regex (.*)"));
		document.append("integer_field", random.nextInt());
		document.append("timestamp_field", new Timestamp(new Date().getTime()));
		document.append("decimal128_field", new Decimal128(random.nextLong()));
		document.append("minkey_field", new MinKey());
		document.append("maxkey_field", new MaxKey());
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
