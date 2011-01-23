package com.artagon.xacml.v30.policy.combine;

import com.artagon.xacml.v30.Rule;

final class PermitUnlessDenyRuleCombiningAlgorithm extends PermitUnlessDeny<Rule>
{
	private final static String ID = "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny";

	public PermitUnlessDenyRuleCombiningAlgorithm() {
		super(ID);
	}	
}