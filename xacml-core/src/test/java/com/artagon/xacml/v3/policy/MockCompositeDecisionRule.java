package com.artagon.xacml.v3.policy;

import java.util.Collections;
import java.util.List;

import com.artagon.xacml.v3.Decision;

public class MockCompositeDecisionRule extends BaseCompositeDecisionRule 
{
	private Decision d;
	
	public MockCompositeDecisionRule(Decision d, MatchResult m) {
		super("MockCompisiteDecision", new MockTarget(m), 
				Collections.<AdviceExpression>emptyList(),
				Collections.<ObligationExpression>emptyList());
		this.d = d;
	}

	@Override
	public List<? extends DecisionRule> getDecisions() {
		return Collections.emptyList();
	}

	@Override
	protected Decision doEvaluate(EvaluationContext context) {
		return d;
	}
	
	@Override
	public EvaluationContext createContext(EvaluationContext context) {
		return context;
	}
	
	@Override
	protected boolean isEvaluationContextValid(EvaluationContext context) {
		return true;
	}

	@Override
	public void accept(PolicyVisitor v) {
		// TODO Auto-generated method stub
		
	}	
	
	
}