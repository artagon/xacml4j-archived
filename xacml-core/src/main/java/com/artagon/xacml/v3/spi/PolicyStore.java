package com.artagon.xacml.v3.spi;

import java.util.Collection;

import com.artagon.xacml.v3.CompositeDecisionRule;
import com.artagon.xacml.v3.Decision;
import com.artagon.xacml.v3.EvaluationContext;
import com.artagon.xacml.v3.PolicyReferenceResolver;

public interface PolicyStore extends PolicyReferenceResolver
{
	public enum Type
	{
		FIRST_APPLICABLE,
		ONLY_ONE_APPLICABLE,
		DENY_OVERRIDES;
	}
	
	/**
	 * Gets policy store mode
	 * 
	 * @return {@link Type} a policy store mode
	 */
	Type getMode();
	
	/**
	 * Gets top level policies
	 * 
	 * @return a collection of {@link CompositeDecisionRule} instances
	 */
	Collection<CompositeDecisionRule> getPolicies();
	
	/**
	 * Evaluates given context against top level 
	 * policies stored in this policy store
	 * 
	 * @param context an evaluation context
	 * @return {@link Decision}
	 */
	Decision evaluate(EvaluationContext context);
	
	/**
	 * Adds top level policy to this store
	 * 
	 * @param policy a top level policy
	 */
	void addPolicy(CompositeDecisionRule policy);
	
	/**
	 * Adds a policy referenced by top level policies
	 * 
	 * @param policy a referenced policy
	 */
	void addReferencedPolicy(CompositeDecisionRule policy);
		
}
