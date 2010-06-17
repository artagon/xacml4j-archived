package com.artagon.xacml.v20;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.oasis.xacml.v20.context.ResponseType;
import org.oasis.xacml.v20.context.ResultType;
import org.oasis.xacml.v20.context.StatusType;
import org.oasis.xacml.v20.policy.AttributeAssignmentType;
import org.oasis.xacml.v20.policy.ObligationType;
import org.oasis.xacml.v20.policy.ObligationsType;

public class Xacml20ConformanceUtility 
{
	public static void assertResponse(ResponseType a, ResponseType b)
	{
		assertEquals(a.getResult().size(), a.getResult().size());
		List<ResultType> ar = a.getResult();
		List<ResultType> br = a.getResult();
		for(int i = 0; i < ar.size(); i++){
			assertResults(ar.get(i), br.get(i));
		}
	}
	
	public static void assertResults(ResultType a, ResultType b)
	{
		assertEquals(a.getDecision(), b.getDecision());
		assertEquals(a.getResourceId(), b.getResourceId());
		assertStatus(a.getStatus(), b.getStatus());
		assertObligations(a.getObligations(), b.getObligations());
	}
	
	public static void assertStatus(StatusType a, StatusType b)
	{
		assertEquals(a.getStatusCode().getValue(), b.getStatusCode().getValue());
	}
	
	public static void assertObligations(ObligationsType a, ObligationsType b)
	{
		if(a == null && 
				b == null){
			return;
		}
		List<ObligationType> oa = a.getObligation();
		List<ObligationType> ob = a.getObligation();
		assertEquals(oa.size(), ob.size());
		Map<String , ObligationType> aMap = toObligationMap(oa);
		Map<String , ObligationType> bMap = toObligationMap(ob);
		
		assertTrue(aMap.keySet().containsAll(bMap.keySet()));
		assertTrue(bMap.keySet().containsAll(aMap.keySet()));
		
		for(String obligationId : aMap.keySet()){
			ObligationType obligationA = aMap.get(obligationId);
			ObligationType obligationB = bMap.get(obligationId);
			assertObligation(obligationA, obligationB);
		}
	}
	
	public static void assertObligation(ObligationType a, ObligationType b)
	{
		List<AttributeAssignmentType> aAttr = a.getAttributeAssignment();
		List<AttributeAssignmentType> bAttr = b.getAttributeAssignment();
		assertEquals(aAttr.size(), bAttr.size());
		Map<String, AttributeAssignmentType> aMap = toAttributeAssignmentMap(aAttr);
		Map<String, AttributeAssignmentType> bMap = toAttributeAssignmentMap(bAttr);
		assertTrue(aMap.keySet().containsAll(bMap.keySet()));
		assertTrue(bMap.keySet().containsAll(aMap.keySet()));
		for(String attributeId : aMap.keySet()){
			AttributeAssignmentType attrA = aMap.get(attributeId);
			AttributeAssignmentType attrB = bMap.get(attributeId);
			assertAttributeAssignment(attrA, attrB);
		}
	}
	
	public static void assertAttributeAssignment(AttributeAssignmentType a, AttributeAssignmentType b)
	{
		assertEquals(a.getAttributeId(), b.getAttributeId());
		assertEquals(a.getDataType(), b.getDataType());
		assertEquals(a.getContent(), b.getContent());
	}
	
	private static Map<String, ObligationType> toObligationMap(
			List<ObligationType> obligations)
	{
		Map<String, ObligationType> map = new LinkedHashMap<String, ObligationType>();
		for(ObligationType o : obligations){
			map.put(o.getObligationId(), o);
		}
		return map;
	}
	
	private static Map<String, AttributeAssignmentType> toAttributeAssignmentMap(
			List<AttributeAssignmentType> attributes)
	{
		Map<String, AttributeAssignmentType> map = new LinkedHashMap<String, AttributeAssignmentType>();
		for(AttributeAssignmentType a : attributes){
			map.put(a.getAttributeId(), a);
		}
		return map;
	}
	
	public static String createName(String prefix, int testCaseNum, String sufix)
	{
		return new StringBuilder(prefix)
		.append(StringUtils.leftPad(Integer.toString(testCaseNum), 3, '0'))
		.append(sufix).toString();
	}
	
	@Test
	public void testName()
	{
		assertEquals("AA003B", createName("AA", 3, "B"));
	}
}