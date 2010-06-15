package com.artagon.xacml.v3.policy.combine;

import java.util.List;

import com.artagon.xacml.v3.CompositeDecisionRule;
import com.artagon.xacml.v3.Decision;
import com.artagon.xacml.v3.EvaluationContext;
import com.artagon.xacml.v3.policy.spi.combine.BaseDecisionCombiningAlgorithm;

public class DenyOverridesPolicyLegacyCombineAlgorithm extends BaseDecisionCombiningAlgorithm<CompositeDecisionRule>
{
	private final static String ID = "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:deny-overrides";

	
	DenyOverridesPolicyLegacyCombineAlgorithm() {
		super(ID);
	}
	
	DenyOverridesPolicyLegacyCombineAlgorithm(String algorithmId) {
		super(algorithmId);
	}
	
	@Override
	public Decision combine(List<CompositeDecisionRule> rules,
			EvaluationContext context) {
		boolean atLeastOnePermit = false;
		for(CompositeDecisionRule r : rules){
			Decision d = evaluateIfApplicable(context, r);
			if(d == Decision.DENY){
				return d;
			}
			if(d == Decision.PERMIT){
				atLeastOnePermit = true;
				continue;
			}
			if(d == Decision.NOT_APPLICABLE){
				continue;
			}
			if(d.isIndeterminate()){
				return Decision.DENY;
			}
		}
		if(atLeastOnePermit){
			return Decision.PERMIT;
		}
		return Decision.NOT_APPLICABLE;
	}	
}
