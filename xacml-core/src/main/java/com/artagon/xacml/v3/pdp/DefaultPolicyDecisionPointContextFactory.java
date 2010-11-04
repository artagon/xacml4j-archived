package com.artagon.xacml.v3.pdp;

import java.util.Collections;
import java.util.List;

import com.artagon.xacml.v3.EvaluationContext;
import com.artagon.xacml.v3.EvaluationContextHandler;
import com.artagon.xacml.v3.PolicyReferenceResolver;
import com.artagon.xacml.v3.RequestContext;
import com.artagon.xacml.v3.Result;
import com.artagon.xacml.v3.RootEvaluationContext;
import com.artagon.xacml.v3.XPathVersion;
import com.artagon.xacml.v3.spi.PolicyDomain;
import com.artagon.xacml.v3.spi.PolicyInformationPoint;
import com.artagon.xacml.v3.spi.PolicyRepository;
import com.artagon.xacml.v3.spi.XPathProvider;
import com.artagon.xacml.v3.spi.xpath.DefaultXPathProvider;
import com.google.common.base.Preconditions;

public class DefaultPolicyDecisionPointContextFactory 
	implements PolicyDecisionPointContextFactory
{
	private PolicyInformationPoint pip;
	private PolicyDecisionAuditor decisionAuditor;
	private PolicyDecisionCache decisionCache;
	private XPathProvider xpathProvider;
	private PolicyReferenceResolver policyReferenceResolver;
	private PolicyDomain policyDomain;
	private RequestContextHandlerChain requestHandlers;
	
	private boolean validateFuncParamsAtRuntime = false;
	private XPathVersion defaultXPathVersion = XPathVersion.XPATH1;
	
	public DefaultPolicyDecisionPointContextFactory(
			PolicyDomain policyDomain, 
			PolicyRepository repository,
			PolicyDecisionAuditor auditor,
			PolicyDecisionCache cache,
			XPathProvider xpathProvider, 
			PolicyInformationPoint pip,
			List<RequestContextHandler> handlers)
	{
		Preconditions.checkArgument(policyDomain != null);
		Preconditions.checkArgument(repository != null);
		Preconditions.checkArgument(xpathProvider != null);
		Preconditions.checkArgument(pip != null);
		Preconditions.checkArgument(auditor != null);
		Preconditions.checkArgument(cache != null);
		this.policyReferenceResolver = new DefaultPolicyReferenceResolver(repository);
		this.pip = pip;
		this.xpathProvider = xpathProvider;
		this.policyDomain = policyDomain;
		this.decisionAuditor = auditor;
		this.decisionCache = cache;
		this.requestHandlers = new RequestContextHandlerChain(handlers);
	}
	
	public DefaultPolicyDecisionPointContextFactory(
			PolicyDomain policyDomain, 
			PolicyRepository repository,
			PolicyDecisionAuditor auditor,
			PolicyDecisionCache cache,
			XPathProvider xpathProvider, 
			PolicyInformationPoint pip){
		this(policyDomain, repository, auditor, cache, xpathProvider, pip, 
				Collections.<RequestContextHandler>emptyList());
	}
	
	public DefaultPolicyDecisionPointContextFactory(
			PolicyDomain policyDomain, 
			PolicyRepository repository, 
			PolicyInformationPoint pip,
			List<RequestContextHandler> handlers)
	{
		this(policyDomain, repository, 
				new NoAuditPolicyDecisionPointAuditor(), 
				new NoCachePolicyDecisionCache(), 
				new DefaultXPathProvider(),  
				pip, handlers);
	}
	
	public DefaultPolicyDecisionPointContextFactory(
			PolicyDomain policyDomain, 
			PolicyRepository repository, 
			PolicyInformationPoint pip)
	{
		this(policyDomain, repository, 
				new NoAuditPolicyDecisionPointAuditor(), 
				new NoCachePolicyDecisionCache(), 
				new DefaultXPathProvider(),  
				pip, 
				Collections.<RequestContextHandler>emptyList());
	}
	
	public void setValidaFunctionParametersAtRuntime(boolean validate){
		this.validateFuncParamsAtRuntime = validate;
	}

	@Override
	public PolicyDecisionPointContext createContext(final PolicyDecisionCallback pdp) 
	{
		return new PolicyDecisionPointContext() {
			
			@Override
			public XPathProvider getXPathProvider() {
				return xpathProvider;
			}
			
			@Override
			public PolicyDomain getPolicyDomain() {
				return policyDomain;
			}
			
			@Override
			public PolicyDecisionCache getDecisionCache() {
				return decisionCache;
			}
			
			@Override
			public PolicyDecisionAuditor getDecisionAuditor() {
				return decisionAuditor;
			}
			
			@Override
			public Result requestDecision(RequestContext req) {

				return pdp.requestDecision(this, req);
			}

			@Override
			public RequestContextHandlerChain getRequestHandlers() {
				return requestHandlers;
			}

			@Override
			public EvaluationContext createEvaluationContext(RequestContext request) 
			{
				Preconditions.checkArgument(!request.containsRepeatingCategories());
				Preconditions.checkArgument(!request.containsRequestReferences());
				RequestContextCallback callback = new DefaultRequestContextCallback(request);
				EvaluationContextHandler handler = new DefaultEvaluationContextHandler(
						callback, xpathProvider, pip);
				return new RootEvaluationContext(validateFuncParamsAtRuntime, 
						defaultXPathVersion, policyReferenceResolver, handler);
			}
		};
	}
}