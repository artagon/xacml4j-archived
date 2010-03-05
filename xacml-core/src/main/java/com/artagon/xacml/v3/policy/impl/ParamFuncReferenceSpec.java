package com.artagon.xacml.v3.policy.impl;

import java.util.ListIterator;

import com.artagon.xacml.util.Preconditions;
import com.artagon.xacml.v3.XacmlObject;
import com.artagon.xacml.v3.policy.Expression;
import com.artagon.xacml.v3.policy.FunctionReference;
import com.artagon.xacml.v3.policy.ValueType;

public class ParamFuncReferenceSpec extends XacmlObject implements ParamSpec
{
	private FunctionFamily family;
	
	public ParamFuncReferenceSpec(FunctionFamily family){
		Preconditions.checkNotNull(family);
		this.family = family;
	}
	
	@Override
	public boolean isValidParamType(ValueType type) {
		return false;
	}
	
	public boolean isVariadic() {
		return false;
	}
	
	@Override
	public boolean validate(ListIterator<Expression> it) {
		Expression exp = it.next();
		if(exp instanceof FunctionReference){
			FunctionReference fexp = (FunctionReference)exp;
			return family.isMemeberOf(fexp.getSpec());
		}
		return false;
	}
}