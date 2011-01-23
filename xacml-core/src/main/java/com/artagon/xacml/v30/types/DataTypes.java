package com.artagon.xacml.v30.types;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.artagon.xacml.v30.AttributeCategories;
import com.artagon.xacml.v30.AttributeCategory;
import com.artagon.xacml.v30.AttributeValue;
import com.artagon.xacml.v30.AttributeValueType;
import com.artagon.xacml.v30.XacmlSyntaxException;

/**
 * An enumeration  of all XACML 2.0 & 3.0 data types
 * 
 * @author Giedrius Trumpickas
 */
public enum DataTypes
{
	/** 
	 * XACML DataType: <b>http://www.w3.org/2001/XMLSchema#anyURI</b> 
	 * 
	 * @see {@link AnyURIType}
	 */
	ANYURI(AnyURIType.ANYURI),

	/** 
	 * XACML DataType: <b>http://www.w3.org/2001/XMLSchema#boolean</b> 
	 * 
	 * @see {@link BooleanType}
	 */
	BOOLEAN(BooleanType.BOOLEAN),

	/** 
	 * XACML DataType: <b>http://www.w3.org/2001/XMLSchema#base64Binary</b> 
	 * 
	 * @see {@link Base64BinaryType}
	 */
	BASE64BINARY(Base64BinaryType.BASE64BINARY),

	/** 
	 * XACML DataType: <b>http://www.w3.org/2001/XMLSchema#date</b> 
	 * 
	 * @see {@link DateType}
	 */
	DATE(DateType.DATE),

	/** 
	 * XACML DataType: <b>http://www.w3.org/2001/XMLSchema#dateTime</b> 
	 * 
	 * @see {@link DateTimeType}
	 */
	DATETIME(DateTimeType.DATETIME),

	/** XACML DataType: <b>http://www.w3.org/2001/XMLSchema#dayTimeDuration</b> */
	DAYTIMEDURATION(DayTimeDurationType.DAYTIMEDURATION),

	DNSNAME(DNSNameType.DNSNAME),

	/** XACML DataType: <b>http://www.w3.org/2001/XMLSchema#double</b> */
	DOUBLE(DoubleType.DOUBLE),

	/** XACML DataType: <b>http://www.w3.org/2001/XMLSchema#hexBinary</b> */
	HEXBINARY(HexBinaryType.HEXBINARY),

	/** XACML DataType: <b>http://www.w3.org/2001/XMLSchema#integer</b> */
	INTEGER(IntegerType.INTEGER),

	IPADDRESS(IPAddressType.IPADDRESS),

	/** XACML DataType: <b>http://www.w3.org/2001/XMLSchema#string</b> */
	RFC822NAME(RFC822NameType.RFC822NAME),

	/** XACML DataType: <b>http://www.w3.org/2001/XMLSchema#string</b> */
	STRING(StringType.STRING),

	/** XACML DataType: <b>http://www.w3.org/2001/XMLSchema#time</b> */
	TIME(TimeType.TIME),

	X500NAME(X500NameType.X500NAME),

	/**
	 * XACML DataType:
	 * <b>urn:oasis:names:tc:xacml:3.0:data-type:xpathExpression</b>
	 */
	XPATHEXPRESSION(XPathExpressionType.XPATHEXPRESSION),

	/**
	 * XACML DataType: <b>http://www.w3.org/2001/XMLSchema#yearMonthDuration</b>
	 */
	YEARMONTHDURATION(YearMonthDurationType.YEARMONTHDURATION);

	private static final Map<String, AttributeValueType> BY_TYPE_ID = new HashMap<String, AttributeValueType>();

	static {
		for (DataTypes t : EnumSet.allOf(DataTypes.class)) {
			BY_TYPE_ID.put(t.getDataType().getDataTypeId(), t.getDataType());
		}
		// legacy XACML 2.0 type mappings
		BY_TYPE_ID.put(
				"urn:oasis:names:tc:xacml:2.0:data-type:xpathExpression",
				DataTypes.XPATHEXPRESSION.getDataType());
		BY_TYPE_ID.put(
				"urn:oasis:names:tc:xacml:2.0:data-type:xpath-expression",
				DataTypes.XPATHEXPRESSION.getDataType());
		BY_TYPE_ID
				.put("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration",
						DataTypes.DAYTIMEDURATION.getDataType());
		BY_TYPE_ID
				.put("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration",
						DataTypes.YEARMONTHDURATION.getDataType());
	}

	private AttributeValueType type;

	private DataTypes(AttributeValueType type) {
		this.type = type;
	}
	
	
	/**
	 * Gets underlying XACML {@link AttributeValueType}
	 * 
	 * @return {@link AttributeValueType}
	 */
	public AttributeValueType getDataType() {
		return type;
	}
	
	/**
	 * Gets {@link AttributeValueType} instance
	 * via type identifier
	 * 
	 * @param <T>
	 * @param typeId a type identifier
	 * @return {@link AttributeValueType}
	 * @throws XacmlSyntaxException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends AttributeValueType> T getType(String typeId) 
		throws XacmlSyntaxException
	{
		AttributeValueType type = BY_TYPE_ID.get(typeId);
		if(type == null){
			throw new XacmlSyntaxException(
					"Unknow XACML typeId=\"%s\"", typeId);
		}
		return (T) type;
	}

	/**
	 * Creates attribute value of given type
	 * 
	 * @param typeId a type identifier
	 * @param value an attribute value
	 * @return {@link AttributeValue} instance
	 * @throws XacmlSyntaxException if attribute can 
	 * not be created from a given value or given type
	 * identifier does not represent valid XACML data type
	 */
	public static AttributeValue createAttributeValue(String typeId, Object value)
			throws XacmlSyntaxException 
	{
		return createAttributeValue(typeId, value, Collections.<QName, String> emptyMap());
	}

	public static AttributeValue createAttributeValue(
			String typeId,
			Object value,
			Object... values) throws XacmlSyntaxException {
		AttributeValueType type = getType(typeId);
		try {
			return type.create(value, values);
		} catch (Exception e) {
			throw new XacmlSyntaxException(e);
		}
	}

	public static AttributeValue createAttributeValue(String typeId, 
			Object value, Map<QName, String> values) throws XacmlSyntaxException 
	{
		AttributeValueType type = getType(typeId);
		try {
			return type.create(value, getXPathCategory(values));
		} catch (Exception e) {
			throw new XacmlSyntaxException(e);
		}
	}

	private static AttributeCategory getXPathCategory(Map<QName, String> attr) 
		throws XacmlSyntaxException
	{
		for (QName n : attr.keySet()) {
			if (n.getLocalPart().equals("XPathCategory")) {
				return AttributeCategories.parse(attr.get(n));
			}
		}
		return null;
	}
}