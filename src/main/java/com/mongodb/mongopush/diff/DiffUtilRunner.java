package com.mongodb.mongopush.diff;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.diffutil.DiffSummary;
import com.mongodb.diffutil.DiffUtil;
import com.mongodb.model.Namespace;

@Component
public class DiffUtilRunner {
	
	@Value("${source}")
	private String source;
	
	@Value("${target}")
	private String target;
	
	
	public DiffSummary diff() {
		return diff(null);
	}
	
	public DiffSummary diff(Set<Namespace> includeNamespaces) {
		DiffUtil sync = new DiffUtil();
		sync.setIncludedNamespaces(includeNamespaces);
        sync.setSourceClusterUri(source);
        sync.setDestClusterUri(target);
        sync.init();
        return sync.compareDocuments(true);
	}
	
	

}
