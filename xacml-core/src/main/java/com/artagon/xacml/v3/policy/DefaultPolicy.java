package com.artagon.xacml.v3.policy;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artagon.xacml.util.Preconditions;
import com.artagon.xacml.v3.DecisionResult;

public final class DefaultPolicy extends BaseCompositeDecision implements Policy
{
	private final static Logger log = LoggerFactory.getLogger(DefaultPolicy.class);
	
	private List<Rule> rules;
	private Map<String, VariableDefinition> variableDefinitions;
	private DecisionCombiningAlgorithm<Rule> combine;
	
	/**
	 * Creates policy with a given identifier, target, variables.
	 * decision combining algorithm, rules and advice and obligation
	 * expressions
	 * 
	 * @param policyId a policy identifier
	 * @param target a policy target
	 * @param variables a policy variable definitions
	 * @param combine a policy rules combine algorithm
	 * @param rules a policy rules
	 * @param adviceExpressions an advice expressions
	 * @param obligationExpressions an obligation expressions
	 */
	public DefaultPolicy(
			String policyId, 
			Target target, 
			Collection<VariableDefinition> variables, 
			DecisionCombiningAlgorithm<Rule> combine,
			Collection<Rule> rules, 
			Collection<AdviceExpression> adviceExpressions,
			Collection<ObligationExpression> obligationExpressions){
		super(policyId, target, adviceExpressions, obligationExpressions);
		Preconditions.checkNotNull(variables);
		Preconditions.checkNotNull(rules);
		Preconditions.checkNotNull(combine);
		this.rules = new LinkedList<Rule>(rules);
		this.combine = combine;
		this.variableDefinitions = new ConcurrentHashMap<String, VariableDefinition>(variables.size());
		for(VariableDefinition varDef : variables){
			this.variableDefinitions.put(varDef.getVariableId(), varDef);
		}
	}
	
	public DefaultPolicy(String policyId, 
			Collection<Rule> rules, DecisionCombiningAlgorithm<Rule> combine)
	{
		this(policyId, null, 
				Collections.<VariableDefinition>emptyList(),
				combine,
				rules,
				Collections.<AdviceExpression>emptyList(),
				Collections.<ObligationExpression>emptyList());
	}
	
	@Override
	public Collection<VariableDefinition> getVariableDefinitions(){
		return Collections.unmodifiableCollection(variableDefinitions.values());
	}
	
	@Override
	public VariableDefinition getVariableDefinition(String variableId){
		return variableDefinitions.get(variableId);
	}
	
	@Override
	public List<? extends Decision> getDecisions() {
		return Collections.unmodifiableList(rules);
	}
	
	/**
	 * Implementation creates {@link PolicyDelegatingEvaluationContext}
	 */
	@Override
	public EvaluationContext createContext(EvaluationContext context) {
		return new PolicyDelegatingEvaluationContext(context, this);
	}

	@Override
	protected boolean isEvaluationContextValid(EvaluationContext context){
		return context.getCurrentPolicy() == this;
	}
	
	@Override
	protected DecisionResult doEvaluate(EvaluationContext context)
	{
		log.debug("Evaluating policy=\"{}\", combine=\"{}\"", 
				getId(), combine.getId());
		return combine.combine(rules, context);
	}

	@Override
	public void accept(PolicyVisitor v) {
		v.visitEnter(this);
		if(getTarget() != null){
			getTarget().accept(v);
		}
		combine.accept(v);
		for(VariableDefinition var : variableDefinitions.values()){
			var.accept(v);
		}
		for(Decision rule : rules){
			rule.accept(v);
		}
		for(ObligationExpression obligation : getObligationExpressions()){
			obligation.accept(v);
		}
		for(AdviceExpression advice : getAdviceExpressions()){
			advice.accept(v);
		}
		v.visitLeave(this);
	}	
}