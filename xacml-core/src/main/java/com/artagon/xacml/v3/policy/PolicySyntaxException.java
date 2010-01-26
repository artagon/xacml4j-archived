package com.artagon.xacml.v3.policy;

import com.artagon.xacml.v3.StatusId;

public class PolicySyntaxException extends PolicyException
{
	private static final long serialVersionUID = 5110785800396453262L;
	
	private String policyId;

	public PolicySyntaxException(String policyId, String template,
			Object... arguments) {
		super(StatusId.SYNTAX_ERROR, template, arguments);
	}

	/**
	 * Gets policy identifier
	 * 
	 * @return policy identifier
	 */
	public String getPolicyId(){
		return policyId;
	}
}