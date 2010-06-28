package com.artagon.xacml.v3.spi;

import org.w3c.dom.Node;

import com.artagon.xacml.v3.AttributeCategoryId;
import com.artagon.xacml.v3.AttributeDesignator;
import com.artagon.xacml.v3.AttributeValue;
import com.artagon.xacml.v3.EvaluationContext;
import com.artagon.xacml.v3.RequestAttributesCallback;
import com.artagon.xacml.v3.BagOfAttributeValues;

public interface PolicyInformationPoint 
{
	BagOfAttributeValues<? extends AttributeValue> resolve(
			EvaluationContext context, 
			AttributeDesignator ref, 
			RequestAttributesCallback callback);
	
	Node resolve(
			EvaluationContext context, 
			AttributeCategoryId categoryId, 
			RequestAttributesCallback callback);
}
