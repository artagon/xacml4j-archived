package com.artagon.xacml.v3;

public interface AttributeValue extends ValueExpression
{
	/**
	 * Converts this XACML attribute value
	 * to {@link String}
	 * 
	 * @return this attribute value as {@link String}
	 */
	String toXacmlString();
}