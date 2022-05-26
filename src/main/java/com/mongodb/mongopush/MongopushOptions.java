package com.mongodb.mongopush;

import java.util.HashSet;
import java.util.Set;

public class MongopushOptions {
	
	private final MongopushMode mode;
	private final Set<IncludeOption> includeOptions;
	
	
	
	private MongopushOptions(final MongopushMode mode, Set<IncludeOption> includeOptions) {
		this.mode = mode;
		this.includeOptions = includeOptions;
		
	}
	
	public static final class IncludeOption {
		private String namespace;
		private String filter;
		
		public IncludeOption(String namespace) {
			this(namespace, null);
		}
		
		public IncludeOption(String namespace, String filter) {
			this.namespace = namespace;
			this.filter = filter;
		}
		
		public String toJson() {
			StringBuilder sb = new StringBuilder();
			sb.append("{\"namespace\": \"");
			sb.append(namespace);
			sb.append("\"");
			if (filter != null) {
				sb.append("\"filter\": \"");
				sb.append(filter);
				sb.append("\"");
			}
			sb.append("}");
			return sb.toString();
		}
 	}
	
	public static final class Builder {
		
		private MongopushMode mode;
		private final Set<IncludeOption> includeOptions = new HashSet<>();
		
		public Builder mode(MongopushMode mode) {
			this.mode = mode;
			return this;
		}
		
		public Builder includeNamespace(String namespace) {
			this.includeOptions.add(new IncludeOption(namespace));
			return this;
		}
		
		public MongopushOptions build() {
			return new MongopushOptions(mode, includeOptions);
		}
		
	}
	
	
	
	public static Builder builder() {
        return new Builder();
    }



	public MongopushMode getMode() {
		return mode;
	}



	public Set<IncludeOption> getIncludeOptions() {
		return includeOptions;
	}
	
	

}
