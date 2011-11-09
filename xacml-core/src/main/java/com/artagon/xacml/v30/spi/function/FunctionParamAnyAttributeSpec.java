package com.artagon.xacml.v30.spi.function;

import java.util.ListIterator;

import com.artagon.xacml.v30.pdp.AttributeExpType;
import com.artagon.xacml.v30.pdp.Expression;
import com.artagon.xacml.v30.pdp.FunctionParamSpec;
import com.artagon.xacml.v30.pdp.ValueType;

final class FunctionParamAnyAttributeSpec implements FunctionParamSpec
{
	@Override
	public boolean isValidParamType(ValueType type) {
		return (type instanceof AttributeExpType);
	}

	@Override
	public boolean isVariadic() {
		return false;
	}

	@Override
	public boolean validate(ListIterator<Expression> it) {
		if(!it.hasNext()){
			return false;
		}
		Expression exp = it.next();
		return isValidParamType(exp.getEvaluatesTo());
	}
	
}
