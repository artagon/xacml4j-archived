package com.artagon.xacml.v30.spi.pip;

import com.artagon.xacml.v30.AttributeCategory;

public interface ContentResolverDescriptor extends ResolverDescriptor
{
	/**
	 * Tests if given category content can be resolved
	 * via this resolver
	 * 
	 * @param category an attribute category
	 * @return <code>true</code> if content can be resolved
	 */
	boolean canResolve(AttributeCategory category);
}
