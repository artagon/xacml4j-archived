package com.artagon.xacml.v30.spi.pip;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.artagon.xacml.v30.AttributeCategory;
import com.artagon.xacml.v30.AttributeDesignatorKey;
import com.artagon.xacml.v30.AttributeReferenceKey;
import com.artagon.xacml.v30.AttributeSelectorKey;
import com.artagon.xacml.v30.AttributeValueType;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

public final class AttributeResolverDescriptorBuilder
{
	private String id;
	private String name;
	private AttributeCategory category;
	private Map<String, AttributeDescriptor> attributes;
	private String issuer;
	private List<AttributeReferenceKey> keys;
	private int preferredCacheTTL = 0;
	
	private AttributeResolverDescriptorBuilder(
			String id, 
			String name, 
			String issuer, 
			AttributeCategory category){
		Preconditions.checkNotNull(id);
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(category);
		this.name = name;
		this.issuer = issuer;
		this.id = id;
		this.category = category;
		this.attributes = new HashMap<String, AttributeDescriptor>();
		this.keys = new LinkedList<AttributeReferenceKey>();
	}
	
	public static AttributeResolverDescriptorBuilder builder(String id, 
			String name, AttributeCategory category){
		return builder(id, name, null, category);
	}
	
	public static AttributeResolverDescriptorBuilder builder(String id, 
			String name, String issuer, AttributeCategory category){
		return new AttributeResolverDescriptorBuilder(id, name, Strings.emptyToNull(issuer), category);
	}
	
	public AttributeResolverDescriptorBuilder designatorRef(AttributeCategory category, 
			String attributeId, AttributeValueType dataType, String issuer)
	{
		this.keys.add(new AttributeDesignatorKey(
				category, attributeId, dataType, Strings.emptyToNull(issuer)));
		return this;
	}
	
	public AttributeResolverDescriptorBuilder selectorRef(
			AttributeCategory category, 
			String xpath, AttributeValueType dataType, 
			String contextAttributeId)
	{
		this.keys.add(new AttributeSelectorKey(
				category, xpath, dataType, Strings.emptyToNull(contextAttributeId)));
		return this;
	}
	
	public AttributeResolverDescriptorBuilder keys(Iterable<AttributeReferenceKey> keys){
		Iterables.addAll(this.keys, keys);
		return this;
	}
	
	public AttributeResolverDescriptorBuilder noCache(){
		this.preferredCacheTTL = -1;
		return this;
	}
	
	public AttributeResolverDescriptorBuilder cache(int ttl){
		this.preferredCacheTTL = ttl;
		return this;
	}
		
	public AttributeResolverDescriptorBuilder attribute(
			String attributeId, 
			AttributeValueType dataType){
		AttributeDescriptor old = attributes.put(attributeId, new AttributeDescriptor(attributeId, dataType));
		Preconditions.checkState(old == null, 
				"Builder already has an attribute with id=\"%s\"", attributeId);
		return this;
	}
		
	public AttributeResolverDescriptor build(){
		return new AttributeResolverDescriptorImpl();
	}

	private class AttributeResolverDescriptorImpl extends BaseResolverDescriptor
			implements AttributeResolverDescriptor 
	{
		private Map<String, AttributeDescriptor> attributes;
		
		AttributeResolverDescriptorImpl() {
			super(id, name, category, keys, preferredCacheTTL);
			this.attributes = ImmutableMap.copyOf(AttributeResolverDescriptorBuilder.this.attributes);
		}
		
		@Override
		public String getIssuer() {
			return issuer;
		}
		
		@Override
		public int getAttributesCount(){
			return attributes.size();
		}
		
		public Set<String> getProvidedAttributeIds(){
			return attributes.keySet();
		}

		@Override
		public Map<String, AttributeDescriptor> getAttributes() {
			return attributes;
		}

		@Override
		public AttributeDescriptor getAttribute(String attributeId) {
			return attributes.get(attributeId);
		}

		@Override
		public boolean isAttributeProvided(String attributeId) {
			return attributes.containsKey(attributeId);
		}
		
		@Override
		public boolean canResolve(AttributeDesignatorKey ref)
		{
			if(getCategory().equals(ref.getCategory()) && 
					((ref.getIssuer() != null)?ref.getIssuer().equals(getIssuer()):true)){
				AttributeDescriptor d = getAttribute(ref.getAttributeId());
				return (d != null)?d.getDataType().equals(ref.getDataType()):false;
			}
			return false;
		}
	}
}
