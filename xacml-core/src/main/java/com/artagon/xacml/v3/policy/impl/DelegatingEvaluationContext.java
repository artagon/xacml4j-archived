package com.artagon.xacml.v3.policy.impl;

import java.util.Collection;
import java.util.TimeZone;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.artagon.xacml.util.Preconditions;
import com.artagon.xacml.v3.Advice;
import com.artagon.xacml.v3.AttributeCategoryId;
import com.artagon.xacml.v3.Obligation;
import com.artagon.xacml.v3.policy.AttributeDesignator;
import com.artagon.xacml.v3.policy.AttributeSelector;
import com.artagon.xacml.v3.policy.AttributeValue;
import com.artagon.xacml.v3.policy.AttributeValueType;
import com.artagon.xacml.v3.policy.BagOfAttributeValues;
import com.artagon.xacml.v3.policy.EvaluationContext;
import com.artagon.xacml.v3.policy.EvaluationException;
import com.artagon.xacml.v3.policy.Policy;
import com.artagon.xacml.v3.policy.PolicyIDReference;
import com.artagon.xacml.v3.policy.PolicyResolutionException;
import com.artagon.xacml.v3.policy.PolicySet;
import com.artagon.xacml.v3.policy.PolicySetIDReference;
import com.artagon.xacml.v3.policy.Value;
import com.artagon.xacml.v3.policy.XPathEvaluationException;

/**
 * An implementation of {@link EvaluationContext} which
 * delegates all invocation to the given context
 * 
 * @author Giedrius Trumpickas
 */
class DelegatingEvaluationContext implements EvaluationContext
{
	private EvaluationContext delegate;
	
	public DelegatingEvaluationContext(EvaluationContext context){
		Preconditions.checkNotNull(context);
		this.delegate = context;
	}
	
	@Override
	public EvaluationContext getParentContext(){
		return delegate;
	}
	
	@Override
	public Node getContent(AttributeCategoryId categoryId) {
		return delegate.getContent(categoryId);
	}

	@Override
	public boolean isValidateFuncParamAtRuntime() {
		return delegate.isValidateFuncParamAtRuntime();
	}

	@Override
	public void addAdvices(Collection<Advice> advices) {
		delegate.addAdvices(advices);
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public void addObligations(Collection<Obligation> obligations) {
		delegate.addObligations(obligations);
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public Collection<Advice> getAdvices() {
		return delegate.getAdvices();
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public Policy getCurrentPolicy() {
		return delegate.getCurrentPolicy();
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public PolicySet getCurrentPolicySet() {
		return delegate.getCurrentPolicySet();
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public Collection<Obligation> getObligations() {
		return delegate.getObligations();
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public Value getVariableEvaluationResult(String variableId) {
		return delegate.getVariableEvaluationResult(variableId);
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public BagOfAttributeValues<AttributeValue> resolveAttributeDesignator(AttributeCategoryId category,
			String attributeId, AttributeValueType dataType,
			String issuer) {
		return delegate.resolveAttributeDesignator(category, attributeId, dataType,
				issuer);
	}
	
	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public void setVariableEvaluationResult(String variableId, Value value) {
		delegate.setVariableEvaluationResult(variableId, value);
	}
	
	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public Policy resolve(PolicyIDReference ref) throws PolicyResolutionException 
	{
		return delegate.resolve(ref);
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public PolicySet resolve(PolicySetIDReference ref) throws PolicyResolutionException {
		return delegate.resolve(ref);
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public PolicyIDReference getCurrentPolicyIDReference() {
		return delegate.getCurrentPolicyIDReference();
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public PolicySetIDReference getCurrentPolicySetIDReference() {
		return delegate.getCurrentPolicySetIDReference();
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public TimeZone getTimeZone() {
		return delegate.getTimeZone();
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public Node evaluateToNode(String path, Node context)
			throws XPathEvaluationException {
		return delegate.evaluateToNode(path, context);
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public NodeList evaluateToNodeSet(String path, Node context)
			throws XPathEvaluationException {
		return delegate.evaluateToNodeSet(path, context);
	}

	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public Number evaluateToNumber(String path, Node context)
			throws XPathEvaluationException {
		return delegate.evaluateToNumber(path, context);
	}
	
	/**
	 * Delegates call to {@link EvaluationContext} instance
	 */
	@Override
	public String evaluateToString(String path, Node context)
			throws XPathEvaluationException {
		return delegate.evaluateToString(path, context);
	}

	/**
	 * Delegates call to {@link EvaluationContext#resolve(AttributeDesignator)} 
	 * instance
	 */
	@Override
	public BagOfAttributeValues<AttributeValue> resolve(
			AttributeDesignator designator) throws EvaluationException {
		return delegate.resolve(designator);
	}

	/**
	 * Delegates call to {@link EvaluationContext#resolve(AttributeSelector)} 
	 * instance
	 */
	@Override
	public BagOfAttributeValues<AttributeValue> resolve(
			AttributeSelector selector) throws EvaluationException {
		return delegate.resolve(selector);
	}
}
