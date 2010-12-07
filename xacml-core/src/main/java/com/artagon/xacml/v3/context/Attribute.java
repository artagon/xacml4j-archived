package com.artagon.xacml.v3.context;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import com.artagon.xacml.v3.AttributeValue;
import com.artagon.xacml.v3.AttributeValueType;
import com.artagon.xacml.v3.XacmlObject;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;

/**
 * A XACML request context attribute
 * 
 * @author Giedrius Trumpickas
 */
public class Attribute extends XacmlObject
{
	private String attributeId;
	private Multiset<AttributeValue> values;
	private boolean includeInResult;
	private String issuer;
	
	/**
	 * Constructs attribute with given
	 * parameters
	 * 
	 * @param attributeId an attribute identifier
	 * @param issuer an attribute issuer
	 * @param includeInResult a flag indicating
	 * if attribute needs to be included in
	 * the result
	 * @param values a collection of 
	 * {@link AttributeValue} instances
	 */
	public Attribute(
			String attributeId,
			String issuer, 
			boolean includeInResult, 
			Collection<AttributeValue> values){
		Preconditions.checkNotNull(attributeId);
		Preconditions.checkNotNull(values);
		this.attributeId = attributeId;
		this.issuer = issuer;
		this.values = LinkedHashMultiset.create(values.size());
		this.values.addAll(values);
		this.includeInResult = includeInResult;
	}
	
	public Attribute(String attributeId,
			String issuer, 
			boolean includeInResult, 
			AttributeValue ...values){
		this(attributeId, issuer, 
				includeInResult, Arrays.asList(values));
	}
	
	public Attribute(String attributeId, 
			Collection<AttributeValue> values){
		this(attributeId, null, false, values);
	}
	
	public Attribute(String attributeId, 
			AttributeValue ...values){
		this(attributeId, null, false, Arrays.asList(values));
	}
	
	/**
	 * Gets attribute identifier.
	 * 
	 * @return attribute identifier
	 */
	public String getAttributeId(){
		return attributeId;
	}
	
	/**
	 * Gets attribute values as collection of
	 * {@link AttributeValue} instances
	 * 
	 * @return collection of {@link AttributeValue} 
	 * instances
	 */
	public Collection<AttributeValue> getValues(){
		return Collections.unmodifiableCollection(values);
	}
	
	/**
	 * Gets all instances of {@link AttributeValue} by type
	 * 
	 * @param type an attribute type
	 * @return a collection of {@link AttributeValue} of given type
	 */
	public Collection<AttributeValue> getValuesByType(final AttributeValueType type)
	{
		return Collections2.filter(values, new Predicate<AttributeValue>() {
			@Override
			public boolean apply(AttributeValue a) {
				return a.getType().equals(type);
			}
			
		});
	}
	
	/**
	 * Gets this attribute issuer
	 * 
	 * @return issuer of this attribute
	 * identifier or <code>null</code>
	 * if it's not available
	 */
	public String getIssuer(){
		return issuer;
	}
	
	/**
	 * Tests if this attribute needs
	 * to be included back to the
	 * evaluation result
	 * 
	 * @return <code>true</code>
	 * if needs to be included
	 */
	public boolean isIncludeInResult(){
		return includeInResult;
	}
	
	@Override
	public final String toString(){
		return Objects.toStringHelper(this)
		.add("AttributeId", attributeId)
		.add("Issuer", issuer)
		.add("IncludeInResult", includeInResult)
		.add("Values", values).toString();
	}
	
	@Override
	public final int hashCode(){
		return Objects.hashCode(
				attributeId, issuer, includeInResult, values);
	}
	
	@Override
	public final boolean equals(Object o){
		if(o == this){
			return true;
		}
		if(o == null){
			return false;
		}
		if(!(o instanceof Attribute)){
			return false;
		}
		Attribute a = (Attribute)o;
		return Objects.equal(attributeId, a.attributeId) &&
			(!(includeInResult ^ a.includeInResult)) &&
			Objects.equal(issuer, a.issuer) && values.equals(a.values);
			
	}
}