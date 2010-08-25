package com.artagon.xacml.spring.domain;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import com.artagon.xacml.v3.PolicySetIDReference;

public class PolicySetIDReferenceFactoryBean extends AbstractFactoryBean
{
	private String id;
	private String version;
	private String earliest;
	private String latest;
	
	public void setId(String id){
		this.id  = id;
	}
	
	public void setEarliest(String earliest){
		this.earliest = earliest;
	}
	
	public void setVersion(String version){
		this.version = version;
	}
	
	public void setLatest(String latest){
		this.latest = latest;
	}
	
	@Override
	public Class<PolicySetIDReference> getObjectType() {
		return PolicySetIDReference.class;
	}
	
	@Override
	protected Object createInstance() throws Exception 
	{
		return PolicySetIDReference.create(id, version, earliest, latest);
	}
	
	
}