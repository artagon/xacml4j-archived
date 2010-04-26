package com.artagon.xacml.v3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.artagon.xacml.util.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class DefaultRequest extends XacmlObject implements Request
{	
	private boolean returnPolicyIdList;
	private Multimap<AttributeCategoryId, Attributes> attributes;
	private Map<String, Attributes> attributesByXmlId;
	private Collection<RequestReference> multipleRequests;
	private Set<String> attributeIds;
	
	/**
	 * Constructs a request with a given attributes
	 * @param attributes
	 */
	public DefaultRequest(boolean returnPolicyIdList, 
			Iterable<Attributes> attributes, 
			Collection<RequestReference> requestReferences)
	{
		this.returnPolicyIdList = returnPolicyIdList;
		this.attributes = HashMultimap.create();
		this.attributeIds = new HashSet<String>();
		this.multipleRequests = new ArrayList<RequestReference>(requestReferences);
		this.attributesByXmlId = new HashMap<String, Attributes>();
		for(Attributes attr : attributes)
		{
			// index attributes by category
			this.attributes.put(attr.getCategoryId(), attr);
			this.attributeIds.addAll(attr.getProvidedAttributeIds());
			// index attributes
			// by id for fast lookup
			if(attr.getId() != null){
				this.attributesByXmlId.put(attr.getId(), attr);
			}
		}
	}
	
	/**
	 * Constructs a request with a given attributes
	 * 
	 * @param attributes a collection of {@link Attributes}
	 * instances
	 */
	public DefaultRequest(boolean returnPolicyIdList, 
			Collection<Attributes> attributes)
	{
		this(returnPolicyIdList, attributes, 
				Collections.<RequestReference>emptyList());
	}
	
	public boolean isReturnPolicyIdList(){
		return returnPolicyIdList;
	}
	
	public int getCategoryOccuriences(AttributeCategoryId category){
		Collection<Attributes> attr = attributes.get(category);
		return (attr == null)?0:attr.size();
	}
	
	public Set<String> getProvidedAttributeIdentifiers(){
		return Collections.unmodifiableSet(attributeIds);
	}
	
	public Collection<RequestReference> getRequestReferences(){
		return Collections.unmodifiableCollection(multipleRequests);
	}

	public boolean hasMultipleRequests(){
		return !multipleRequests.isEmpty();
	}
	
	public Set<AttributeCategoryId> getCategories(){
		return Collections.unmodifiableSet(attributes.keySet());
	}
	
	
	@Override
	public Collection<Attributes> getIncludeInResultAttributes() 
	{
		Collection<Attributes> resultAttr = new LinkedList<Attributes>();
		for(Attributes a : attributes.values()){
			Collection<Attribute> includeInResult =  a.getIncludeInResultAttributes();
			if(!includeInResult.isEmpty()){
				resultAttr.add(new Attributes(a.getCategoryId(), includeInResult));
			}
		}
		return resultAttr;
	}
	
	public Attributes getReferencedAttributes(AttributesReference reference){
		Preconditions.checkNotNull(reference);
		return attributesByXmlId.get(reference.getReferenceId());
	}
	
	public Collection<Attributes> getAttributes(AttributeCategoryId categoryId){
		Preconditions.checkNotNull(categoryId);
		Collection<Attributes> attr =  attributes.get(categoryId);
		return (attr == null)?Collections.<Attributes>emptyList():attr;
	}
	
	public Collection<Attributes> getAttributes(){
		return Collections.unmodifiableCollection(attributes.values());
	}
	
	
	public Collection<Attributes> getAttributes(
			final AttributeCategoryId categoryId, 
			final String attributeId)
	{
		Preconditions.checkNotNull(categoryId);
		Preconditions.checkNotNull(attributeId);
		 Collection<Attributes> attr = attributes.get(categoryId);
		 if(attr == null){
			 return Collections.emptyList();
		 }
		 return Collections2.filter(attr, new Predicate<Attributes>() {
			@Override
			public boolean apply(Attributes a) {
				return a.containsAttribute(attributeId);
			}
		});
	}
}