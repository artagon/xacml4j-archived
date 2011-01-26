package com.artagon.xacml.spring.pip;

import java.util.List;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class ResolverRegistryDefinitionParser extends AbstractBeanDefinitionParser
{
		
	@Override
	protected AbstractBeanDefinition parseInternal(Element element,
			ParserContext parserContext) {
		BeanDefinitionBuilder registry = BeanDefinitionBuilder.rootBeanDefinition(ResolverRegistryFactoryBean.class);
		List<Element> attributeResolvers = DomUtils.getChildElementsByTagName(element, "AttributeResolvers");
	    if(attributeResolvers != null && 
	    		attributeResolvers.size() == 1) {
	         parseAttributeResolvers(
	        		 DomUtils.getChildElementsByTagName(attributeResolvers.get(0), 
	        		 "AttributeResolver"), registry);
	    }
	    List<Element> contentResolvers = DomUtils.getChildElementsByTagName(element, "ContentResolvers");
	    if(contentResolvers != null && 
	    		attributeResolvers.size() == 1) {
	         parseContentResolvers(
	        		 DomUtils.getChildElementsByTagName(contentResolvers.get(0), 
	        		 "ContentResolver"), registry);
	    }
	    return registry.getBeanDefinition();
	}
	
	private static BeanDefinitionBuilder parseAttributeResolvers(Element element) 
	{
	      BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(
	    		  AttributeResolverRegistrationFactoryBean.class);
	      String policyId = element.getAttribute("policyId");
	      if(StringUtils.hasText(policyId)){
	    	  component.addPropertyValue("policyId", policyId);
	      }
	      String ref = element.getAttribute("ref");
	      if(StringUtils.hasText(ref)){
	    	  component.addPropertyReference("resolver", ref);
	      }
	      return component;
	}
	
	private static BeanDefinitionBuilder parseContentResolvers(Element element) 
	{
	      BeanDefinitionBuilder component = BeanDefinitionBuilder.rootBeanDefinition(
	    		  ContentResolverRegistrationFactoryBean.class);
	      String policyId = element.getAttribute("policyId");
	      if(StringUtils.hasText(policyId)){
	    	  component.addPropertyValue("policyId", policyId);
	      }
	      String ref = element.getAttribute("ref");
	      if(StringUtils.hasText(ref)){
	    	  component.addPropertyReference("resolver", ref);
	      }
	      return component;
	}
	
	private static void parseAttributeResolvers(List<Element> childElements, BeanDefinitionBuilder factory) 
	{
		ManagedList<BeanDefinition> children = new ManagedList<BeanDefinition>(childElements.size());
	    for (int i = 0; i < childElements.size(); ++i) {
	         Element childElement = (Element) childElements.get(i);
	         BeanDefinitionBuilder child = parseAttributeResolvers(childElement);
	         children.add(child.getBeanDefinition());
	    }
	    factory.addPropertyValue("attributeResolvers", children);
	}
	
	private static void parseContentResolvers(List<Element> childElements, BeanDefinitionBuilder factory) 
	{
		ManagedList<BeanDefinition> children = new ManagedList<BeanDefinition>(childElements.size());
	    for (int i = 0; i < childElements.size(); ++i) {
	         Element childElement = (Element) childElements.get(i);
	         BeanDefinitionBuilder child = parseContentResolvers(childElement);
	         children.add(child.getBeanDefinition());
	    }
	    factory.addPropertyValue("contentResolvers", children);
	}
}
