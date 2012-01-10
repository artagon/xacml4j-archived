package com.artagon.xacml.v30.pdp;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

abstract class BaseDecisionRule extends XacmlObject implements DecisionRule
{
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	private String description;
	private Target target;
	private Collection<AdviceExpression> adviceExpressions;
	private Collection<ObligationExpression> obligationExpressions;
	
	/**
	 * Constructs base decision with a given identifier
	 * 
	 * @param id an decision identifier
	 * @param description a decision rule description
	 * @param target a a decision target
	 * @param adviceExpressions a decision 
	 * advice expressions
	 * @param obligationExpressions a decision 
	 * obligation expressions
	 */
	protected BaseDecisionRule( 
			String description,
			Target target, 
			Collection<AdviceExpression> adviceExpressions,
			Collection<ObligationExpression> obligationExpressions){
		this.description = description;
		this.target = target;
		this.adviceExpressions = (adviceExpressions != null)?
				ImmutableList.copyOf(adviceExpressions):ImmutableList.<AdviceExpression>of();
		this.obligationExpressions = (obligationExpressions != null)?
				ImmutableList.copyOf(obligationExpressions):ImmutableList.<ObligationExpression>of();
	}
	
	protected BaseDecisionRule(
			Target target, 
			Collection<AdviceExpression> adviceExpressions,
			Collection<ObligationExpression> obligationExpressions){
		this(null, target, adviceExpressions, obligationExpressions);
	}
	
	protected BaseDecisionRule(
			Target target){
		this(null, target, 
				Collections.<AdviceExpression>emptyList(), 
				Collections.<ObligationExpression>emptyList());
	}
	
	/**
	 * Gets decision rule description
	 * 
	 * @return decision rule description
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * Gets decision rule target
	 * 
	 * @return {@link Target} or <code>null</code>
	 * if rule is applicable to any request
	 */
	public Target getTarget(){
		return target;
	}
	
	/**
	 * Gets decision rule collection of {@link ObligationExpression}
	 * expressions
	 * 
	 * @return a collection of {@link ObligationExpression}
	 */
	public Collection<ObligationExpression> getObligationExpressions(){
		return obligationExpressions;
	}
	
	/**
	 * Gets decision rule collection of {@link AdviceExpression}
	 * expressions
	 * 
	 * @return a collection of {@link AdviceExpression}
	 */
	public Collection<AdviceExpression> getAdviceExpressions(){
		return adviceExpressions;
	}
	
	/**
	 * Testing if this decision rule is applicable to
	 * the current evaluation context
	 * 
	 * @param context an evaluation context
	 * @return {@link MatchResult} a match result
	 */
	public MatchResult isApplicable(EvaluationContext context){ 
		Preconditions.checkArgument(
				isEvaluationContextValid(context));
		return (target == null)?MatchResult.MATCH:target.match(context);
	}
	
	/**
	 * Evaluates all matching to the given
	 * decision rule advice and obligations
	 * 
	 * @param context an evaluation context
	 * @param result a rule evaluation result
	 * @return a rule evaluation result or {@link Decision#INDETERMINATE}
	 * if any of the advice or obligation evaluation fails
	 */
	protected final Decision evaluateAdvicesAndObligations(
			EvaluationContext context, Decision result)
	{
		try
		{
			if(result.isIndeterminate() || 
					result == Decision.NOT_APPLICABLE){
				return result;
			}
			Collection<Advice> advices = evaluateAdvices(context, result);
			Collection<Obligation> obligations = evaluateObligations(context, result);
			if(!advices.isEmpty()){
				context.addAdvices(advices);
			}
			if(!obligations.isEmpty()){
				context.addObligations(obligations);
			}
			return result;
		}catch(Exception e){
			return Decision.INDETERMINATE;
		}
	}
	
	/**
	 * Evaluates advice expressions matching given decision
	 * {@link Decision} result
	 * 
	 * @param context an evaluation context
	 * @param result a decision evaluation result
	 * @return collection of {@link Advice} instances
	 * @throws EvaluationException if an evaluation error occurs
	 */
	private Collection<Advice> evaluateAdvices(EvaluationContext context, Decision result) 
		throws EvaluationException
	{
		if(log.isDebugEnabled()){
			log.debug("Evaluating advices " +
					"for descision rule id=\"{}\"", getId());
		}
		Collection<Advice> advices = new LinkedList<Advice>();
		try{
			for(AdviceExpression adviceExp : adviceExpressions){
				if(adviceExp.isApplicable(result)){
					if(log.isDebugEnabled()){
						log.debug("Evaluating advice id=\"{}\"", 
								adviceExp.getId());
					}
					Advice a = adviceExp.evaluate(context);
					Preconditions.checkState(a != null);
					advices.add(a);
				}
			}
			if(log.isDebugEnabled()){
				log.debug("Evaluated=\"{}\" applicable advices", 
						advices.size());
			}
			return advices;
		}catch(EvaluationException e){
			if(log.isDebugEnabled()){
				log.debug("Failed to evaluate " +
						"decision rule advices", e);
			}
			throw e;
		}catch(Exception e){
			if(log.isDebugEnabled()){
				log.debug("Failed to evaluate " +
						"decision rule advices", e);
			}
			throw new EvaluationException(
					StatusCode.createProcessingError(), 
					context, e);
		}
	}
	
	/**
	 * Evaluates obligation matching given decision 
	 * {@link Decision} result
	 * 
	 * @param context an evaluation context
	 * @param result an decision result
	 * @return collection of {@link Obligation} instances
	 * @throws EvaluationException if an evaluation error occurs
	 */
	private Collection<Obligation> evaluateObligations(EvaluationContext context, Decision result) 
		throws EvaluationException
	{
		if(log.isDebugEnabled()){
			log.debug("Evaluating obligations " +
					"for descision rule id=\"{}\"", getId());
		}
		Collection<Obligation> obligations = new LinkedList<Obligation>();
		try{
			for(ObligationExpression obligationExp : obligationExpressions){
				if(obligationExp.isApplicable(result)){
					if(log.isDebugEnabled()){
						log.debug("Evaluating obligation id=\"{}\"", 
								obligationExp.getId());
					}
					Obligation o  = obligationExp.evaluate(context);
					Preconditions.checkState(o != null);
					obligations.add(o);
				}
			}
			if(log.isDebugEnabled()){
				log.debug("Evaluated=\"{}\" applicable obligations", 
						obligations.size());
			}
			return obligations;
		}catch(EvaluationException e){
			if(log.isDebugEnabled()){
				log.debug("Failed to evaluate " +
						"decision rule obligations", e);
			}
			throw e;
		}catch(Exception e){
			if(log.isDebugEnabled()){
				log.debug("Failed to evaluate " +
						"decision rule obligations", e);
			}
			throw new EvaluationException(
					StatusCode.createProcessingError(), 
					context, e);
		}
	}
	
	protected abstract boolean isEvaluationContextValid(EvaluationContext context);
	
	
	public abstract static class BaseBuilder<T extends BaseBuilder<?>>
	{
		protected String id;
		protected String description;
		protected Target target;	
		protected Collection<AdviceExpression> adviceExpressions = new LinkedList<AdviceExpression>();
		protected Collection<ObligationExpression> obligationExpressions = new LinkedList<ObligationExpression>();
		
		protected BaseBuilder(){
		}
		
		public T withId(String id){
			Preconditions.checkNotNull(id);
			this.id = id;
			return getThis();
		}
		
		public T withDescription(String desc){
			Preconditions.checkNotNull(desc);
			this.description = desc;
			return getThis();
		}
		
		public T withTarget(Target target){
			Preconditions.checkNotNull(target);
			this.target = target;
			return getThis();
		}
		
		public T withTarget(Target.Builder b){
			Preconditions.checkNotNull(b);
			this.target = b.build();
			return getThis();
		}
		
		public T withoutTarget(){
			this.target = null;
			return getThis();
		}
		
		public T withAdvice(AdviceExpression advice){
			Preconditions.checkNotNull(advice);
			this.adviceExpressions.add(advice);
			return getThis();
		}
		
		public T withoutAdvices(){
			this.adviceExpressions.clear();
			return getThis();
		}
		
		public T withoutObligations(){
			this.obligationExpressions.clear();
			return getThis();
		}
		
		public T withAdvice(AdviceExpression.Builder b){
			Preconditions.checkNotNull(b);
			this.adviceExpressions.add(b.build());
			return getThis();
		}
		
		public T withObligation(ObligationExpression advice){
			Preconditions.checkNotNull(advice);
			this.obligationExpressions.add(advice);
			return getThis();
		}
		
		public T withObligation(ObligationExpression.Builder b){
			Preconditions.checkNotNull(b);
			this.obligationExpressions.add(b.build());
			return getThis();
		}
		
		protected abstract T getThis();
	}
}