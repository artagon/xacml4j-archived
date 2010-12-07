package com.artagon.xacml.v3.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.w3c.dom.Node;

import com.artagon.xacml.v3.AttributeCategory;
import com.artagon.xacml.v3.AttributeValue;
import com.artagon.xacml.v3.AttributeValueType;
import com.artagon.xacml.v3.XacmlObject;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

public class Attributes extends XacmlObject
{
	private String id;
	private AttributeCategory categoryId;
	private Node content;
	private Multimap<String, Attribute> attributes;
	
	/**
	 * Constructs an attributes with a given identifier,
	 * category, attributes and XML content.
	 * XML content is deep copied from a given
	 * content node
	 * 
	 * @param id an optional unique identifier
	 * @param categoryId an attribute category
	 * @param content an optional XML content
	 * @param attributes a collection of attributes
	 */
	public Attributes(
			String id, 
			AttributeCategory categoryId, 
			Node content, 
			Iterable<Attribute> attrs){
		Preconditions.checkNotNull(categoryId);
		Preconditions.checkNotNull(attrs);
		this.id = id;
		this.categoryId = categoryId;
		this.content = content;
		this.attributes = LinkedListMultimap.create();
		for(Attribute attr : attrs){
			this.attributes.put(attr.getAttributeId(), attr);
		}
	}
	
	/** 
	 * @see {@link #Attributes(String, AttributeCategory, Node, Collection)
	 */
	public Attributes(String id, AttributeCategory categoryId, 
			Node content, Attribute ...attributes){
		this(id, categoryId, content, Arrays.asList(attributes));
	}
	
	/** 
	 * @see {@link #Attributes(String, AttributeCategory, Node, Collection)
	 */
	public Attributes(AttributeCategory categoryId, 
			Iterable<Attribute> attributes){
		this(null, categoryId, null, attributes);
	}
	
	public Attributes(AttributeCategory categoryId, 
			Node content,
			Iterable<Attribute> attributes){
		this(null, categoryId, content, attributes);
	}
	
	/** 
	 * @see {@link #Attributes(String, AttributeCategory, Node, Collection)
	 */
	public Attributes(String id, 
			AttributeCategory categoryId, 
			Iterable<Attribute> attributes){
		this(id, categoryId, null, attributes);
	}
	
	/** 
	 * @see {@link #Attributes(String, AttributeCategory, Node, Collection)
	 */
	public Attributes(String id, AttributeCategory categoryId, 
			Attribute ...attributes){
		this(id, categoryId, null, Arrays.asList(attributes));
	}
	
	/** 
	 * @see {@link #Attributes(String, AttributeCategory, Node, Collection)
	 */
	public Attributes(AttributeCategory categoryId, 
			Attribute ...attributes){
		this(null, categoryId, null, Arrays.asList(attributes));
	}
	
	/**
	 * An unique identifier of the attribute in
	 * the request context
	 * 
	 * @return unique identifier of the
	 * attribute in the request context
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Gets content as {@link Node}
	 * instance
	 * 
	 * @return a {@link Node} instance
	 * or <code>null</code>
	 */
	public Node getContent(){
		return content;
	}
	
	/**
	 * Gets an attribute category
	 * 
	 * @return attribute category
	 */
	public AttributeCategory getCategory(){
		return categoryId;
	}

	/**
	 * Tests if this instance contains an
	 * attribute with a given identifier
	 * 
	 * @param attributeId an attribute id
	 * @return <code>true</code> if contains
	 */
	public boolean containsAttribute(String attributeId){
		return attributes.containsKey(attributeId);
	}
	
	/**
	 * Gets all attributes with a given identifier
	 * 
	 * @param attributeId an attribute identifier
	 * @return a collection of attributes if there is no
	 * attributes with a given identifier empty collection
	 * is returned
	 */
	public Collection<Attribute> getAttributes(String attributeId){
		return  Collections.unmodifiableCollection(attributes.get(attributeId));
	}
	
	/**
	 * Gets a single {@link Attribute} instance with
	 * a given attribute identifier
	 * 
	 * @param attributeId an attribute identifier
	 * @return {@link Attribute} instance or <code>null</code>
	 * if no attribute available with a given identifier
	 */
	public Attribute getOnlyAttribute(String attributeId){
		return Iterables.getOnlyElement(attributes.get(attributeId), null);
	}
	
	/**
	 * Gets all attributes with a given attribute 
	 * identifier and issuer
	 * 
	 * @param attributeId an attribute identifier
	 * @param issuer an attribute issuer
	 * @return a collection of attributes with a given identifier
	 * and given issuer
	 */
	public Collection<Attribute> getAttributes(final String attributeId, final String issuer){
		Collection<Attribute> v = attributes.get(attributeId);
		return Collections.unmodifiableCollection(
				Collections2.filter(v, new Predicate<Attribute>() {
					@Override
					public boolean apply(Attribute attr) {
						return issuer == null || 
						issuer.equals(attr.getIssuer());
					}}));	
	}
	
	/**
	 * Gets all attribute {@Link Attribute}
	 * instances
	 * 
	 * @return immutable collection of {@link Attribute}
	 * instances
	 */
	public Collection<Attribute> getAttributes() {
		return Collections.unmodifiableCollection(attributes.values());
	}
	
	/**
	 * Finds all instance of {@Link Attribute} with
	 * {@link Attribute#isIncludeInResult()} returning
	 * <code>true</code>
	 * 
	 * @return a collection of {@link Attribute}
	 * instances
	 */
	public Collection<Attribute> getIncludeInResultAttributes(){
		return Collections.unmodifiableCollection(
				Collections2.filter(attributes.values(), new Predicate<Attribute>() {
				@Override
				public boolean apply(Attribute attr) {
					return attr.isIncludeInResult();
				}
			}));	
	}
	
	/**
	 * @see {@link Attributes#getAttributeValues(String, String, AttributeValueType)
	 */
	public Collection<AttributeValue> getAttributeValues(
			String attributeId, 
			AttributeValueType dataType){
		return getAttributeValues(attributeId, null, dataType);
	}
	
	/**
	 * Gets only one {@link AttributeValue} instance of the given type 
	 * from an attribute with a given identifier
	 * 
	 * @param attributeId an attribute identifier
	 * @param dataType an attribute value data type
	 * @return {@link AttributeValue} of the given type or <code>null</code>
	 * if value matching given criteria does not exist
	 * @exception IllegalArgumentException if more than one value is found
	 * matching given criteria
	 */
	public AttributeValue getOnlyAttributeValue(String attributeId, AttributeValueType dataType){
		return Iterables.getOnlyElement(getAttributeValues(attributeId, dataType), null);
	}
	
	/**
	 * Gets all {@link AttributeValue} instances
	 * contained in this attributes instance
	 * 
	 * @param attributeId an attribute id
	 * @param issuer an attribute issuer
	 * @param type an attribute value data type
	 * @return a collection of {@link AttributeValue} instances
	 */
	public Collection<AttributeValue> getAttributeValues(
			String attributeId, String issuer, final AttributeValueType type){
		Preconditions.checkNotNull(type);
		Collection<Attribute> found = getAttributes(attributeId, issuer);
		Collection<AttributeValue> values = new LinkedList<AttributeValue>();
		for(Attribute a : found){
			values.addAll(a.getValuesByType(type));
		}
		return values;
	}
	
	@Override
	public String toString(){
		return Objects.toStringHelper(this).add("Category", categoryId)
		.add("ID", id)
		.add("Content", content)
		.addValue(attributes.values()).toString();
	}
	
	@Override
	public int hashCode(){
		return Objects.hashCode(categoryId, id, content, attributes);
	}
	
	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
		}
		if(o == null){
			return false;
		}
		if(!(o instanceof Attributes)){
			return false;
		}
		Attributes a = (Attributes)o;
		return Objects.equal(categoryId, a.categoryId) &&
		Objects.equal(id, a.id) && 
		(content != null?content.isEqualNode(a.content):(a.content == null)) &&
		Objects.equal(attributes, a.attributes);
	}
}