package com.artagon.xacml.v3;

import com.artagon.xacml.v3.spi.PolicyReferenceResolver;
import com.artagon.xacml.v3.spi.XPathProvider;
import com.google.common.base.Preconditions;

public class PolicySetEvaluationContext extends BaseEvaluationContext
{
	private PolicySet policySet;
	private XPathVersion xpathVersion;
	
	public PolicySetEvaluationContext(PolicySet policySet, 
			ContextHandler attributeService, 
			XPathProvider xpathProvider,
			PolicyReferenceResolver policyResolver){
		super(attributeService, xpathProvider, policyResolver);
		Preconditions.checkNotNull(policySet);
		this.policySet = policySet;
		this.xpathVersion = XPathVersion.XPATH1;
	}

	@Override
	public PolicySet getCurrentPolicySet() {
		return policySet;
	}

	@Override
	public XPathVersion getXPathVersion() {
		PolicySetDefaults defaults = policySet.getDefaults();
		if (defaults == null) {
			return xpathVersion;
		}
		XPathVersion version = defaults.getXPathVersion();
		return (version == null) ? xpathVersion : version;
	}
}
