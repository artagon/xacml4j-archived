package com.artagon.xacml.v3;


public class RuleCombinerParameters extends BaseDecisionCombinerParameters 
	implements PolicyElement
{
	private String ruleId; 
	
	public RuleCombinerParameters(
			Iterable<CombinerParameter> parameters) {
		super(parameters);
	}

	public String getRuleId() {
		return ruleId;
	}

	@Override
	public void accept(PolicyVisitor v) {
		v.visitEnter(this);
		for(CombinerParameter p : parameters.values()){
			p.accept(v);
		}
		v.visitLeave(this);
	}
}