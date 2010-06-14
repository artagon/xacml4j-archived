package com.artagon.xacml.v3;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Target extends XacmlObject implements PolicyElement
{
	private final static Logger log = LoggerFactory.getLogger(Target.class);
	
	private Collection<MatchAnyOf> matches;
	
	/**
	 * Creates target with a collection
	 * of {@link MatchAnyOf}
	 * 
	 * @param matches a collection of {@link MatchAnyOf}
	 */
	public Target(Collection<MatchAnyOf> matches){
		this.matches = new LinkedList<MatchAnyOf>(matches);
	}
	
	public Target(){
		this(Collections.<MatchAnyOf>emptyList());
	}
	
	public Collection<MatchAnyOf> getAnyOf(){
		return Collections.unmodifiableCollection(matches);
	}
	
	public MatchResult match(EvaluationContext context) 
	{
		AttributeResolutionScope scope = context.getAttributeResolutionScope();
		context.setAttributeResolutionScope(AttributeResolutionScope.REQUEST);
		MatchResult state = MatchResult.MATCH;
		for(Matchable m : matches){
			MatchResult r = m.match(context);
			if(r == MatchResult.NOMATCH){
				state = r;
				log.debug("Found AnyOf with match result=\"{}\", " +
						"no need to evaluate target further", r);
				break;
			}
			if(r == MatchResult.INDETERMINATE){
				state = r;
				log.debug("Found AnyOf with match result=\"{}\", " +
						"no need to evaluate target further", r);
				break;
			}
		}
		context.setAttributeResolutionScope(scope);
		return state;
	}

	@Override
	public void accept(PolicyVisitor v) {
		v.visitEnter(this);
		for(Matchable m : matches){
			m.accept(v);
		}
		v.visitLeave(this);
	}
		
}