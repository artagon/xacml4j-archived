package org.xacml4j.v30.spi.function;

import org.xacml4j.v30.pdp.FunctionSpec;
import org.xacml4j.v30.spi.function.FunctionSpecBuilder.FunctionSpecImpl;


public interface FunctionProvider 
{
	/**
	 * Gets function instance for a given function
	 * identifier.
	 * 
	 * @param functionId a function identifier
	 * @return {@link FunctionSpecImpl} instance for a given
	 * identifier or <code>null</code> if function
	 * can not be found for a given identifier
	 */
	FunctionSpec getFunction(String functionId);
	
	/**
	 * Tests if given function is supported by
	 * this factory
	 * 
	 * @param functionId a function identifier
	 * @return <code>true</code> if function
	 * is supported by this factory
	 */
	boolean isFunctionProvided(String functionId);
	
	/**
	 * Removes a function with a given identifier
	 * 
	 * @param functionId a function identifier
	 * @return {@link FunctionSpec} a reference
	 * to removed function or <code>null</code>
	 */
	FunctionSpec remove(String functionId);
	
	/**
	 * Gets all supported function by this factory
	 * 
	 * @return {@link Iterable} over all supported
	 * function identifiers 
	 */
	Iterable<String> getProvidedFunctions();
}