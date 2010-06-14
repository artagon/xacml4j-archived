package com.artagon.xacml.v3;

import com.google.common.base.Preconditions;

/**
 * Conveys a single parameter for a policy- or rule-combining algorithm.
 * 
 * @author Giedrius Trumpickas
 */
public class CombinerParameter extends XacmlObject implements PolicyElement
{
	private String name;
	private AttributeValue value;
	
	/**
	 * Constructs decision combining parameter
	 * 
	 * @param name a parameter name
	 * @param value a parameter value
	 */
	public CombinerParameter(String name, AttributeValue value)
	{
		Preconditions.checkNotNull(name);
		Preconditions.checkNotNull(value);
		this.name = name;
		this.value = value;
	}
	
	public final String getName(){
		return name;
	}
	
	public final AttributeValue getValue(){
		return value;
	}

	@Override
	public void accept(PolicyVisitor v) {
		v.visitEnter(this);
		v.visitLeave(this);
	}	
}