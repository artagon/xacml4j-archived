package com.artagon.xacml.v3.policy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artagon.xacml.v3.policy.type.DataTypes;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface XacmlParamVarArg 
{
	DataTypes type();
	int min() default 2;
	int max() default Integer.MAX_VALUE;
	boolean isBag() default false;
}