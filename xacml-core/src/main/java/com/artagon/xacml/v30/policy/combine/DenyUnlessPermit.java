package com.artagon.xacml.v30.policy.combine;

import static com.artagon.xacml.v30.spi.combine.DecisionCombingingAlgorithms.evaluateIfApplicable;

import java.util.List;

import com.artagon.xacml.v30.pdp.Decision;
import com.artagon.xacml.v30.pdp.DecisionRule;
import com.artagon.xacml.v30.pdp.EvaluationContext;
import com.artagon.xacml.v30.spi.combine.BaseDecisionCombiningAlgorithm;
import com.artagon.xacml.v30.spi.combine.XacmlPolicyDecisionCombingingAlgorithm;
import com.artagon.xacml.v30.spi.combine.XacmlRuleDecisionCombingingAlgorithm;

public class DenyUnlessPermit<D extends DecisionRule> extends BaseDecisionCombiningAlgorithm<D>
{
	protected DenyUnlessPermit(String algorithmId){
		super(algorithmId);
	}
	
	public final Decision combine(EvaluationContext context, List<D> decisions)
	{
		return doCombine(context, decisions);
	}
	
	@XacmlPolicyDecisionCombingingAlgorithm("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit")
	@XacmlRuleDecisionCombingingAlgorithm("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit")
	public final static <D extends DecisionRule> Decision doCombine(EvaluationContext context, List<D> decisions)
	{
		for(D d : decisions){
			Decision decision = evaluateIfApplicable(context, d);
			if(decision == Decision.PERMIT){
				return decision;
			}
		}
		return Decision.DENY;
	}
	
}
