package com.artagon.xacml.v30.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.artagon.xacml.v30.types.RFC822Name;

public class RFC822NameTest 
{
	@Test
	public void testParseValidName()
	{
		RFC822Name n = RFC822Name.parse("test@test.org");
		assertEquals("test", n.getLocalPart());
		assertEquals("test.org", n.getDomainPart());
		n = RFC822Name.parse("tEst@TeSt.org");
		assertEquals("tEst", n.getLocalPart());
		assertEquals("test.org", n.getDomainPart());

		n = RFC822Name.parse("\"John Doe\" <tEst@TeSt.org>");
		assertEquals("tEst", n.getLocalPart());
		assertEquals("test.org", n.getDomainPart());
	}
	
	@Test
	public void testMatch()
	{
		RFC822Name n = RFC822Name.parse("test@east.test.org");
		assertTrue(n.matches("east.test.org"));
		assertTrue(n.matches("test@east.test.org"));
		assertTrue(n.matches(".test.org"));
	}
}