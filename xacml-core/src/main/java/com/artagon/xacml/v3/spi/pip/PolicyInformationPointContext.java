package com.artagon.xacml.v3.spi.pip;

import com.artagon.xacml.v3.AttributeValue;

public interface PolicyInformationPointContext 
{
	AttributeValue getCurrentTime();
	AttributeValue getCurrentDateTime();
	AttributeValue getCurrentDate();
}