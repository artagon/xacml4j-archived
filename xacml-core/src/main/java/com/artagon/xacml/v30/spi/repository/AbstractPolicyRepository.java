package com.artagon.xacml.v30.spi.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.artagon.xacml.v30.Apply;
import com.artagon.xacml.v30.CompositeDecisionRule;
import com.artagon.xacml.v30.FunctionReference;
import com.artagon.xacml.v30.Policy;
import com.artagon.xacml.v30.PolicySet;
import com.artagon.xacml.v30.PolicyVisitorSupport;
import com.artagon.xacml.v30.Version;
import com.artagon.xacml.v30.VersionMatch;
import com.artagon.xacml.v30.XacmlSyntaxException;
import com.artagon.xacml.v30.marshall.PolicyUnmarshaller;
import com.artagon.xacml.v30.marshall.jaxb.Xacml30PolicyUnmarshaller;
import com.artagon.xacml.v30.policy.combine.DefaultXacml30DecisionCombiningAlgorithms;
import com.artagon.xacml.v30.policy.function.DefaultXacml30Functions;
import com.artagon.xacml.v30.spi.combine.DecisionCombiningAlgorithmProvider;
import com.artagon.xacml.v30.spi.function.FunctionProvider;
import com.google.common.base.Preconditions;

/**
 * A base class for {@link PolicyRepository} implementations
 * every {@link PolicyRepository} implementation supports
 * at least {@link PolicyRepositorySearch} capability
 * 
 * @author Giedrius Trumpickas
 */
public abstract class AbstractPolicyRepository 
	implements PolicyRepository
{	
	private ConcurrentMap<PolicyRepositoryListener, PolicyRepositoryListener> listeners;
	
	private PolicyUnmarshaller unmarshaller;
	
	private FunctionProvider functions;
	private DecisionCombiningAlgorithmProvider decisionAlgorithms;
	
	protected AbstractPolicyRepository(FunctionProvider functions, 
			DecisionCombiningAlgorithmProvider decisionAlgorithms) 
		throws Exception
	{
		Preconditions.checkNotNull(functions);
		Preconditions.checkNotNull(decisionAlgorithms);
		this.functions = functions;
		this.decisionAlgorithms = decisionAlgorithms;
		this.listeners = new ConcurrentHashMap<PolicyRepositoryListener, PolicyRepositoryListener>();
		this.unmarshaller = new Xacml30PolicyUnmarshaller(functions, decisionAlgorithms);
	}
	
	protected AbstractPolicyRepository() throws Exception
	{
		this(new DefaultXacml30Functions(), 
				new DefaultXacml30DecisionCombiningAlgorithms());
	}
	
	/**
	 * Implementation assumes that 
	 * {@link #getPolicies(String, VersionMatch, VersionMatch, VersionMatch)}
	 * returns unordered collection of policies
	 */
	@Override
	public Policy getPolicy(String id, VersionMatch version, 
			VersionMatch earliest, VersionMatch latest){
		Collection<Policy> found = getPolicies(id, version, earliest, latest);
		if(found.isEmpty()){
			return null;
		}
		return Collections.<Policy>max(found, new Comparator<Policy>() {
			@Override
			public int compare(Policy a, Policy b) {
				return a.getVersion().compareTo(b.getVersion());
			}
		});
	}
	
	/**
	 * Implementation assumes that 
	 * {@link #getPolicies(String, VersionMatch, VersionMatch, VersionMatch)}
	 * returns unordered collection of policies. 
	 */
	@Override
	public PolicySet getPolicySet(String id, VersionMatch version, 
			VersionMatch earliest, VersionMatch latest)
	{
		Collection<PolicySet> found = getPolicySets(id, version, earliest, latest);
		if(found.isEmpty()){
			return null;
		}
		return Collections.<PolicySet>max(found, new Comparator<PolicySet>() {
			@Override
			public int compare(PolicySet a, PolicySet b) {
				return a.getVersion().compareTo(b.getVersion());
			}
			
		});
	}
	
	/**
	 * @see {@link #getPolicySet(String, VersionMatch, VersionMatch, VersionMatch)
	 */
	@Override
	public final Collection<Policy> getPolicies(String id) {
		return getPolicies(id, null);
	}
	
	/**
	 * @see {@link #getPolicySet(String, VersionMatch, VersionMatch, VersionMatch)
	 */
	@Override
	public final Collection<Policy> getPolicies(String id, VersionMatch earliest,
			VersionMatch latest) {
		return getPolicies(id, null, earliest, latest);
	}
	
	/**
	 * @see {@link #getPolicySet(String, VersionMatch, VersionMatch, VersionMatch)
	 */
	@Override
	public final Collection<Policy> getPolicies(String id, VersionMatch version){
		return getPolicies(id, version, null, null);
	}
	
	/**
	 * @see {@link #getPolicySet(String, VersionMatch, VersionMatch, VersionMatch)
	 */
	@Override
	public final Collection<PolicySet> getPolicySets(String id, VersionMatch version){
		return getPolicySets(id, version, null, null);
	}
	
	@Override
	public final void addPolicyRepositoryListener(PolicyRepositoryListener l){
		this.listeners.put(l, l);
	}
	
	public final void removePolicyRepositoryListener(PolicyRepositoryListener l){
		listeners.remove(l);
	}
	
	private void notifyPolicyAdded(Policy p){
		for(PolicyRepositoryListener l : listeners.keySet()){
			l.policyAdded(p);
		}
	}
	
	private void notifyPolicySetAdded(PolicySet p){
		for(PolicyRepositoryListener l : listeners.keySet()){
			l.policySetAdded(p);
		}
	}
	
	private void notifyPolicyRemoved(Policy p){
		for(PolicyRepositoryListener l : listeners.keySet()){
			l.policyRemoved(p);
		}
	}
	
	private void notifyPolicySetRemoved(PolicySet p){
		for(PolicyRepositoryListener l : listeners.keySet()){
			l.policySetAdded(p);
		}
	}
	
	/**
	 * Default implementation uses 
	 * {@link #getPolicies(String, VersionMatch, VersionMatch, VersionMatch)
	 * {@link #getPolicySet(String, VersionMatch, VersionMatch, VersionMatch)
	 * to locate appropriate policy or policy set
	 * 
	 * @see #getPolicies(String, VersionMatch, VersionMatch, VersionMatch)
	 * @see #getPolicySet(String, VersionMatch, VersionMatch, VersionMatch)
	 */
	@Override
	public CompositeDecisionRule get(String id, Version v)
	{
		VersionMatch m = new VersionMatch(v.getValue());
		Policy p = getPolicy(id, m, null, null);
		return (p != null)?p:getPolicySet(id, m, null, null);
	}
	
	@Override
	public final boolean add(CompositeDecisionRule r) 
	{
//		r.accept(new DecisionAlgorithmValidatingVisitor());
//		r.accept(new FunctionValidatingVisitor());
		if(r instanceof Policy){
			Policy p = (Policy)r;
			if(addPolicy(p)){
				notifyPolicyAdded(p);
				return true;
			}
			return false;
		}
		if(r instanceof PolicySet){
			PolicySet p = (PolicySet)r;
			if(addPolicySet(p)){
				notifyPolicySetAdded(p);
				return true;
			}
			return false;
		}
		return false;
	}
	
	@Override
	public boolean remove(CompositeDecisionRule r) {
		if(r instanceof Policy){
			Policy p = (Policy)r;
			if(removePolicy(p)){
				notifyPolicyRemoved(p);
				return true;
			}
			return false;	
		}
		if(r instanceof PolicySet){
			PolicySet p = (PolicySet)r;
			if(removePolicySet(p)){
				notifyPolicySetRemoved(p);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Implemented by subclass to add given
	 * policy to this repository
	 * 
	 * @param p a policy
	 */
	protected abstract boolean addPolicy(Policy p);
	
	/**
	 * Implemented by subclass to add policy set
	 * to this repository
	 * 
	 * @param p a policy set
	 */
	protected abstract boolean addPolicySet(PolicySet p);
	
	
	
	protected abstract  boolean removePolicy(Policy p);
	protected abstract boolean removePolicySet(PolicySet p);

	@Override
	public final CompositeDecisionRule importPolicy(InputStream source)
			throws XacmlSyntaxException, IOException {
		CompositeDecisionRule r =  unmarshaller.unmarshal(source);
		add(r);
		return r;
	}
	
	private class FunctionValidatingVisitor 
		extends PolicyVisitorSupport
	{
		@Override
		public void visitEnter(FunctionReference function) {
			Preconditions.checkArgument(functions.isFunctionProvided(
					function.getFunctionId()));
		}

		@Override
		public void visitEnter(Apply apply) {
			Preconditions.checkArgument(functions.isFunctionProvided(
					apply.getFunctionId()));
		}
		
	}
	
	private class DecisionAlgorithmValidatingVisitor 
		extends PolicyVisitorSupport
	{
		@Override
		public void visitEnter(Policy policy) {
			Preconditions.checkArgument(
					decisionAlgorithms.isRuleAgorithmProvided(
							policy.getRuleCombiningAlgorithm().getId()));
		}

		@Override
		public void visitEnter(PolicySet policySet) {
			Preconditions.checkArgument(
					decisionAlgorithms.isPolicyAgorithmProvided(
							policySet.getPolicyDecisionCombiningAlgorithm().getId()));
		}
	}
}