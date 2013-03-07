package org.xacml4j.v30.pdp;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createStrictControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xacml4j.v30.Decision;
import org.xacml4j.v30.Effect;
import org.xacml4j.v30.EvaluationContext;
import org.xacml4j.v30.EvaluationException;
import org.xacml4j.v30.Expression;
import org.xacml4j.v30.MatchResult;
import org.xacml4j.v30.XacmlSyntaxException;
import org.xacml4j.v30.spi.repository.PolicyReferenceResolver;
import org.xacml4j.v30.types.StringType;

import com.google.common.collect.Iterables;

public class PolicyTest 
{
	private EvaluationContext context;
	private Policy policy;
	private Target target;
	
	
	private DecisionCombiningAlgorithm<Rule> combingingAlg;
	
	private Expression denyObligationAttributeExp;
	private Expression permitObligationAttributeExp;
	
	private Expression denyAdviceAttributeExp;
	private Expression permitAdviceAttributeExp;

	
	
	private PolicyReferenceResolver referenceResolver;
	private EvaluationContextHandler handler;
	
	private IMocksControl c;
	
	@SuppressWarnings("unchecked")
	@Before
	public void init() throws XacmlSyntaxException
	{		
		this.c = createStrictControl();
		
		this.target = c.createMock(Target.class);
		this.combingingAlg = c.createMock(DecisionCombiningAlgorithm.class);
		
		this.permitAdviceAttributeExp = c.createMock(Expression.class);
		this.denyAdviceAttributeExp = c.createMock(Expression.class);
		
		this.denyObligationAttributeExp = c.createMock(Expression.class);
		this.permitObligationAttributeExp = c.createMock(Expression.class);
		
		this.policy = Policy.builder("TestPolicy")
				.version("1.0")
				.target(target)
				.withRule(c.createMock(Rule.class))
				.withCombiningAlgorithm(combingingAlg)
				.obligation(ObligationExpression
						.builder("denyObligation", Effect.DENY)
							.attribute("testId", denyObligationAttributeExp))
				.obligation(ObligationExpression
						.builder("permitObligation", Effect.PERMIT)
						.attribute("testId", permitObligationAttributeExp))
				.advice(AdviceExpression
						.builder("denyAdvice", Effect.DENY)
						.attribute("testId", denyAdviceAttributeExp))
				.advice(AdviceExpression
					.builder("permitAdvice", Effect.PERMIT)
					.attribute("testId", permitAdviceAttributeExp))
				.create();
			
		this.referenceResolver = c.createMock(PolicyReferenceResolver.class);
		this.handler = c.createMock(EvaluationContextHandler.class);
		this.context = new RootEvaluationContext(true, 0, referenceResolver, handler);
	}
	
	@Test
	public void testPolicyBuilder()
	{
		Policy p = Policy
				.builder("testId")
				.version("1.0.1")
				.withCombiningAlgorithm(combingingAlg)
				.target(Target.builder())
				.create();
	}
	
	@Test
	@Ignore
	public void testPolicyCreate() throws XacmlSyntaxException
	{
//		Policy p = new Policy("testId", 
//				Version.valueOf(1),
//				"Test",
//				new PolicyDefaults(XPathVersion.XPATH1),
//				target, 
//				Collections.<VariableDefinition>emptyList(), 
//				combingingAlg, rules, adviceExpressions, obligationExpressions);
//		assertEquals("testId", p.getId());
//		assertEquals(Version.valueOf(1), p.getVersion());
//		assertEquals("Test", p.getDescription());
//		assertSame(target, p.getTarget());
//		assertSame(combingingAlg, p.getRuleCombiningAlgorithm());
	}
	
	@Test
	public void testCreateContext() throws EvaluationException
	{
		EvaluationContext policyContext = policy.createContext(context);
		assertSame(policy, policyContext.getCurrentPolicy());
		assertSame(context, policyContext.getParentContext());
		EvaluationContext policyContext1 = policy.createContext(policyContext);
		assertSame(policyContext, policyContext1);
	}
	
	@Test
	public void testIsApplicableTargetMatch() throws EvaluationException
	{
		EvaluationContext policyContext = policy.createContext(context);
		expect(target.match(policyContext)).andReturn(MatchResult.MATCH);
		c.replay();
		assertEquals(MatchResult.MATCH, policy.isMatch(policyContext));
		c.verify();
	}
	
	@Test
	public void testIsApplicableTargetNoMatch() throws EvaluationException
	{
		EvaluationContext policyContext = policy.createContext(context);
		expect(target.match(policyContext)).andReturn(MatchResult.NOMATCH);
		c.replay();
		assertEquals(MatchResult.NOMATCH, policy.isMatch(policyContext));
		c.verify();
	}
	
	@Test
	public void testIsApplicableTargetIndeterminate() throws EvaluationException
	{
		EvaluationContext policyContext = policy.createContext(context);
		expect(target.match(policyContext)).andReturn(MatchResult.INDETERMINATE);
		c.replay();
		assertEquals(MatchResult.INDETERMINATE, policy.isMatch(policyContext));
		c.verify();
	}
	
	@Test
	public void testEvaluateCombiningAlgorithmResultIsDeny() throws EvaluationException
	{
		EvaluationContext policyContext = policy.createContext(context);
		
		Capture<EvaluationContext> contextCapture = new Capture<EvaluationContext>();
		Capture<List<Rule>> ruleCapture = new Capture<List<Rule>>();
		
		expect(combingingAlg.combine(capture(contextCapture), capture(ruleCapture))).andReturn(Decision.DENY);
		expect(combingingAlg.getId()).andReturn("id");
		
		expect(denyAdviceAttributeExp.evaluate(policyContext)).andReturn(StringType.STRING.create("testValue1"));
		expect(denyObligationAttributeExp.evaluate(policyContext)).andReturn(StringType.STRING.create("testValue1"));
		
		c.replay();
		assertEquals(Decision.DENY, policy.evaluate(policyContext));
		c.verify();
		assertSame(policy, policyContext.getCurrentPolicy());
		assertSame(policyContext, contextCapture.getValue());
		
		assertEquals(1, Iterables.size(context.getMatchingAdvices(Decision.DENY)));
		assertEquals(1, Iterables.size(context.getMatchingObligations(Decision.DENY)));
	}
	
	@Test
	public void testEvaluateCombiningAlgorithResultIsPermit() throws EvaluationException
	{
		EvaluationContext policyContext = policy.createContext(context);
		
		Capture<EvaluationContext> contextCapture = new Capture<EvaluationContext>();
		Capture<List<Rule>> ruleCapture = new Capture<List<Rule>>();
		
		expect(combingingAlg.combine(capture(contextCapture), capture(ruleCapture))).andReturn(Decision.PERMIT);
		expect(combingingAlg.getId()).andReturn("id");
		expect(permitAdviceAttributeExp.evaluate(policyContext)).andReturn(StringType.STRING.create("testValue1"));
		expect(permitObligationAttributeExp.evaluate(policyContext)).andReturn(StringType.STRING.create("testValue1"));
		
		c.replay();
		assertEquals(Decision.PERMIT, policy.evaluate(policyContext));
		c.verify();
		assertSame(policy, policyContext.getCurrentPolicy());
		assertSame(policyContext, contextCapture.getValue());
		assertEquals(1, Iterables.size(context.getMatchingAdvices(Decision.PERMIT)));
		assertEquals(1, Iterables.size(context.getMatchingObligations(Decision.PERMIT)));
	}
	
	@Test
	public void testEvaluateCombiningAlgorithResultIsIndeterminate() throws EvaluationException
	{
		EvaluationContext policyContext = policy.createContext(context);
		
		Capture<EvaluationContext> contextCapture = new Capture<EvaluationContext>();
		Capture<List<Rule>> ruleCapture = new Capture<List<Rule>>();
		
		expect(combingingAlg.combine(capture(contextCapture), capture(ruleCapture))).andReturn(Decision.INDETERMINATE);
		expect(combingingAlg.getId()).andReturn("id");
		
		c.replay();
		assertEquals(Decision.INDETERMINATE, policy.evaluate(policyContext));
		c.verify();
		assertSame(policy, policyContext.getCurrentPolicy());
		assertSame(policyContext, contextCapture.getValue());
		assertEquals(0, Iterables.size(context.getMatchingAdvices(Decision.DENY)));
		assertEquals(0, Iterables.size(context.getMatchingObligations(Decision.DENY)));
		assertEquals(0, Iterables.size(context.getMatchingAdvices(Decision.PERMIT)));
		assertEquals(0, Iterables.size(context.getMatchingObligations(Decision.PERMIT)));
	}
	
	
}