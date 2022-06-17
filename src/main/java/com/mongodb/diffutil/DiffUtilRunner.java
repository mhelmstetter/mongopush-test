package com.mongodb.diffutil;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mongodb.model.Namespace;
import com.mongodb.mongopush.config.MongoPushConfiguration;

@Component
public class DiffUtilRunner {
	
	@Autowired
	MongoPushConfiguration mongoPushConfiguration;
	
	public DiffSummary diff() {
		return diff(null);
	}
	
	public DiffSummary diff(Set<Namespace> includeNamespaces) {
		DiffUtil sync = new DiffUtil();
		sync.setIncludedNamespaces(includeNamespaces);
        sync.setSourceClusterUri(mongoPushConfiguration.getMongopushSource());
        sync.setDestClusterUri(mongoPushConfiguration.getMongopushTarget());
        sync.init();
        return sync.compareDocuments(true);
	}
	
	

}
