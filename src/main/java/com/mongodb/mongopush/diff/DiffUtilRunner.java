package com.mongodb.mongopush.diff;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.diffutil.DiffUtil;

@Component
public class DiffUtilRunner {
	
	@Value("${source}")
	private String source;
	
	@Value("${target}")
	private String target;
	
	
	public void diff() {
		DiffUtil sync = new DiffUtil();
        sync.setSourceClusterUri(source);
        sync.setDestClusterUri(target);
        sync.init();
        sync.compareDocuments(true);
	}
	
	

}
