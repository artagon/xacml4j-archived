package org.xacml4j.v30.pdp;

import org.xacml4j.v30.XPathVersion;
import org.xacml4j.v30.spi.repository.PolicyReferenceResolver;


public final class RootEvaluationContext extends BaseEvaluationContext
{
	private XPathVersion defaultXPathVersion;
	
	public RootEvaluationContext(
			boolean validateFuncParamsAtRuntime, 
			int defaultDecisionCacheTTL,
			XPathVersion defaultXPathVersion, 
			PolicyReferenceResolver referenceResolver,
			EvaluationContextHandler contextHandler) {
		super(validateFuncParamsAtRuntime,
				defaultDecisionCacheTTL,
				contextHandler,
				referenceResolver);
		this.defaultXPathVersion = defaultXPathVersion;
	}
	
	public RootEvaluationContext(
			boolean validateFuncParamsAtRuntime, 
			int defaultDecisionCacheTTL,
			PolicyReferenceResolver referenceResolver, 
			EvaluationContextHandler handler){
		this(validateFuncParamsAtRuntime, 
				defaultDecisionCacheTTL,
				XPathVersion.XPATH1, 
				referenceResolver, 
				handler);
	}

	@Override
	public XPathVersion getXPathVersion() { 
		return defaultXPathVersion;
	}	
}

