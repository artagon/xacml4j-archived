package com.artagon.xacml.v3.policy;

import com.artagon.xacml.v3.Expression;
import com.artagon.xacml.v3.ValueType;
import com.google.common.base.Preconditions;

final class FixedReturnTypeFunctionReturnTypeResolver implements FunctionReturnTypeResolver
{
	private ValueType returnType;
	
	FixedReturnTypeFunctionReturnTypeResolver(ValueType returnType){
		Preconditions.checkNotNull(returnType);
		this.returnType = returnType;
	}
	@Override
	public ValueType resolve(FunctionSpec spec, 
			Expression... arguments) {
		return returnType;
	}
}