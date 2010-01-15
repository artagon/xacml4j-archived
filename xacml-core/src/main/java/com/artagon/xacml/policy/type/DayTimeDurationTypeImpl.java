package com.artagon.xacml.policy.type;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


import com.artagon.xacml.DataTypes;
import com.artagon.xacml.policy.BaseAttributeDataType;
import com.artagon.xacml.policy.type.DayTimeDurationType.DayTimeDurationValue;
import com.artagon.xacml.util.Preconditions;

final class DayTimeDurationTypeImpl extends BaseAttributeDataType<DayTimeDurationValue> implements DayTimeDurationType
{
	private DatatypeFactory xmlDataTypesFactory;
	
	public DayTimeDurationTypeImpl()
	{
		super(DataTypes.DAYTIMEDURATION, Duration.class);
		try{
			this.xmlDataTypesFactory = DatatypeFactory.newInstance();
		}catch(DatatypeConfigurationException e){
			throw new IllegalStateException(e);
		}
	}

	@Override
	public DayTimeDurationValue fromXacmlString(String v) {
		Preconditions.checkNotNull(v);
		Duration dayTimeDuration = xmlDataTypesFactory.newDurationDayTime(v);
		return new DayTimeDurationValue(this, dayTimeDuration);
	}
	
	@Override
	public DayTimeDurationValue create(Object any){
		Preconditions.checkNotNull(any);
		Preconditions.checkArgument(isConvertableFrom(any), String.format(
				"Value=\"%s\" of class=\"%s\" " +
				"can't ne converted to XACML \"date\" type", 
				any, any.getClass()));
		if(String.class.isInstance(any)){
			return fromXacmlString((String)any);
		}
		return new DayTimeDurationValue(this, (Duration)any);
	}

}

