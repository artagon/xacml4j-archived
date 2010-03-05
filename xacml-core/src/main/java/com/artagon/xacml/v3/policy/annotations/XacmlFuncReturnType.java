package com.artagon.xacml.v3.policy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.artagon.xacml.v3.policy.type.DataTypes;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XacmlFuncReturnType 
{
	DataTypes type();
	boolean isBag() default false;
}