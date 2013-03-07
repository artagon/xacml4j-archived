package org.xacml4j.v30.pdp;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xacml4j.v30.CompositeDecisionRule;
import org.xacml4j.v30.CompositeDecisionRuleIDReference;
import org.xacml4j.v30.Decision;
import org.xacml4j.v30.DecisionRule;
import org.xacml4j.v30.EvaluationContext;
import org.xacml4j.v30.StatusCode;
import org.xacml4j.v30.XPathVersion;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

public class PolicySet extends
	BaseCompositeDecisionRule implements PolicyElement
{
	private PolicySetDefaults policySetDefaults;
	private DecisionCombiningAlgorithm<CompositeDecisionRule> combine;
	private List<CompositeDecisionRule> decisionRules;

	private Map<String, Multimap<String, CombinerParameter>> policyCombinerParameters;
	private	Map<String, Multimap<String, CombinerParameter>> policySetCombinerParameters;

	private PolicySetIDReference reference;

	private PolicySet(PolicySetBuilder b)
	{
		super(b);
		this.reference = PolicySetIDReference.builder(id).version(b.version).build();
		this.policySetDefaults = b.policyDefaults;
		Preconditions.checkNotNull(b.combiningAlgorithm,
				"Policy decision combinging algorithm must be specified");
		this.combine = b.combiningAlgorithm;
		this.decisionRules = b.policies.build();
		ImmutableMap.Builder<String, Multimap<String, CombinerParameter>> forPolicySets = ImmutableMap.builder();
		ImmutableMap.Builder<String, Multimap<String, CombinerParameter>> forPolicies = ImmutableMap.builder();
		for(Entry<String, Multimap<String, CombinerParameter>> e : b.policySetCombinerParams.entrySet()){
			forPolicySets.put(e.getKey(), ImmutableListMultimap.copyOf(e.getValue()));
		}
		for(Entry<String, Multimap<String, CombinerParameter>> e : b.policyCombinerParams.entrySet()){
			forPolicies.put(e.getKey(), ImmutableListMultimap.copyOf(e.getValue()));
		}
		this.policySetCombinerParameters = forPolicySets.build();
		this.policyCombinerParameters = forPolicies.build();

	}

	public static PolicySetBuilder builder(String id){
		return new PolicySetBuilder(id);
	}

	@Override
	public CompositeDecisionRuleIDReference getReference() {
		return reference;
	}

	/**
	 * Gets all combiner parameters for a given policy identifier
	 *
	 * @param policyId a policy identifier
	 * @return a collection of combiner parameters
	 */
	public Collection<CombinerParameter> getPolicyCombinerParams(String policyId){
		Multimap<String, CombinerParameter> p = policyCombinerParameters.get(policyId);
		return  (p == null)?ImmutableList.<CombinerParameter>of():p.values();
	}

	/**
	 * Gets policy combiner parameter with a given name
	 *
	 * @param policyId a policy identifier
	 * @param name a parameter name
	 * @return a collection of combiner parameters
	 */
	public Collection<CombinerParameter> getPolicyCombinerParam(String policyId, String name){
		Multimap<String, CombinerParameter> p = policyCombinerParameters.get(policyId);
		return  (p == null)?ImmutableList.<CombinerParameter>of():p.get(name);
	}

	/**
	 * Gets all combiner parameters for a given policy set identifier
	 *
	 * @param policySetId a policy set identifier
	 * @return a collection of combiner parameters
	 */
	public Collection<CombinerParameter> getPolicySetCombinerParams(String policySetId){
		Multimap<String, CombinerParameter> p = policySetCombinerParameters.get(policySetId);
		return  (p == null)?ImmutableList.<CombinerParameter>of():p.values();
	}

	/**
	 * Gets policy set combiner parameter with a given name
	 *
	 * @param policySetId a policy set identifier
	 * @param name a parameter name
	 * @return a collection of combiner parameters
	 */
	public Collection<CombinerParameter> getPolicySetCombinerParam(String policyId, String name){
		Multimap<String, CombinerParameter> p = policySetCombinerParameters.get(policyId);
		return  (p == null)?ImmutableList.<CombinerParameter>of():p.get(name);
	}

	/**
	 * Gets policy set decision combining algorithm
	 *
	 * @return a decision combining algorithm
	 */
	public DecisionCombiningAlgorithm<CompositeDecisionRule> getPolicyDecisionCombiningAlgorithm(){
		return combine;
	}

	/**
	 * Gets policy set defaults
	 *
	 * @return {@link PolicySetDefaults}
	 */
	public PolicySetDefaults getDefaults() {
		return policySetDefaults;
	}

	/**
	 * Creates {@link EvaluationContext} to evaluate this policy
	 * set to be used in {@link this#isApplicable(EvaluationContext)}
	 * or {@link this#evaluate(EvaluationContext)}
	 *
	 * @param context a parent evaluation context
	 * @return {@link EvaluationContext} instance to evaluate
	 * this policy set
	 */
	@Override
	public EvaluationContext createContext(EvaluationContext context) {
		Preconditions.checkNotNull(context);
		if(context.getCurrentPolicySet() == this){
			return context;
		}
		return new PolicySetDelegatingEvaluationContext(context);
	}

	@Override
	protected final boolean isEvaluationContextValid(EvaluationContext context){
		return context.getCurrentPolicySet() == this;
	}

	@Override
	public final Decision evaluate(EvaluationContext context)
	{
		Preconditions.checkNotNull(context);
		Preconditions.checkArgument(isEvaluationContextValid(context));
		ConditionResult result = (condition == null)?ConditionResult.TRUE:condition.evaluate(context);
		if(log.isDebugEnabled()) {
			log.debug("PolicySet id=\"{}\" condition " +
					"evaluation result=\"{}\"", id, result);
		}
		if(result == ConditionResult.TRUE){
			Decision decision = combine.combine(context, decisionRules);
			if(!decision.isIndeterminate() ||
					decision != Decision.NOT_APPLICABLE){
				decision = evaluateAdvicesAndObligations(context, decision);
				context.addEvaluationResult(this, decision);
			}
			return decision;
		}
		if(result == ConditionResult.INDETERMINATE){
			Decision decision = combine.combine(context, decisionRules);
			switch(decision){
				case DENY : return Decision.INDETERMINATE_D;
				case PERMIT : return Decision.INDETERMINATE_P;
				case INDETERMINATE_DP: return Decision.INDETERMINATE_DP;
				default: return decision;
			}
		}
		return Decision.NOT_APPLICABLE;
	}

	public List<? extends CompositeDecisionRule> getDecisions() {
		return decisionRules;
	}

	@Override
	public void accept(PolicyVisitor v)
	{
		v.visitEnter(this);
		if(getTarget() != null){
			getTarget().accept(v);
		}
		if(policySetDefaults != null){
			policySetDefaults.accept(v);
		}
		for(DecisionRule decision : decisionRules){
			decision.accept(v);
		}
		for(ObligationExpression obligation : obligationExpressions){
			obligation.accept(v);
		}
		for(AdviceExpression advice : adviceExpressions){
			advice.accept(v);
		}
		v.visitLeave(this);
	}

	class PolicySetDelegatingEvaluationContext
		extends DelegatingEvaluationContext
	{
		private StatusCode evalStatus;
		
		/**
		 * Constructs delegating evaluation context
		 * which delegates all invocations to the enclosing
		 * policy set or root context to evaluate given
		 * policy set
		 *
		 * @param parentContext a parent context
		 */
		PolicySetDelegatingEvaluationContext(
				EvaluationContext parentContext){
			super(parentContext);
			Preconditions.checkArgument(
					parentContext.getCurrentPolicySet() != PolicySet.this);
			Preconditions.checkArgument(
					parentContext.getCurrentPolicy() == null);
		}

		@Override
		public PolicySet getCurrentPolicySet() {
			return PolicySet.this;
		}
		
		@Override
		public StatusCode getEvaluationStatus() {
			return evalStatus;
		}
		
		@Override
		public void setEvaluationStatus(StatusCode code) {
			Preconditions.checkNotNull(code);
			this.evalStatus = code;
		}

		@Override
		public XPathVersion getXPathVersion() {
			PolicySetDefaults defaults = PolicySet.this.getDefaults();
			if(defaults != null &&
					defaults.getXPathVersion() != null){
				return defaults.getXPathVersion();
			}
			return super.getXPathVersion();
		}
	}

	public final static class PolicySetBuilder extends BaseCompositeDecisionRuleBuilder<PolicySetBuilder>
	{
		private DecisionCombiningAlgorithm<CompositeDecisionRule> combiningAlgorithm;
		private PolicySetDefaults policyDefaults;
		private ImmutableList.Builder<CompositeDecisionRule> policies = ImmutableList.builder();

		private Map<String, Multimap<String, CombinerParameter>> policyCombinerParams = new LinkedHashMap<String, Multimap<String,CombinerParameter>>();
		private Map<String, Multimap<String, CombinerParameter>> policySetCombinerParams = new LinkedHashMap<String, Multimap<String,CombinerParameter>>();

		private PolicySetBuilder(String policySetId){
			super(policySetId);
		}


		public PolicySetBuilder withPolicyCombinerParameter(String policyId, CombinerParameter p){
			Preconditions.checkNotNull(policyId);
			Preconditions.checkNotNull(p);
			Multimap<String, CombinerParameter> params = policyCombinerParams.get(policyId);
			if(params == null){
				params = LinkedHashMultimap.create();
				policyCombinerParams.put(policyId, params);
			}
			params.put(p.getName(), p);
			return this;
		}

		public PolicySetBuilder withPolicySetCombinerParameter(String policySetId, CombinerParameter p){
			Preconditions.checkNotNull(policySetId);
			Preconditions.checkNotNull(p);
			Multimap<String, CombinerParameter> params = policySetCombinerParams.get(policySetId);
			if(params == null){
				params = LinkedHashMultimap.create();
				policySetCombinerParams.put(policySetId, params);
			}
			params.put(p.getName(), p);
			return this;
		}

		public PolicySetBuilder withoutPolicyCombinerParameters(){
			this.policyCombinerParams.clear();
			return this;
		}

		public PolicySetBuilder withoutPolicySetCombinerParameters(){
			this.policySetCombinerParams.clear();
			return this;
		}

		public PolicySetBuilder defaults(PolicySetDefaults defaults){
			this.policyDefaults = defaults;
			return this;
		}

		public PolicySetBuilder withoutDefaults(){
			this.policyDefaults = null;
			return this;
		}

		public PolicySetBuilder policy(Policy p){
			Preconditions.checkNotNull(p);
			this.policies.add(p);
			return this;
		}

		public PolicySetBuilder policySet(PolicySet p){
			Preconditions.checkNotNull(p);
			this.policies.add(p);
			return this;
		}

		public PolicySetBuilder compositeDecisionRules(Iterable<CompositeDecisionRule> rules)
		{
			this.policies.addAll(rules);
			return getThis();
		}

		public PolicySetBuilder compositeDecisionRules(CompositeDecisionRule ... rules)
		{
			this.policies.add(rules);
			return getThis();
		}

		public PolicySetBuilder withPolicy(Policy.PolicyBuilder b){
			Preconditions.checkNotNull(b);
			this.policies.add(b.create());
			return this;
		}

		public PolicySetBuilder withPolicy(PolicySet.PolicySetBuilder b){
			Preconditions.checkNotNull(b);
			this.policies.add(b.create());
			return this;
		}

		public PolicySetBuilder withoutRules(){
			this.policies = ImmutableList.builder();
			return this;
		}

		public PolicySetBuilder withCombiningAlgorithm(DecisionCombiningAlgorithm<CompositeDecisionRule> algo)
		{
			Preconditions.checkNotNull(algo);
			this.combiningAlgorithm = algo;
			return this;
		}

		@Override
		protected PolicySetBuilder getThis() {
			return this;
		}

		public PolicySet create(){
			return new PolicySet(this);
		}
	}
}