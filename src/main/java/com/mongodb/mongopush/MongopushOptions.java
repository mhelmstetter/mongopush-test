package com.mongodb.mongopush;

import java.util.HashSet;
import java.util.Set;

import com.mongodb.model.Namespace;

public class MongopushOptions {
	
	private final MongopushMode mode;
	private final Set<IncludeOption> includeOptions;
	private final Set<Namespace> includeNamespaces;
	
	private MongopushOptions(final MongopushMode mode, Set<IncludeOption> includeOptions, Set<Namespace> includeNamespaces) {
		this.mode = mode;
		this.includeOptions = includeOptions;
		this.includeNamespaces = includeNamespaces;
	}
	
	public static final class IncludeOption {
		private String namespace;
		private String filter;
		private String to;
		
		public IncludeOption(String namespace) {
			this(namespace, null, null);
		}
		
		public IncludeOption(String namespace, String filter) {
			this(namespace, filter, null);
		}
		
		public IncludeOption(String namespace, String filter, String to) {
			this.namespace = namespace;
			this.filter = filter;
			this.to = to;
		}
		
		public String toJson() {
			StringBuilder sb = new StringBuilder();
			sb.append("{\"namespace\": \"");
			sb.append(namespace);
			sb.append("\"");
			if (filter != null) {
				sb.append(",\"filter\": ");
				if(!filter.startsWith("{"))
				{
					sb.append("\"");
				}
				sb.append(filter);
				if(!filter.endsWith("}"))
				{
					sb.append("\"");
				}
			}
			if (to != null) {
				sb.append(",\"to\": \"");
				sb.append(to);
				sb.append("\"");
			}
			sb.append("}");
			return sb.toString();
		}
 	}
	
	public static final class Builder {
		
		private MongopushMode mode;
		private final Set<IncludeOption> includeOptions = new HashSet<>();
		private final Set<Namespace> includeNamespaces = new HashSet<>();
		
		public Builder mode(MongopushMode mode) {
			this.mode = mode;
			return this;
		}
		
		public Builder includeNamespace(String namespace) {
			this.includeOptions.add(new IncludeOption(namespace));
			this.includeNamespaces.add(new Namespace(namespace));
			return this;
		}
		
		public Builder includeNamespace(String namespace, String filter) {
			this.includeOptions.add(new IncludeOption(namespace, filter));
			this.includeNamespaces.add(new Namespace(namespace));
			return this;
		}
		
		public Builder includeNamespace(IncludeOption[] includeOptions) {
			for(IncludeOption includeOption: includeOptions)
			{
				this.includeOptions.add(includeOption);
				this.includeNamespaces.add(new Namespace(includeOption.namespace));
			}
			return this;
		}
		
		public MongopushOptions build() {
			return new MongopushOptions(mode, includeOptions, includeNamespaces);
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



	public Set<Namespace> getIncludeNamespaces() {
		return includeNamespaces;
	}
	
	

}
