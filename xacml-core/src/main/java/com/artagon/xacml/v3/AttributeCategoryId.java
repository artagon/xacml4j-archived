package com.artagon.xacml.v3;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum AttributeCategoryId
{ 
	ACTION("urn:oasis:names:tc:xacml:3.0:attribute-category:action"),
	ENVIRONMENT("urn:oasis:names:tc:xacml:3.0:attribute-category:environment"),
	OBLIGATION("urn:oasis:names:tc:xacml:3.0:attribute-category:obligation"),
	RESOURCE("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"),
	STATUSDETAIL("urn:oasis:names:tc:xacml:3.0:attribute-category:status-detail"),
	SUBJECT_ACCESS("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"),
	SUBJECT_CODEBASE("urn:oasis:names:tc:xacml:1.0:subject-category:codebase"),
	SUBJECT_INTERMEDIARY("urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject"),
	SUBJECT_RECIPIENT("urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject"),
	SUBJECT_REQUESTING_MACHINE("urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine");
	
	private String id;
	
	private static final Map<String, AttributeCategoryId> BY_ID = new ConcurrentHashMap<String, AttributeCategoryId>();

	static {
		for(AttributeCategoryId t : EnumSet.allOf(AttributeCategoryId.class)){
			BY_ID.put(t.id, t);
		}
	}
	
	private AttributeCategoryId(String id){
		this.id = id;
	}
	
	public static AttributeCategoryId parse(String v){
		return BY_ID.get(v);
	}
	
	@Override
	public String toString(){
		return id;
	}
}