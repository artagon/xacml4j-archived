package com.artagon.xacml.v3.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.artagon.xacml.v3.AttributeCategoryId;
import com.artagon.xacml.v3.policy.type.DataTypes;
import com.artagon.xacml.v3.policy.type.IntegerType;

public class AttributeDesignatorTest extends XacmlPolicyTestCase
{
	private String issuer;
	private String attributeId;
	private String badAttributeId;
	private IntegerType type;
	
	@Before
	public void init(){
		this.issuer = "urn:test:issuer";
		this.attributeId = "urn:test:attrId";
		this.badAttributeId = "urn:test:attrId:bad";
		this.type = DataTypes.INTEGER.getType();
	}
	
	@Test(expected=EvaluationException.class)
	public void testMustBePresentTrueAttributeDoesNotExist() throws EvaluationException
	{
		DefaultAttributeDesignator desig = new DefaultAttributeDesignator(
				AttributeCategoryId.SUBJECT_RECIPIENT, attributeId, issuer, type, true);
		desig.evaluate(context);
	}
	
	@Test
	public void testMustBePresentTrueAttributeDoesExist() throws EvaluationException
	{
		Collection<AttributeValue> attributes = new LinkedList<AttributeValue>();
		AttributeValue attr = type.create(10l);
		attributes.add(attr);
		attributeService.addAttribute(AttributeCategoryId.SUBJECT_RECIPIENT,
				issuer, attributeId, type, attributes);
		DefaultAttributeDesignator desig = new DefaultAttributeDesignator(AttributeCategoryId.SUBJECT_RECIPIENT, issuer, attributeId, 
				type, true);
		Value v = desig.evaluate(context);
		assertNotNull(v);
		assertTrue(((BagOfAttributeValues<?>)v).contains(attr));
	}
	
	@Test
	public void testMustBePresentFalseAttributeDoesNotExistWithId() throws EvaluationException
	{
		DefaultAttributeDesignator desig = new DefaultAttributeDesignator(AttributeCategoryId.SUBJECT_RECIPIENT,
				badAttributeId, issuer,  type, false);
		Value v = desig.evaluate(context);
		assertNotNull(v);
		assertEquals(type.bagOf(), v.getEvaluatesTo());
	}
}
