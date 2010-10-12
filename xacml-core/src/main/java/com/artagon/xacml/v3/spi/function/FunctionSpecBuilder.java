package com.artagon.xacml.v3.spi.function;

import java.util.LinkedList;
import java.util.List;

import com.artagon.xacml.v3.FunctionParamSpec;
import com.artagon.xacml.v3.ValueType;
import com.google.common.base.Preconditions;

public final class FunctionSpecBuilder 
{
	private String functionId;
	private String legacyId;
	private List<FunctionParamSpec> paramSpec;
	private boolean hadVarArg = false;
	private boolean lazyArgumentEvaluation;
	
	private FunctionSpecBuilder(String functionId){
		this(functionId, null);
	}
	
	private FunctionSpecBuilder(String functionId, String legacyId){
		Preconditions.checkNotNull(functionId);
		this.functionId = functionId;
		this.legacyId = legacyId;
		this.paramSpec = new LinkedList<FunctionParamSpec>();
	}
	
	public static FunctionSpecBuilder  create(String functionId, String legacyId){
		return new FunctionSpecBuilder(functionId, legacyId);
	}
	
	public static FunctionSpecBuilder  create(String functionId){
		return create(functionId, null);
	}
	
	public FunctionSpecBuilder withParamFunctionReference()
	{
		this.paramSpec.add(new FunctionParamFuncReferenceSpec());
		return this;
	}
	
	public FunctionSpecBuilder withParam(ValueType type){
		Preconditions.checkNotNull(type);
		Preconditions.checkState(!hadVarArg, 
				String.format("Can't add parameter after variadic parameter"));
		this.paramSpec.add(new FunctionParamValueTypeSpec(type));
		return this;
	}
	
	public FunctionSpecBuilder withLazyArgumentsEvaluation(){
		this.lazyArgumentEvaluation = true;
		return this;
	}
	
	public FunctionSpecBuilder withParam(ValueType type, int min, int max){
		Preconditions.checkNotNull(type);
		Preconditions.checkArgument(min >= 0 && max > 0);
		Preconditions.checkArgument(max > min);
		Preconditions.checkArgument(max - min > 1, "Max and min should be different at least by 1");
		hadVarArg = true;
		this.paramSpec.add(new FunctionParamValueTypeSequenceSpec(min, max, type));
		return this;
	}
	
	public FunctionSpecBuilder withParamAnyBag() {
		this.paramSpec.add(new FunctionParamAnyBagSpec());
		return this;
	}
	
	public FunctionSpecBuilder withParamAnyAttribute() {
		this.paramSpec.add(new FunctionParamAnyAttributeSpec());
		return this;
	}

	public DefaultFunctionSpec build(FunctionReturnTypeResolver returnType, 
			FunctionInvocation invocation) {
		return new DefaultFunctionSpec(functionId, 
				legacyId, paramSpec, returnType, invocation, lazyArgumentEvaluation);
	}
	
	public DefaultFunctionSpec build(FunctionReturnTypeResolver returnType, 
			FunctionParametersValidator validator,
			FunctionInvocation invocation) {
		return new DefaultFunctionSpec(functionId, 
				legacyId, paramSpec, returnType, 
				invocation, 
				validator, 
				lazyArgumentEvaluation);
	}

	public DefaultFunctionSpec build(ValueType returnType,
			FunctionInvocation invocation) {
		return build(
				new FixedReturnTypeFunctionReturnTypeResolver(returnType), 
				invocation);
	}
	
	public DefaultFunctionSpec build(ValueType returnType,
			FunctionParametersValidator validator,
			FunctionInvocation invocation) {
		return build(
				new FixedReturnTypeFunctionReturnTypeResolver(returnType), 
				validator,
				invocation);
	}
	
}
