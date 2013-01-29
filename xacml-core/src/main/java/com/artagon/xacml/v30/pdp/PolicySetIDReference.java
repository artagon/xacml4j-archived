package com.artagon.xacml.v30.pdp;

import com.artagon.xacml.v30.CompositeDecisionRule;
import com.artagon.xacml.v30.CompositeDecisionRuleIDReference;
import com.artagon.xacml.v30.Decision;
import com.artagon.xacml.v30.EvaluationContext;
import com.artagon.xacml.v30.MatchResult;
import com.artagon.xacml.v30.PolicyResolutionException;
import com.artagon.xacml.v30.Version;
import com.artagon.xacml.v30.VersionMatch;
import com.artagon.xacml.v30.XacmlSyntaxException;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * A XACML {@link PolicySet} reference
 *
 * @author Giedrius Trumpickas
 */
public final class PolicySetIDReference extends BaseCompositeDecisionRuleIDReference
	implements PolicyElement
{

	public PolicySetIDReference(String id, VersionMatch version,
			VersionMatch earliest, VersionMatch latest) {
		super(id, version, earliest, latest);
	}

	public PolicySetIDReference(String id, VersionMatch version) {
		super(id, version, null, null);
	}

	public PolicySetIDReference(String id, Version version) {
		super(id, VersionMatch.parse(version.getValue()), null, null);
	}

	/**
	 * Creates {@link PolicySetIDReference}
	 *
	 * @param policyId a policy identifier
	 * @param version a policy version match
	 * @param earliest a policy earliest version match
	 * @param latest a policy latest version match
	 * @return {@link PolicySetIDReference} instance
	 * @throws XacmlSyntaxException if syntax error occurs
	 */
	public static PolicySetIDReference create(String policyId, String version,
			String earliest, String latest) throws XacmlSyntaxException
	{
		return new PolicySetIDReference(policyId,
				(version != null)?VersionMatch.parse(version):null,
				(earliest != null)?VersionMatch.parse(earliest):null,
				(latest != null)?VersionMatch.parse(latest):null);
	}

	/**
	 * Test this reference points to a given policy
	 *
	 * @param policy a policy
	 * @return <code>true</code> if a this reference
	 * points to a given policys
	 */
	@Override
	public boolean isReferenceTo(CompositeDecisionRule policy) {
		PolicySet p = (PolicySet)policy;
		return p != null &&
		matches(p.getId(), p.getVersion());
	}

	@Override
	public boolean equals(Object o){
		if(o == this){
			return true;
		}
		if(o == null){
			return false;
		}
		if(!(o instanceof PolicySetIDReference)){
			return false;
		}
		PolicySetIDReference r = (PolicySetIDReference)o;
		return getId().equals(r.getId())
		&& Objects.equal(getVersion(), r.getVersion())
		&& Objects.equal(getEarliestVersion(), r.getEarliestVersion())
		&& Objects.equal(getLatestVersion(), r.getLatestVersion());
	}

	/**
	 * Creates an {@link EvaluationContext} to evaluate this reference.
	 */
	@Override
	public EvaluationContext createContext(EvaluationContext context)
	{
		Preconditions.checkNotNull(context);
		if(context.getCurrentPolicySetIDReference() ==  this){
			return context;
		}
		PolicySetIDReferenceEvaluationContext refContext = new PolicySetIDReferenceEvaluationContext(context);
		try{
			CompositeDecisionRule policySet = refContext.resolve(this);
			if(policySet == null){
				return refContext;
			}
			return policySet.createContext(refContext);
		}catch(PolicyResolutionException e){
			return refContext;
		}
	}

	@Override
	public Decision evaluate(EvaluationContext context) {
		Preconditions.checkNotNull(context);
		Preconditions.checkArgument(context.getCurrentPolicySetIDReference() == this);
		if(!isReferenceTo(context.getCurrentPolicySet())){
			return Decision.INDETERMINATE;
		}
		CompositeDecisionRule ps = context.getCurrentPolicySet();
		Preconditions.checkState(ps != null);
		return ps.evaluate(context);
	}

	@Override
	public Decision evaluateIfMatch(EvaluationContext context) {
		Preconditions.checkNotNull(context);
		Preconditions.checkArgument(context.getCurrentPolicySetIDReference() == this);
		if(!isReferenceTo(context.getCurrentPolicySet())){
			return Decision.INDETERMINATE;
		}
		CompositeDecisionRule ps = context.getCurrentPolicySet();
		Preconditions.checkState(ps != null);
		return ps.evaluateIfMatch(context);
	}

	@Override
	public MatchResult isMatch(EvaluationContext context) {
		Preconditions.checkNotNull(context);
		Preconditions.checkArgument(context.getCurrentPolicySetIDReference() == this);
		if(!isReferenceTo(context.getCurrentPolicySet())){
			return MatchResult.INDETERMINATE;
		}
		CompositeDecisionRule ps = context.getCurrentPolicySet();
		Preconditions.checkState(ps != null);
		return ps.isMatch(context);
	}

	@Override
	public void accept(PolicyVisitor v) {
		v.visitEnter(this);
		v.visitLeave(this);
	}

	@Override
	public CompositeDecisionRuleIDReference getReference() {
		return this;
	}

	/**
	 * A static helper method to detect cyclic references
	 *
	 * @param ref a policy set id reference
	 * @param context an evaluation context
	 * @return <code>true</code> if a given reference
	 */
	private static boolean isReferenceCyclic(PolicySetIDReference ref,
			EvaluationContext context)
	{
		if(context.getCurrentPolicySetIDReference() != null){
			if(ref.equals(context.getCurrentPolicySetIDReference())){
				throw new IllegalStateException("Cyclic reference detected");
			}
			return isReferenceCyclic(ref, context.getParentContext());
		}
		return false;
	}

	class PolicySetIDReferenceEvaluationContext extends DelegatingEvaluationContext
	{
		PolicySetIDReferenceEvaluationContext(
				EvaluationContext context) {
			super(context);
			Preconditions.checkArgument(!isReferenceCyclic(PolicySetIDReference.this, context));
		}

		@Override
		public PolicySetIDReference getCurrentPolicySetIDReference() {
			return PolicySetIDReference.this;
		}
	}
}
