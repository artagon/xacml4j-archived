package com.artagon.xacml.policy.type;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.artagon.xacml.policy.Attribute;

public class AnyURITypeTest 
{
	private AnyURIType t1;
	
	@Before
	public void init(){
		this.t1 = DataTypes.ANYURI.getType();
	}
	
	@Test
	public void testEquals(){
		Attribute v0 = t1.create("http://www.test.org");
		Attribute v1 = t1.create("http://www.test.org");
		assertEquals(v0, v1);
	}
}