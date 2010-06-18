package com.artagon.xacml.v3;

import com.artagon.xacml.v3.spi.PolicyReferenceResolver;
import com.artagon.xacml.v3.spi.XPathProvider;
import com.google.common.base.Preconditions;

public class PolicyEvaluationContext extends BaseEvaluationContext
{	
	private Policy policy;
	private XPathVersion xpathVersion;
	
	/**
	 * Creates policy evaluation context without parent evaluation context
	 * @param policy a target policy
	 * @param service an attribute resolver
	 * @param policyResolver a policy resolver
	 */
	public PolicyEvaluationContext(
			Policy policy, 
			ContextHandler service, 
			XPathProvider xpathProvider,
			PolicyReferenceResolver policyResolver)
	{
		super(service, xpathProvider, policyResolver);
		Preconditions.checkNotNull(policy);
		this.policy = policy;
		this.xpathVersion = XPathVersion.XPATH1;
	}
	
	@Override
	public Policy getCurrentPolicy() {
		return policy;
	}
	
	@Override
	public XPathVersion getXPathVersion() {
		PolicyDefaults defaults = policy.getDefaults();
		return (defaults == null)?xpathVersion:((
				defaults.getXPathVersion() == null)?
						xpathVersion:defaults.getXPathVersion());
	}

}
