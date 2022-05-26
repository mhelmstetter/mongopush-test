package com.mongodb.mongopush;

public enum MongopushMode {
	
	PUSH_DATA("data");
	
	private String name;
	
	MongopushMode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
