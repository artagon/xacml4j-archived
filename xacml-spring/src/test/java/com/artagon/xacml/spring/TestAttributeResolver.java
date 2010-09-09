package com.artagon.xacml.spring;

import org.junit.Ignore;

import com.artagon.xacml.v3.AttributeCategoryId;
import com.artagon.xacml.v3.AttributeDesignator;
import com.artagon.xacml.v3.AttributeValue;
import com.artagon.xacml.v3.BagOfAttributeValues;
import com.artagon.xacml.v3.RequestContextAttributesCallback;
import com.artagon.xacml.v3.spi.pip.AttributeResolverDescriptorBuilder;
import com.artagon.xacml.v3.spi.pip.BaseAttributeResolver;
import com.artagon.xacml.v3.spi.pip.PolicyInformationPointContext;
import com.artagon.xacml.v3.types.XacmlDataTypes;

@Ignore
public class TestAttributeResolver extends BaseAttributeResolver
{
	public TestAttributeResolver(){
		super(AttributeResolverDescriptorBuilder.create().
				attribute(AttributeCategoryId.ENVIRONMENT, "test", XacmlDataTypes.STRING.getType()).build());
	}
	
	@Override
	protected BagOfAttributeValues<AttributeValue> doResolve(
			PolicyInformationPointContext context, AttributeDesignator ref,
			RequestContextAttributesCallback callback) {
		return null;
	}
	
}
