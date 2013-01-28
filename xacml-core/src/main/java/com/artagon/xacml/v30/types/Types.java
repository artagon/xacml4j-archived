package com.artagon.xacml.v30.types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.artagon.xacml.v30.AttributeCategories;
import com.artagon.xacml.v30.AttributeCategory;
import com.artagon.xacml.v30.AttributeExp;
import com.artagon.xacml.v30.AttributeExpType;
import com.artagon.xacml.v30.XacmlSyntaxException;
import com.google.common.base.Preconditions;

public interface Types
{
	/**
	 * Gets type by given type identifier
	 *
	 * @param typeId a type identifier
	 * @return {@link AttributeExpType} a data type instance
	 */
	AttributeExpType getType(String typeId);
	AttributeExp valueOf(String typeId, Object value)
			throws XacmlSyntaxException;

	AttributeExp valueOf(String typeId, Object value, Map<QName, String> attrs)
			throws XacmlSyntaxException;

	public final class Builder
	{
		private final static Logger log = LoggerFactory.getLogger(Builder.class);

		private final static String XPATH_CATEGORY_ATTR_NAME = "XPathCategory";

		private Map<String, AttributeExpType> types;


		private Builder(){
			this.types = new ConcurrentHashMap<String, AttributeExpType>();
		}

		public Builder defaultTypes()
		{
			addType(AnyURIType.ANYURI);
			addType(Base64BinaryType.BASE64BINARY);
			addType(BooleanType.BOOLEAN);
			addType(DateTimeType.DATETIME);
			addType(DateType.DATE);
			addType(DayTimeDurationType.DAYTIMEDURATION);
			addType(DNSNameType.DNSNAME);
			addType(DoubleType.DOUBLE);
			addType(HexBinaryType.HEXBINARY);
			addType(IntegerType.INTEGER);
			addType(IPAddressType.IPADDRESS);
			addType(RFC822NameType.RFC822NAME);
			addType(StringType.STRING);
			addType(TimeType.TIME);
			addType(X500NameType.X500NAME);
			addType(XPathExpType.XPATHEXPRESSION);
			addType(YearMonthDurationType.YEARMONTHDURATION);

			// Legacy XACML 2.0 type mappings/aliases
			addType("urn:oasis:names:tc:xacml:2.0:data-type:xpathExpression",  XPathExpType.XPATHEXPRESSION);
			addType("urn:oasis:names:tc:xacml:2.0:data-type:xpath-expression", XPathExpType.XPATHEXPRESSION);
			addType("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration", DayTimeDurationType.DAYTIMEDURATION);
			addType("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration", YearMonthDurationType.YEARMONTHDURATION);
			return this;
		}

		public Builder type(AttributeExpType type){
			addType(type);
			return this;
		}

		public static Builder builder(){
			return new Builder();
		}

		public Types create(){
			return new Types() {
				@Override
				public AttributeExpType getType(String typeId) {
					return types.get(typeId);
				}

				@Override
				public AttributeExp valueOf(String typeId, Object value)
						throws XacmlSyntaxException {
					AttributeExpType type = getType(typeId);
					if(type == null){
						throw new XacmlSyntaxException("Unknown data type=\"%s\"", typeId);
					}
					return type.create(value);
				}

				@Override
				public AttributeExp valueOf(String typeId,
						Object value, Map<QName, String> values) throws XacmlSyntaxException
				{
					AttributeExpType type = getType(typeId);
					try {
						return type.create(value, getXPathCategory(values));
					} catch (Exception e) {
						throw new XacmlSyntaxException(e);
					}
				}
			};
		}

		private void addType(AttributeExpType type){
			addType(type.getDataTypeId(), type);
		}

		private void addType(String typeId, AttributeExpType type){
			if(log.isDebugEnabled()){
				log.debug("Adding typeId=\"{}\"", typeId);
			}
			Preconditions.checkArgument(!types.containsKey(typeId));
			this.types.put(typeId, type);
		}

		private static AttributeCategory getXPathCategory(Map<QName, String> attr)
				throws XacmlSyntaxException
			{
				for (QName n : attr.keySet()) {
					if (n.getLocalPart().equals(XPATH_CATEGORY_ATTR_NAME)) {
						return AttributeCategories.parse(attr.get(n));
					}
				}
				return null;
			}
	}
}