package com.artagon.xacml.v3.policy;


public class DefaultEvaluationContextFactory implements EvaluationContextFactory
{
	private AttributeResolutionService service;
	
	public DefaultEvaluationContextFactory(AttributeResolutionService service){
		this.service = service;	
	}

	@Override
	public EvaluationContext createContext(Policy policy) {
		return new PolicyEvaluationContext(policy, service);
	}
	
	@Override
	public EvaluationContext createContext(PolicySet policySet) {
		return new PolicySetEvaluationContext(policySet, service);
	}
}