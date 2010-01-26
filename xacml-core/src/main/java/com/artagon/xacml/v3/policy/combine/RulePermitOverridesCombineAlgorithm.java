package com.artagon.xacml.v3.policy.combine;

import com.artagon.xacml.v3.policy.CompositeDecision;

public class RulePermitOverridesCombineAlgorithm extends PermitOverrides<CompositeDecision> 
{
	public final static String ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-overrides";
	
	public RulePermitOverridesCombineAlgorithm() {
		super(ID);
	}

}