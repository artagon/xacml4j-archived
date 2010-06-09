package com.artagon.xacml.v3.types;

import com.artagon.xacml.v3.AttributeValueType;
import com.artagon.xacml.v3.BagOfAttributeValuesType;

public interface IntegerType extends AttributeValueType
{
	IntegerValue create(Object value, Object ...params);
	IntegerValue fromXacmlString(String v, Object ...params);
	BagOfAttributeValuesType<IntegerValue> bagOf();
	
	final class IntegerValue extends BaseAttributeValue<Long>
	{
		public IntegerValue(IntegerType type, Long value) {
			super(type, value);
		}
		
	}
}