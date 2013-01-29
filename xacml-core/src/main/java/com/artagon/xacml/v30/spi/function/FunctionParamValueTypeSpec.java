package com.artagon.xacml.v30.spi.function;

import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artagon.xacml.v30.Expression;
import com.artagon.xacml.v30.ValueType;
import com.artagon.xacml.v30.pdp.FunctionParamSpec;
import com.google.common.base.Preconditions;

final class FunctionParamValueTypeSpec implements FunctionParamSpec
{
	private final static Logger log = LoggerFactory.getLogger(FunctionParamValueTypeSpec.class);

	private ValueType type;

	public FunctionParamValueTypeSpec(ValueType type){
		Preconditions.checkNotNull(type);
		this.type = type;
	}

	public ValueType getParamType(){
		return type;
	}

	@Override
	public boolean isVariadic() {
		return false;
	}

	@Override
	public boolean validate(ListIterator<Expression> it) {
		if(!it.hasNext()){
			log.debug("Parameter iterator at index=\"{}\" does not " +
					"have any elements left to iterate", it.previousIndex());
			return false;
		}
		Expression exp = it.next();
		log.debug("Validating expression=\"{}\" " +
				"against paramSpec=\"{}\"", exp, this);
		ValueType expType = exp.getEvaluatesTo();
		boolean valid = type.equals(expType);
		if(log.isDebugEnabled()){
			log.debug("Validating parameter at index=\"{}\" " +
					"is valid=\"{}\"", it.previousIndex(), valid);
			log.debug("Expecting parameter of type=\"{}\" found=\"{}\"",
					type, expType);
		}
		return valid;
	}

	@Override
	public boolean isValidParamType(ValueType type) {
		return this.type.equals(type);
	}

}
