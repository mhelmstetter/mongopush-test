package com.mongodb.mongopush.model;

public class ReplaceDataArgumentsModel {

	private String namespace;
	private String filter;
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
}
