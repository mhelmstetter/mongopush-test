package com.mongodb.test;

import org.bson.UuidRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.util.MaskUtil;

public class MongoTestClient {
	
	private static Logger logger = LoggerFactory.getLogger(MongoTestClient.class);
	
	private String name;
	private ConnectionString connectionString;

	private MongoClientSettings mongoClientSettings;

	private MongoClient mongoClient;
	
	public MongoTestClient(String name, String clusterUri) {
		this.name = name;
		this.connectionString = new ConnectionString(clusterUri);
		
		logger.debug(String.format("%s client, uri: %s", name, MaskUtil.maskConnectionString(connectionString)));
		
	}
	
	public void init() {
		mongoClientSettings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.uuidRepresentation(UuidRepresentation.STANDARD)
				.build();
		mongoClient = MongoClients.create(mongoClientSettings);
	}

}
