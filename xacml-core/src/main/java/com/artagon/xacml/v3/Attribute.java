package com.artagon.xacml.v3;

import java.util.Collection;

import com.artagon.xacml.v3.policy.AttributeValue;

public class Attribute 
{
	private String attributeId;
	private Collection<AttributeValue> values;
	private boolean includeInResult;
	private String issuer;
	
	public String getAttributeId(){
		return attributeId;
	}
	
	public Iterable<AttributeValue> getValues(){
		return values;
	}
	
	public String getIssuer(){
		return issuer;
	}
	
	public boolean isIncludeInResult(){
		return includeInResult;
	}
}
