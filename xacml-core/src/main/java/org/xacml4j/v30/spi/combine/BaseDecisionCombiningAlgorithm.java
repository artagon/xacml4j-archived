package org.xacml4j.v30.spi.combine;

import org.xacml4j.v30.DecisionRule;
import org.xacml4j.v30.pdp.DecisionCombiningAlgorithm;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public abstract class BaseDecisionCombiningAlgorithm <D extends DecisionRule>
	implements DecisionCombiningAlgorithm <D>
{
	private String algorithmId;

	/**
	 * Creates decision combining algorithm with a
	 * given algorithm identifier
	 * @param algorithmId an algorithm identifier
	 */
	protected BaseDecisionCombiningAlgorithm(String algorithmId){
		Preconditions.checkNotNull(algorithmId);
		this.algorithmId = algorithmId;
	}

	/**
	 * Gets decision algorithm identifier
	 *
	 * @return decision algorithm identifier
	 */
	@Override
	public final String getId(){
		return algorithmId;
	}

	@Override
	public final int hashCode(){
		return algorithmId.hashCode();
	}

	@Override
	public final boolean equals(Object o){
		if(o == this){
			return true;
		}
		if(o == null){
			return false;
		}
		@SuppressWarnings("unchecked")
		BaseDecisionCombiningAlgorithm<D> a = (BaseDecisionCombiningAlgorithm<D>)o;
		return algorithmId.equals(a.algorithmId);
	}

	@Override
	public final String toString(){
		return Objects
				.toStringHelper(this)
				.add("algorithmId", algorithmId)
				.toString();
	}
}