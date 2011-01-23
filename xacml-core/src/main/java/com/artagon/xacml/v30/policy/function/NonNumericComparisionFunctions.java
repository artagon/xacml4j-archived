package com.artagon.xacml.v30.policy.function;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import com.artagon.xacml.v30.EvaluationContext;
import com.artagon.xacml.v30.spi.function.XacmlFuncParam;
import com.artagon.xacml.v30.spi.function.XacmlFuncParamEvaluationContext;
import com.artagon.xacml.v30.spi.function.XacmlFuncReturnType;
import com.artagon.xacml.v30.spi.function.XacmlFuncSpec;
import com.artagon.xacml.v30.spi.function.XacmlFunctionProvider;
import com.artagon.xacml.v30.types.BooleanType;
import com.artagon.xacml.v30.types.BooleanValue;
import com.artagon.xacml.v30.types.DateTimeValue;
import com.artagon.xacml.v30.types.DateValue;
import com.artagon.xacml.v30.types.StringValue;
import com.artagon.xacml.v30.types.TimeValue;
import com.google.common.base.Preconditions;

@XacmlFunctionProvider(description="XACML non-numeric comparision functions")
public class NonNumericComparisionFunctions 
{
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:string-greater-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue greatherThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue b)
	{
		return BooleanType.BOOLEAN.create(a.getValue().compareTo(b.getValue()) > 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:string-greater-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue greatherThanOrEqual(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue b)
	{
		int r = a.getValue().compareTo(b.getValue());
		return BooleanType.BOOLEAN.create(r > 0 || r == 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:string-less-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue lessThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue b)
	{
		int r = a.getValue().compareTo(b.getValue());
		return BooleanType.BOOLEAN.create(r < 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:string-less-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue lessThanOrEqual(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#string")StringValue b)
	{
		int r = a.getValue().compareTo(b.getValue());
		return BooleanType.BOOLEAN.create(r < 0 || r == 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-greater-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue greatherThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue b)
	{
		return BooleanType.BOOLEAN.create(a.compareTo(b) > 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-greater-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue greatherThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue b)
	{
		int r = a.compareTo(b);
		return BooleanType.BOOLEAN.create(r  > 0 || r == 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-less-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue lessThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue b)
	{
		return BooleanType.BOOLEAN.create(a.compareTo(b) < 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-less-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue lessThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue b)
	{
		int r = a.compareTo(b);
		return BooleanType.BOOLEAN.create(r  < 0 || r == 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:time-in-range")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue timeInRange(
			@XacmlFuncParamEvaluationContext EvaluationContext context,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue b,
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#time")TimeValue c)
	{
		XMLGregorianCalendar ac = (XMLGregorianCalendar)a.getValue().clone();
		XMLGregorianCalendar bc = (XMLGregorianCalendar)b.getValue().clone();
		XMLGregorianCalendar cc = (XMLGregorianCalendar)c.getValue().clone();
		if(ac.getTimezone() == DatatypeConstants.FIELD_UNDEFINED){
			ac.setTimezone(context.getTimeZone().getRawOffset() / (1000 * 60));
		}
		if(bc.getTimezone() == DatatypeConstants.FIELD_UNDEFINED){
			bc.setTimezone(ac.getTimezone());
		}
		if(cc.getTimezone() == DatatypeConstants.FIELD_UNDEFINED){
			cc.setTimezone(ac.getTimezone());
		}
		Preconditions.checkArgument(b.compareTo(c) <= 0);
		return BooleanType.BOOLEAN.create(ac.compare(bc) >= 0 && ac.compare(cc) <= 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:date-greater-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue greatherThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateValue b)
	{
		return BooleanType.BOOLEAN.create(a.compareTo(b) > 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:date-greater-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue greatherThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateValue b)
	{
		int r = a.compareTo(b);
		return BooleanType.BOOLEAN.create(r  > 0 || r == 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:date-less-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue lessThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateValue b)
	{
		return BooleanType.BOOLEAN.create(a.compareTo(b) < 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:date-less-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue lessThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#date")DateValue b)
	{
		int r = a.compareTo(b);
		return BooleanType.BOOLEAN.create(r  < 0 || r == 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue greatherThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeValue b)
	{
		return BooleanType.BOOLEAN.create(a.compareTo(b) > 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:dateTime-greater-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue greatherThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeValue b)
	{
		int r = a.compareTo(b);
		return BooleanType.BOOLEAN.create(r  > 0 || r == 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue lessThan(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeValue b)
	{
		return BooleanType.BOOLEAN.create(a.compareTo(b) < 0);
	}
	
	@XacmlFuncSpec(id="urn:oasis:names:tc:xacml:1.0:function:dateTime-less-than-or-equal")
	@XacmlFuncReturnType(typeId="http://www.w3.org/2001/XMLSchema#boolean")
	public static BooleanValue lessThanOrEquals(
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeValue a, 
			@XacmlFuncParam(typeId="http://www.w3.org/2001/XMLSchema#dateTime")DateTimeValue b)
	{
		int r = a.compareTo(b);
		return BooleanType.BOOLEAN.create(r  < 0 || r == 0);
	}
}