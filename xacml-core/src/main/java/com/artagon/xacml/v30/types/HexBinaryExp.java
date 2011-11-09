package com.artagon.xacml.v30.types;

import com.artagon.xacml.v30.BinaryValue;

public final class HexBinaryExp extends 
	BaseAttributeExp<BinaryValue>
{
	private static final long serialVersionUID = 8087916652227967791L;

	HexBinaryExp(HexBinaryType type, BinaryValue value) {
		super(type, value);
	}
	
	@Override
	public String toXacmlString() {
		return getValue().toHexEncoded();
	}
}

