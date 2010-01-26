
package com.artagon.xacml.v3.policy;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import com.artagon.xacml.v3.policy.MatchAllOf;
import com.artagon.xacml.v3.policy.MatchResult;
import com.artagon.xacml.v3.policy.Matchable;

public class MatchAllOfTest extends XacmlPolicyTestCase
{
	private Collection<Matchable> matches;
	
	@Before
	public void init(){
		this.matches = new LinkedList<Matchable>();
	}
	
	@Test
	public void testAllMatch()
	{
		matches.add(new MockMatchable(MatchResult.MATCH));
		Matchable m = new MatchAllOf(matches);
		assertEquals(MatchResult.MATCH, m.match(context));
		matches.add(new MockMatchable(MatchResult.MATCH));
		m = new MatchAllOf(matches);
		assertEquals(MatchResult.MATCH, m.match(context));
		matches.add(new MockMatchable(MatchResult.MATCH));
		m = new MatchAllOf(matches);
		assertEquals(MatchResult.MATCH, m.match(context));
	}
	
	@Test
	public void testFirstMatchRestNoMatch()
	{
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.NOMATCH));
		Matchable m = new MatchAllOf(matches);
		assertEquals(MatchResult.NOMATCH, m.match(context));
	}
	
	@Test
	public void testFirstNoMatchRestMatch()
	{
		matches.add(new MockMatchable(MatchResult.NOMATCH));
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.MATCH));
		Matchable m = new MatchAllOf(matches);
		assertEquals(MatchResult.NOMATCH, m.match(context));
	}
	
	@Test
	public void testAllMatchAndAtLeastOneIndeterminate()
	{
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.INDETERMINATE));
		Matchable m = new MatchAllOf(matches);
		assertEquals(MatchResult.INDETERMINATE, m.match(context));
		matches.add(new MockMatchable(MatchResult.MATCH));
		m = new MatchAllOf(matches);
		assertEquals(MatchResult.INDETERMINATE, m.match(context));
	}
	
	@Test
	public void testAtLeastOneNoMatch()
	{
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.INDETERMINATE));
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.NOMATCH));
		Matchable m = new MatchAllOf(matches);
		assertEquals(MatchResult.NOMATCH, m.match(context));
	}
	
	@Test
	public void testAllNoMatchAndAtLeastOnIndeterminate()
	{
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.MATCH));
		matches.add(new MockMatchable(MatchResult.INDETERMINATE));
		matches.add(new MockMatchable(MatchResult.MATCH));
		Matchable m = new MatchAllOf(matches);
		assertEquals(MatchResult.INDETERMINATE, m.match(context));
	}
}
