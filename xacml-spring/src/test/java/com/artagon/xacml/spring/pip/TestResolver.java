package com.artagon.xacml.spring.pip;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;

import com.artagon.xacml.v30.BagOfAttributeValues;
import com.artagon.xacml.v30.spi.pip.XacmlAttributeDescriptor;
import com.artagon.xacml.v30.spi.pip.XacmlAttributeDesignator;
import com.artagon.xacml.v30.spi.pip.XacmlAttributeResolverDescriptor;
import com.artagon.xacml.v30.spi.pip.XacmlContentResolverDescriptor;

public class TestResolver 
{
	@XacmlAttributeResolverDescriptor(id="testId", name="Test", category="subject", issuer="issuer", cacheTTL=30,
			attributes={
				@XacmlAttributeDescriptor(dataType="http://www.w3.org/2001/XMLSchema#integer", id="testId1"),
				@XacmlAttributeDescriptor(dataType="http://www.w3.org/2001/XMLSchema#boolean", id="testId2"),
				@XacmlAttributeDescriptor(dataType="http://www.w3.org/2001/XMLSchema#string", id="testId3"),
				@XacmlAttributeDescriptor(dataType="http://www.w3.org/2001/XMLSchema#double", id="testId4")
	})
	public Map<String, BagOfAttributeValues> resolveAttributes1(
			@XacmlAttributeDesignator(category="test", attributeId="attr1", 
					dataType="http://www.w3.org/2001/XMLSchema#boolean") BagOfAttributeValues k1, 
			@XacmlAttributeDesignator(category="test", attributeId="attr2", 
					dataType="http://www.w3.org/2001/XMLSchema#integer", issuer="test") BagOfAttributeValues k2)
	{
		return null;
	}
		
	@XacmlContentResolverDescriptor(id="testId", name="Test", category="subject", cacheTTL=30)
	public Node resolveContent1(@XacmlAttributeDesignator(category="test", attributeId="aaaTTr", 
			dataType="http://www.w3.org/2001/XMLSchema#boolean") BagOfAttributeValues k1)
	{
		return null;
	}
}