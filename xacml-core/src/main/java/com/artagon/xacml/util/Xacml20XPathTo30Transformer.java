package com.artagon.xacml.util;

import com.artagon.xacml.v3.AttributeCategoryId;
import com.artagon.xacml.v3.types.XacmlDataTypes;
import com.artagon.xacml.v3.types.StringType.StringValue;
import com.artagon.xacml.v3.types.XPathExpressionType.XPathExpressionValue;

public class Xacml20XPathTo30Transformer 
{
	private final static String REQUEST_ELEMENT_NAME = "Request";
	private final static String RESOURCE_ELEMENT_NAME = "Resource";
	private final static String RESOURCE_CONTENT_ELEMENT_NAME = "ResourceContent";
	
	
	public static XPathExpressionValue fromXacml20String(StringValue path)
	{
		XPathExpressionValue xpathExp = XacmlDataTypes.XPATHEXPRESSION.create(
				transform20PathTo30(path.getValue()), AttributeCategoryId.RESOURCE);
		return xpathExp;
	}
	
	public static String transform20PathTo30(String xpath)
	{
		StringBuffer buf = new StringBuffer(xpath);
		int firstIndex = xpath.indexOf(REQUEST_ELEMENT_NAME);
		if(firstIndex == -1){
			firstIndex = xpath.indexOf(RESOURCE_ELEMENT_NAME);
			if(firstIndex == -1){
				firstIndex = xpath.indexOf(RESOURCE_CONTENT_ELEMENT_NAME);
				if(firstIndex == -1){
					return xpath;
				}
			}
		}
		// found namespace prefix
		if(firstIndex > 0 && 
				buf.charAt(firstIndex - 1) == ':'){
			int index = xpath.indexOf("/");
			if(index == -1){
				firstIndex = 0;
			}
			else
			{
				firstIndex = index;
				while(xpath.charAt(index++) == '/'){
					firstIndex++;
				}
			}
		}
		int lastIndex = xpath.indexOf(RESOURCE_CONTENT_ELEMENT_NAME);
		if(lastIndex == -1){
			throw new IllegalArgumentException(
					String.format("Invalid XACML 2.0 xpath=\"%s\" " +
					"expression, \"ResourceContent\" is missing in the path", xpath));
		}
		lastIndex += RESOURCE_CONTENT_ELEMENT_NAME.length();
		buf.delete(firstIndex, lastIndex + 1);
		return buf.toString();
	}
}