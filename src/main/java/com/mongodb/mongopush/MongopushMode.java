package com.mongodb.mongopush;

public enum MongopushMode {
	
	PUSH_DATA("data"),
	PUSH_DATA_ONLY("data-only"),
	VERIFY("verify"),
	REFETCH("refetch");
	
	private String name;
	
	MongopushMode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
